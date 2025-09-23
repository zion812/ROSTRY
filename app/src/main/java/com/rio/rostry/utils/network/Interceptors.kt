package com.rio.rostry.utils.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// Adds offline cache when network is unavailable (up to 7 days stale)
@Singleton
class OfflineCacheInterceptor @Inject constructor(
    private val context: Context,
    private val network: NetworkQualityMonitor,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val noNetwork = !network.isOnline()
        if (noNetwork) {
            val maxStale = 60 * 60 * 24 * 7 // 7 days
            request = request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .build()
        }
        return chain.proceed(request)
    }
}

// Adds short-lived online cache (e.g., 5 minutes) for GETs
@Singleton
class OnlineCacheInterceptor @Inject constructor(
    private val network: NetworkQualityMonitor,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val online = network.isOnline()
        val maxAge = if (online) 60 * 5 else 0 // 5 minutes
        return response.newBuilder()
            .header("Cache-Control", "public, max-age=$maxAge")
            .build()
    }
}

// Simple retry with exponential backoff for idempotent GET/HEAD
@Singleton
class RetryInterceptor @Inject constructor(
    private val network: NetworkQualityMonitor,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var tryCount = 0
        val maxTries = 3
        var wait = 250L
        var lastError: IOException? = null
        while (tryCount < maxTries) {
            try {
                return chain.proceed(chain.request())
            } catch (e: IOException) {
                lastError = e
                tryCount++
                if (tryCount >= maxTries || !network.isOnline()) break
                try { Thread.sleep(wait) } catch (_: InterruptedException) {}
                wait *= 2
            }
        }
        throw lastError ?: IOException("Network error")
    }
}

// Tracks rough data usage for requests/responses
@Singleton
class DataUsageInterceptor @Inject constructor(
    private val tracker: DataUsageTracker
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val reqBytes = req.body?.contentLength() ?: 0L
        tracker.addUpload(req.url.host, reqBytes)
        val resp = chain.proceed(req)
        val respBytes = resp.body?.contentLength() ?: 0L
        tracker.addDownload(req.url.host, respBytes)
        return resp
    }
}
