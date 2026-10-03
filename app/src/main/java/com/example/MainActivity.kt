package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.navigation.HiraRoutes
import com.example.ui.onboarding.OnboardingScreen1
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
                var currentRoute by rememberSaveable { mutableStateOf(HiraRoutes.SPLASH) }

                // Vérification automatique au démarrage (avec throttling intégré)
                LaunchedEffect(Unit) {
                    updateViewModel.checkForUpdates(force = false)
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    // Navigation fluide et légère adaptée à tous les téléphones
                    AnimatedContent(
                        targetState = currentRoute,
                        transitionSpec = {
                            fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) togetherWith
                                fadeOut(animationSpec = androidx.compose.animation.core.tween(200))
                        },
                        label = "hira_screen_transition"
                    ) { route ->
                        when (route) {
                            HiraRoutes.SPLASH -> {
                                SplashScreen(
                                    onSplashFinished = {
                                        // Transition normale vers Accueil 1/3
                                        currentRoute = HiraRoutes.ACCUEIL_1
                                    }
                                )
                            }
                            HiraRoutes.ACCUEIL_1 -> {
                                OnboardingScreen1(
                                    onNextClick = {
                                        // Cible de navigation vers Accueil 2/3
                                        currentRoute = HiraRoutes.ACCUEIL_2
                                    },
                                    onSkipClick = {
                                        currentRoute = HiraRoutes.ACCUEIL_2
                                    },
                                    onBack = {
                                        // Sortie propre sans retour en boucle vers le Splash
                                        finish()
                                    }
                                )
                            }
                            HiraRoutes.ACCUEIL_2 -> {
                                // Route cible déclarée mais interface non implémentée selon les consignes strictes
                                BackHandler {
                                    currentRoute = HiraRoutes.ACCUEIL_1
                                }
                            }
                        }
                    }

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
