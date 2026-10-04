package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.activity.compose.rememberLauncherForActivityResult
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
import com.example.ui.chat.AttachmentMenuScreen
import com.example.ui.chat.LargeFileDownloadScreen
import com.example.ui.chat.AttachmentAction
import com.example.ui.chat.PrivateChatScreen
import com.example.ui.groups.GroupChatScreen
import com.example.ui.groups.GroupContact
import com.example.ui.groups.NewGroupScreen
import com.example.ui.discussions.DiscussionsScreen
import com.example.ui.profile.ProfileScreen
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
                var routeHistory by rememberSaveable { mutableStateOf(listOf(HiraRoutes.SPLASH)) }
                var lastBackPressAt by rememberSaveable { mutableStateOf(0L) }

                fun goTo(route: String) {
                    if (route == currentRoute) return
                    if (route == HiraRoutes.DISCUSSIONS) {
                        routeHistory = listOf(HiraRoutes.DISCUSSIONS)
                    } else if (routeHistory.lastOrNull() != route) {
                        routeHistory = routeHistory + route
                    }
                    currentRoute = route
                    lastBackPressAt = 0L
                }

                fun goBack() {
                    if (routeHistory.size > 1) {
                        val previousHistory = routeHistory.dropLast(1)
                        routeHistory = previousHistory
                        currentRoute = previousHistory.last()
                        lastBackPressAt = 0L
                        return
                    }
                    val now = System.currentTimeMillis()
                    if (now - lastBackPressAt <= 1800L) {
                        finish()
                    } else {
                        lastBackPressAt = now
                        Toast.makeText(
                            this@MainActivity,
                            getString(com.hira.kidas.R.string.press_back_again_to_exit),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                BackHandler(enabled = true) {
                    goBack()
                }

                // Données Google transmises à l'écran 7 pour préremplir le profil.
                var profileDisplayName by rememberSaveable { mutableStateOf("") }
                var profilePhotoUrl by rememberSaveable { mutableStateOf<String?>(null) }
                var profileUsername by rememberSaveable { mutableStateOf<String?>(null) }
                var selectedChatContactName by rememberSaveable { mutableStateOf<String?>(null) }
                var selectedChatContactStatus by rememberSaveable { mutableStateOf<String?>(null) }
                var selectedLargeFileUri by rememberSaveable { mutableStateOf<String?>(null) }
                var selectedLargeFileName by rememberSaveable { mutableStateOf<String?>(null) }
                var selectedLargeFileSizeBytes by rememberSaveable { mutableStateOf<Long?>(null) }
                var selectedLargeFileMimeType by rememberSaveable { mutableStateOf<String?>(null) }
                var selectedGroupName by rememberSaveable { mutableStateOf<String?>(null) }
                var selectedGroupMemberCount by rememberSaveable { mutableStateOf(0) }

                val defaultGroupContacts = remember {
                    listOf(
                        GroupContact(
                            id = "demo-hira-user",
                            displayName = getString(com.hira.kidas.R.string.contacts_demo_name),
                            initials = "HI"
                        )
                    )
                }

                val attachmentPicker = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocument()
                ) { uri ->
                    selectedLargeFileUri = uri?.toString()
                }

                LaunchedEffect(selectedLargeFileUri) {
                    val uriString = selectedLargeFileUri ?: return@LaunchedEffect
                    val uri = Uri.parse(uriString)
                    val metadata = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        readSelectedFileMetadata(contentResolver, uri)
                    }
                    if (metadata != null) {
                        selectedLargeFileName = metadata.name
                        selectedLargeFileSizeBytes = metadata.sizeBytes
                        selectedLargeFileMimeType = metadata.mimeType
                        goTo(HiraRoutes.TELECHARGEMENT_FICHIER)
                    }
                }

                // Écoute de l'événement de navigation après authentification réussie
                // Consommé une seule fois grâce au Channel
                LaunchedEffect(authViewModel) {
                    authViewModel.navigationEvent.collect { event ->
                        when (event) {
                            is AuthNavigationEvent.NavigateSuccess -> {
                                if (event.user.isNewUser) {
                                    profileDisplayName = event.user.displayName.orEmpty()
                                    profilePhotoUrl = event.user.photoUrl
                                    goTo(HiraRoutes.CONFIG_PROFIL)
                                } else {
                                    goTo(HiraRoutes.DISCUSSIONS)
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
                                        goTo(
                                            when {
                                                authRepository.isSignedIn() -> HiraRoutes.DISCUSSIONS
                                                onboardingPreferences.isOnboardingCompleted() -> HiraRoutes.CONNEXION
                                                else -> HiraRoutes.ACCUEIL_1
                                            }
                                        )
                                    }
                                )
                            }
                            HiraRoutes.ACCUEIL_1 -> {
                                OnboardingScreen1(
                                    onNextClick = { goTo(HiraRoutes.ACCUEIL_)2 },
                                    onSkipClick = { goTo(HiraRoutes.ACCUEIL_)3 },
                                    onBack = { finish() }
                                )
                            }
                            HiraRoutes.ACCUEIL_2 -> {
                                OnboardingScreen2(
                                    onNextClick = { goTo(HiraRoutes.ACCUEIL_)3 },
                                    onSkipClick = { goTo(HiraRoutes.ACCUEIL_)3 },
                                    onBackClick = { goTo(HiraRoutes.ACCUEIL_)1 }
                                )
                            }
                            HiraRoutes.ACCUEIL_3 -> {
                                OnboardingScreen3(
                                    onFinishClick = {
                                        onboardingPreferences.setOnboardingStep(3)
                                        onboardingPreferences.setOnboardingCompleted(true)
                                        goTo(HiraRoutes.CONNEXION)
                                    },
                                    onBackClick = { goTo(HiraRoutes.ACCUEIL_)2 }
                                )
                            }
                            HiraRoutes.CONNEXION -> {
                                LoginScreen(
                                    viewModel = authViewModel,
                                    uiState = authUiState,
                                    onNavigateToRegister = {
                                        authViewModel.clearErrors()
                                        goTo(HiraRoutes.CREATION_COMPTE)
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
                                        goTo(HiraRoutes.CONNEXION)
                                    }
                                )
                            }
                            HiraRoutes.DISCUSSIONS -> {
                                DiscussionsScreen(
                                    onContactsClick = {
                                        goTo(HiraRoutes.CONTACTS)
                                    },
                                    onGroupsClick = {
                                        goTo(HiraRoutes.NOUVEAU_GROUPE)
                                    }
                                )
                            }
                            HiraRoutes.CONTACTS -> {
                                ContactsScreen(
                                    onDiscussionsClick = {
                                        goTo(HiraRoutes.DISCUSSIONS)
                                    },
                                    onInviteClick = {
                                        // Le parcours de téléchargement HIRA sera ajouté sur un écran dédié ultérieurement.
                                    },
                                    onContactClick = {
                                        // Les contacts réels seront ouverts après la vérification du compte HIRA.
                                    },
                                    onDemoContactClick = {
                                        selectedChatContactName = getString(com.hira.kidas.R.string.contacts_demo_name)
                                        selectedChatContactStatus = getString(com.hira.kidas.R.string.contacts_demo_status)
                                        goTo(HiraRoutes.CHAT_PRIVE)
                                    }
                                )
                            }
                            HiraRoutes.CHAT_PRIVE -> {
                                PrivateChatScreen(
                                    contactName = selectedChatContactName,
                                    contactStatus = selectedChatContactStatus,
                                    onBack = { goBack() },
                                    onAttachmentClick = {
                                        goTo(HiraRoutes.PIECES_JOINTES)
                                    },
                                    onSendMessage = {
                                        // L'envoi réel sera branché au service de messagerie.
                                    }
                                )
                            }
                            HiraRoutes.PIECES_JOINTES -> {
                                AttachmentMenuScreen(
                                    contactName = selectedChatContactName,
                                    contactStatus = selectedChatContactStatus,
                                    onBack = {
                                        goTo(HiraRoutes.CHAT_PRIVE)
                                    },
                                    onAttachmentAction = { action ->
                                        when (action) {
                                            AttachmentAction.VIDEO -> {
                                                attachmentPicker.launch(arrayOf("video/*"))
                                            }
                                            AttachmentAction.DOCUMENT -> {
                                                attachmentPicker.launch(arrayOf("*/*"))
                                            }
                                            AttachmentAction.PHOTO,
                                            AttachmentAction.VOICE -> {
                                                // Les sélecteurs correspondants seront ajoutés dans leurs écrans dédiés.
                                            }
                                        }
                                    }
                                )
                            }
                            HiraRoutes.TELECHARGEMENT_FICHIER -> {
                                LargeFileDownloadScreen(
                                    contactName = selectedChatContactName,
                                    contactStatus = selectedChatContactStatus,
                                    fileName = selectedLargeFileName.orEmpty().ifBlank {
                                        getString(com.hira.kidas.R.string.large_file_name_unknown)
                                    },
                                    fileSizeBytes = selectedLargeFileSizeBytes,
                                    mimeType = selectedLargeFileMimeType,
                                    onBack = { goBack() },
                                    onDownloadClick = {
                                        // Le téléchargement réel sera relié au moteur de transfert de fichiers.
                                    },
                                    onCancel = {
                                        goTo(HiraRoutes.PIECES_JOINTES)
                                    }
                                )
                            }
                            HiraRoutes.NOUVEAU_GROUPE -> {
                                NewGroupScreen(
                                    contacts = defaultGroupContacts,
                                    onBack = {
                                        goTo(HiraRoutes.DISCUSSIONS)
                                    },
                                    onCreateGroup = { groupName, selectedContactIds ->
                                        selectedGroupName = groupName
                                        selectedGroupMemberCount = selectedContactIds.size + 1
                                        goTo(HiraRoutes.CHAT_GROUPE)
                                    }
                                )
                            }
                            HiraRoutes.CHAT_GROUPE -> {
                                GroupChatScreen(
                                    groupName = selectedGroupName,
                                    memberCount = selectedGroupMemberCount,
                                    onBack = { goBack() },
                                    onAttachmentClick = {
                                        goTo(HiraRoutes.PIECES_JOINTES)
                                    },
                                    onSendMessage = {
                                        // L'envoi réel sera branché au service de messagerie de groupe.
                                    }
                                )
                            }
                            HiraRoutes.PROFIL -> {
                                val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                                ProfileScreen(
                                    displayName = profileDisplayName.ifBlank { firebaseUser?.displayName },
                                    username = profileUsername,
                                    email = firebaseUser?.email,
                                    phoneNumber = firebaseUser?.phoneNumber,
                                    photoUrl = profilePhotoUrl ?: firebaseUser?.photoUrl?.toString(),
                                    memberSinceMillis = firebaseUser?.metadata?.creationTimestamp,
                                    onBack = { goBack() }
                                )
                            }
                            HiraRoutes.CONFIG_PROFIL -> {
                                ProfileSetupScreen(
                                    initialDisplayName = profileDisplayName,
                                    initialPhotoUrl = profilePhotoUrl,
                                    onFinish = { _, _ ->
                                        goTo(HiraRoutes.PERMISSION_CONTACTS)
                                    }
                                )
                            }
                            HiraRoutes.PERMISSION_CONTACTS -> {
                                ContactsPermissionScreen(
                                    onPermissionGranted = {
                                        goTo(HiraRoutes.DISCUSSIONS)
                                    },
                                    onSkip = {
                                        goTo(HiraRoutes.DISCUSSIONS)
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


private data class SelectedFileMetadata(
    val name: String,
    val sizeBytes: Long?,
    val mimeType: String?
)

private fun readSelectedFileMetadata(
    resolver: android.content.ContentResolver,
    uri: Uri
): SelectedFileMetadata? {
    val nameAndSize = resolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
        null,
        null,
        null
    )?.use { cursor ->
        if (!cursor.moveToFirst()) return@use null

        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        val name = if (nameIndex >= 0) cursor.getString(nameIndex)?.trim().orEmpty() else ""
        val size = if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) cursor.getLong(sizeIndex) else null
        if (name.isBlank()) null else name to size
    } ?: return null

    return SelectedFileMetadata(
        name = nameAndSize.first,
        sizeBytes = nameAndSize.second,
        mimeType = resolver.getType(uri)
    )
}
