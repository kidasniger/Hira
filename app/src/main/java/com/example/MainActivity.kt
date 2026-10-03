package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.auth.AuthRepository
import com.example.data.local.OnboardingPreferences
import com.example.navigation.HiraRoutes
import com.example.ui.auth.AuthNavigationEvent
import com.example.ui.auth.AuthViewModel
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.RegisterScreen
import com.example.ui.contacts.ContactsPermissionScreen
import com.example.ui.contacts.ContactsScreen
import com.example.ui.chat.PrivateChatScreen
import com.example.ui.discussions.DiscussionsScreen
import com.example.ui.profile.ProfileSetupScreen
import com.example.ui.onboarding.OnboardingScreen1
import com.example.ui.onboarding.OnboardingScreen2
import com.example.ui.onboarding.OnboardingScreen3
import com.example.ui.splash.SplashScreen
import com.example.ui.theme.HiraTheme
import com.example.ui.update.UpdateBottomSheet
import com.example.ui.update.UpdateViewModel

class MainActivity : ComponentActivity() {

    private val updateViewModel: UpdateViewModel by viewModels()
    private val authRepository = AuthRepository()
    private val authViewModel: AuthViewModel by viewModels()

    @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Gestion d'un éventuel Deep link (ex: notification push hira://update)
        handleIntent(intent)

        setContent {
            HiraTheme {
                val updateState by updateViewModel.updateState.collectAsStateWithLifecycle()
                val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
                val onboardingPreferences = remember { OnboardingPreferences(applicationContext) }
                var currentRoute by rememberSaveable { mutableStateOf(HiraRoutes.SPLASH) }

                // Données Google transmises à l'écran 7 pour préremplir le profil.
                var profileDisplayName by rememberSaveable { mutableStateOf("") }
                var profilePhotoUrl by rememberSaveable { mutableStateOf<String?>(null) }
                var selectedChatContactName by rememberSaveable { mutableStateOf<String?>(null) }

                // Écoute de l'événement de navigation après authentification réussie
                // Consommé une seule fois grâce au Channel
                LaunchedEffect(authViewModel) {
                    authViewModel.navigationEvent.collect { event ->
                        when (event) {
                            is AuthNavigationEvent.NavigateSuccess -> {
                                if (event.user.isNewUser) {
                                    profileDisplayName = event.user.displayName.orEmpty()
                                    profilePhotoUrl = event.user.photoUrl
                                    currentRoute = HiraRoutes.CONFIG_PROFIL
                                } else {
                                    currentRoute = HiraRoutes.DISCUSSIONS
                                }
                            }
                        }
                    }
                }

                // Vérification automatique au démarrage (avec throttling intégré)
                LaunchedEffect(Unit) {
                    updateViewModel.checkForUpdates(force = false)
                }

                Box(modifier = Modifier.fillMaxSize()) {
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
                                        currentRoute = when {
                                            authRepository.isSignedIn() -> HiraRoutes.DISCUSSIONS
                                            onboardingPreferences.isOnboardingCompleted() -> HiraRoutes.CONNEXION
                                            else -> HiraRoutes.ACCUEIL_1
                                        }
                                    }
                                )
                            }
                            HiraRoutes.ACCUEIL_1 -> {
                                OnboardingScreen1(
                                    onNextClick = { currentRoute = HiraRoutes.ACCUEIL_2 },
                                    onSkipClick = { currentRoute = HiraRoutes.ACCUEIL_3 },
                                    onBack = { finish() }
                                )
                            }
                            HiraRoutes.ACCUEIL_2 -> {
                                OnboardingScreen2(
                                    onNextClick = { currentRoute = HiraRoutes.ACCUEIL_3 },
                                    onSkipClick = { currentRoute = HiraRoutes.ACCUEIL_3 },
                                    onBackClick = { currentRoute = HiraRoutes.ACCUEIL_1 }
                                )
                            }
                            HiraRoutes.ACCUEIL_3 -> {
                                OnboardingScreen3(
                                    onFinishClick = {
                                        onboardingPreferences.setOnboardingStep(3)
                                        onboardingPreferences.setOnboardingCompleted(true)
                                        currentRoute = HiraRoutes.CONNEXION
                                    },
                                    onBackClick = { currentRoute = HiraRoutes.ACCUEIL_2 }
                                )
                            }
                            HiraRoutes.CONNEXION -> {
                                LoginScreen(
                                    viewModel = authViewModel,
                                    uiState = authUiState,
                                    onNavigateToRegister = {
                                        authViewModel.clearErrors()
                                        currentRoute = HiraRoutes.CREATION_COMPTE
                                    },
                                    onBack = { finish() }
                                )
                            }
                            HiraRoutes.CREATION_COMPTE -> {
                                RegisterScreen(
                                    viewModel = authViewModel,
                                    uiState = authUiState,
                                    onBackToLogin = {
                                        authViewModel.clearErrors()
                                        currentRoute = HiraRoutes.CONNEXION
                                    }
                                )
                            }
                            HiraRoutes.DISCUSSIONS -> {
                                DiscussionsScreen(
                                    onContactsClick = {
                                        currentRoute = HiraRoutes.CONTACTS
                                    }
                                )
                            }
                            HiraRoutes.CONTACTS -> {
                                ContactsScreen(
                                    onDiscussionsClick = {
                                        currentRoute = HiraRoutes.DISCUSSIONS
                                    },
                                    onInviteClick = {
                                        // Le parcours de téléchargement HIRA sera ajouté sur un écran dédié ultérieurement.
                                    },
                                    onContactClick = {
                                        // L'ouverture sera activée après vérification réelle du compte HIRA du contact.
                                    }
                                )
                            }
                            HiraRoutes.CHAT_PRIVE -> {
                                PrivateChatScreen(
                                    contactName = selectedChatContactName,
                                    onBack = {
                                        currentRoute = HiraRoutes.CONTACTS
                                    },
                                    onAttachmentClick = {
                                        // L'écran 12 sera branché ici ultérieurement.
                                    },
                                    onSendMessage = {
                                        // L'envoi réel sera branché au service de messagerie.
                                    }
                                )
                            }
                            HiraRoutes.CONFIG_PROFIL -> {
                                ProfileSetupScreen(
                                    initialDisplayName = profileDisplayName,
                                    initialPhotoUrl = profilePhotoUrl,
                                    onFinish = { _, _ ->
                                        currentRoute = HiraRoutes.PERMISSION_CONTACTS
                                    }
                                )
                            }
                            HiraRoutes.PERMISSION_CONTACTS -> {
                                ContactsPermissionScreen(
                                    onPermissionGranted = {
                                        currentRoute = HiraRoutes.DISCUSSIONS
                                    },
                                    onSkip = {
                                        currentRoute = HiraRoutes.DISCUSSIONS
                                    }
                                )
                            }
                        }
                    }

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
        updateViewModel.checkForUpdates(force = false)
    }

    private fun handleIntent(intent: Intent?) {
        val data = intent?.data
        if (data?.scheme == "hira" && data.host == "update") {
            updateViewModel.checkForUpdates(force = true)
        }
    }
}
