package com.rio.rostry

import android.app.Application
import com.google.firebase.FirebaseApp

class ROSTRYApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}