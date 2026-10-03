package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.splash.SplashScreen
import com.example.ui.theme.HiraTheme
import com.example.ui.update.UpdateBottomSheet
import com.example.ui.update.UpdateViewModel

class MainActivity : ComponentActivity() {

    private val updateViewModel: UpdateViewModel by viewModels()

    @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Gestion d'un éventuel Deep link (ex: notification push hira://update)
        handleIntent(intent)

        setContent {
            HiraTheme {
                val updateState by updateViewModel.updateState.collectAsStateWithLifecycle()

                // Vérification automatique au démarrage (avec throttling intégré)
                LaunchedEffect(Unit) {
                    updateViewModel.checkForUpdates(force = false)
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    // Écran principal (Splash Screen pour cette première étape)
                    SplashScreen()

                    // Interface modale de mise à jour globale superposée
                    UpdateBottomSheet(
                        state = updateState,
                        onDownloadClick = { updateInfo ->
                            updateViewModel.startDownload(updateInfo)
                        },
                        onInstallClick = { apkFile ->
                            updateViewModel.installUpdate(apkFile)
                        },
                        onDismiss = {
                            updateViewModel.dismiss()
                        },
                        onRequestPermission = {
                            updateViewModel.requestInstallPermission()
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        // Vérification lors du retour au premier plan (avec throttling 1h)
        updateViewModel.checkForUpdates(force = false)
    }

    private fun handleIntent(intent: Intent?) {
        val data = intent?.data
        if (data?.scheme == "hira" && data.host == "update") {
            // Déclenchement forcé de la mise à jour via deep link
            updateViewModel.checkForUpdates(force = true)
        }
    }
}
