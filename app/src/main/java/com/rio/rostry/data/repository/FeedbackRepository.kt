package com.rio.rostry.data.repository

import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FeedbackRepository {
    suspend fun submitFeedback(userId: String, content: String, type: String): Resource<Boolean>
}
