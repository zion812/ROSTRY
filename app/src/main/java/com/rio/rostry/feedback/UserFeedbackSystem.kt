package com.rio.rostry.feedback

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserFeedbackSystem @Inject constructor() {
    data class Feedback(val id: String, val rating: Int, val comment: String?)
    suspend fun submit(feedback: Feedback) {}
}
