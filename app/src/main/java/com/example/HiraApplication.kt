package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class HiraApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeFirebase()
    }

    private fun initializeFirebase() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val app = FirebaseApp.initializeApp(this)
                if (app != null) {
                    Log.i(TAG, "FirebaseApp initialisé avec succès depuis les ressources")
                } else {
                    fallbackInitialize()
                }
            } else {
                Log.i(TAG, "FirebaseApp déjà initialisé")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Initialisation standard de FirebaseApp échouée: ${e.message}, bascule vers fallback explicite")
            fallbackInitialize()
        }
    }

    private fun fallbackInitialize() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:129923565999:android:f65c07cce87001aae4475a")
                    .setApiKey("AIzaSyAO8qEzAjd0-tLXcMLqrAJ3819ZXMboi2o")
                    .setProjectId("hira-app-chat")
                    .setStorageBucket("hira-app-chat.firebasestorage.app")
                    .setGcmSenderId("129923565999")
                    .build()
                FirebaseApp.initializeApp(this, options)
                Log.i(TAG, "FirebaseApp initialisé avec succès avec les options explicites de google-services.json")
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Erreur fatale lors de l'initialisation de repli de FirebaseApp", ex)
        }
    }

    companion object {
        private const val TAG = "HiraApplication"
    }
}
