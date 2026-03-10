package com.rio.rostry.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import android.os.Handler
import android.os.Looper
import com.rio.rostry.BuildConfig
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressSelectionWebViewScreen(
    onBack: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Address Selection", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AddressSelectionWebView(
                context = context,
                onSubmit = onSubmit,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun AddressSelectionWebView(
    context: Context,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                webChromeClient = WebChromeClient()
                addJavascriptInterface(AddressJsBridge(onSubmit), "Android")
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        super.onPageFinished(view, url)
                        val js = (
                            "(function(){" +
                            "var btn=document.querySelector('gmpx-icon-button');" +
                            "if(btn){btn.addEventListener('click',function(){" +
                            // collect lat/lng from advanced marker or map center
                            "var mapEl=document.querySelector('gmp-map');" +
                            "var markerEl=document.querySelector('gmp-advanced-marker');" +
                            "var pos=(markerEl&&markerEl.position)?markerEl.position:(mapEl&&mapEl.center?mapEl.center:null);" +
                            "var plat=pos?(typeof pos.lat==='function'?pos.lat():pos.lat):'';" +
                            "var plng=pos?(typeof pos.lng==='function'?pos.lng():pos.lng):'';" +
                            "var payload={" +
                            "location:document.getElementById('location-input')?.value||''," +
                            "locality:document.getElementById('locality-input')?.value||''," +
                            "administrative_area_level_1:document.getElementById('administrative_area_level_1-input')?.value||''," +
                            "postal_code:document.getElementById('postal_code-input')?.value||''," +
                            "country:document.getElementById('country-input')?.value||''," +
                            "lat:plat||'',lng:plng||''" +
                            "};" +
                            "if(window.Android&&Android.onSubmit){Android.onSubmit(JSON.stringify(payload));}" +
                            "});}" +
                            "})();"
                        )
                        evaluateJavascript(js, null)
                    }
                }

                val html = ctx.assets.open("address_selection.html").bufferedReader().use { it.readText() }
                // Use dedicated JS key when available; fall back to native Maps key as a backup.
                val jsKey = try {
                    BuildConfig.MAPS_JS_API_KEY
                } catch (_: Throwable) {
                    BuildConfig.MAPS_API_KEY
                }
                val patched = html.replace("YOUR_API_KEY_HERE", jsKey)
                loadDataWithBaseURL(
                    "https://rostry.local/",
                    patched,
                    "text/html",
                    "utf-8",
                    null
                )
            }
        }
    )
}

private class AddressJsBridge(private val onSubmit: (String) -> Unit) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun onSubmit(json: String) {
        val payload = try {
            // Basic validation that it's JSON; pass upstream as-is to keep flexibility
            JSONObject(json)
            json
        } catch (_: Throwable) {
            "{}"
        }
        mainHandler.post {
            onSubmit(payload)
        }
    }
}
