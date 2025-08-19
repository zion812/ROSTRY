package com.rio.rostry

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.rio.rostry.ui.theme.ROSTRYTheme
import com.rio.rostry.utils.FirebaseTestUtil
import kotlinx.coroutines.launch

class FirebaseTestActivity : ComponentActivity() {
    private val TAG = "FirebaseTestActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "FirebaseTestActivity onCreate")
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        Log.d(TAG, "Firebase initialized")
        
        setContent {
            ROSTRYTheme {
                FirebaseTestScreen()
            }
        }
    }
}

@Composable
fun FirebaseTestScreen() {
    val scope = rememberCoroutineScope()
    var testResult by remember { mutableStateOf<String?>(null) }
    var isTesting by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Firebase Connection Test",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                isTesting = true
                testResult = null
                scope.launch {
                    val result = FirebaseTestUtil.testFirebaseConnection()
                    testResult = if (result) "SUCCESS: Firebase is properly configured!" else "FAILURE: Firebase connection failed!"
                    isTesting = false
                }
            },
            enabled = !isTesting
        ) {
            if (isTesting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Testing...")
            } else {
                Text("Test Firebase Connection")
            }
        }
        
        testResult?.let { result ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = if (result.startsWith("SUCCESS")) {
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                } else {
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                }
            ) {
                Text(
                    text = result,
                    modifier = Modifier.padding(16.dp),
                    color = if (result.startsWith("SUCCESS")) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Display Firebase Auth info
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Firebase Auth Info:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Current User: ${currentUser?.email ?: "None"}")
                Text("User UID: ${currentUser?.uid ?: "N/A"}")
            }
        }
    }
}