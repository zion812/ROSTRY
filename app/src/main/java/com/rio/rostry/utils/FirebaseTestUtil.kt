package com.rio.rostry.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseTestUtil {
    private const val TAG = "FirebaseTestUtil"
    
    suspend fun testFirebaseConnection(): Boolean {
        return try {
            Log.d(TAG, "Testing Firebase Authentication connection")
            val auth = FirebaseAuth.getInstance()
            Log.d(TAG, "Firebase Auth instance: $auth")
            
            Log.d(TAG, "Testing Firebase Firestore connection")
            val firestore = FirebaseFirestore.getInstance()
            Log.d(TAG, "Firebase Firestore instance: $firestore")
            
            // Test a simple Firestore operation
            val testCollection = firestore.collection("test")
            val testDoc = testCollection.document("connection_test")
            testDoc.set(mapOf("test" to "value")).await()
            Log.d(TAG, "Firestore write test successful")
            
            // Clean up test document
            testDoc.delete().await()
            Log.d(TAG, "Test document cleaned up")
            
            Log.d(TAG, "Firebase connection test successful")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Firebase connection test failed", e)
            false
        }
    }
}