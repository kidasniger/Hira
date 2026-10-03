package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

/**
 * Application principale pour HIRA.
 * Initialise Firebase à partir du fichier google-services.json traité par le plugin Google Services.
 */
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
                    Log.i(TAG, "FirebaseApp initialisé avec succès depuis google-services.json (Projet: ${app.options.projectId})")
                } else {
                    Log.w(TAG, "FirebaseApp.initializeApp a retourné null. Vérifiez que google-services.json est présent dans app/")
                }
            } else {
                Log.i(TAG, "FirebaseApp déjà initialisé automatiquement via FirebaseInitProvider")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de l'initialisation de FirebaseApp depuis google-services.json: ${e.message}", e)
        }
    }

    companion object {
        private const val TAG = "HiraApplication"
    }
}
