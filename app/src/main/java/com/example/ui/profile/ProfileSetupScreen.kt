package com.example.ui.profile

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HiraBorder
import com.example.ui.theme.HiraGrayDark
import com.example.ui.theme.HiraGrayLight
import com.example.ui.theme.HiraGrayMedium
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraWhite
import com.hira.kidas.R

/**
 * ÉCRAN 7 — CONFIGURATION DU PROFIL
 *
 * Interface :
 * - Fond blanc
 * - Barre supérieure de hauteur 56 dp avec titre centré : « Votre profil »
 * - Avatar circulaire de 96 dp gris clair avec bouton caméra superposé en bas à droite
 * - Deux champs de texte :
 *   1. « Nom d'utilisateur » (placeholder: « @amina »)
 *   2. « Nom affiché » (placeholder: « Amina Diallo »)
 * - Bouton principal bleu « Terminer »
 * - Validation minimale (champs obligatoires, gestion intelligente du caractère @)
 */
@Composable
fun ProfileSetupScreen(
    initialDisplayName: String? = null,
    initialPhotoUrl: String? = null,
    onFinish: (username: String, displayName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val googleDisplayName = initialDisplayName?.trim().orEmpty()
    val suggestedUsername = suggestUsername(googleDisplayName)

    // Pour un nouvel utilisateur Google, les champs sont préremplis dès l'ouverture.
    // Pour un compte e-mail, les valeurs initiales restent vides.
    var username by rememberSaveable { mutableStateOf(suggestedUsername) }
    var displayName by rememberSaveable { mutableStateOf(googleDisplayName) }
    var avatarUriString by rememberSaveable { mutableStateOf(initialPhotoUrl) }

    var usernameError by rememberSaveable { mutableStateOf<Int?>(null) }
    var displayNameError by rememberSaveable { mutableStateOf<Int?>(null) }

    // Sélecteur de média photo natif (sans permission requise)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            avatarUriString = uri.toString()
        }
    }

    // Empêche la fermeture brutale ou le retour arrière vers l'inscription déjà validée
    BackHandler {
        // Reste sur l'écran de profil pour finaliser la configuration
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HiraWhite)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .imePadding()
            .testTag("profile_setup_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barre supérieure : hauteur environ 56 dp avec titre centré « Votre profil »
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.profile_setup_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111111),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.testTag("profile_setup_title")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grand avatar circulaire de 96 dp avec petite action caméra en bas à droite
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .testTag("profile_avatar_container"),
                contentAlignment = Alignment.Center
            ) {
                // Fond de l'avatar (cercle gris clair ou photo sélectionnée)
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(HiraGrayLight)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (!avatarUriString.isNullOrBlank()) {
                        AsyncImage(
                            model = avatarUriString,
                            contentDescription = stringResource(id = R.string.profile_setup_avatar_description),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = stringResource(id = R.string.profile_setup_avatar_description),
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Bouton circulaire bleu superposé en bas à droite (action caméra/modification)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(HiraRoyalBlue)
                        .border(width = 2.dp, color = HiraWhite, shape = CircleShape)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .testTag("profile_avatar_edit_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = stringResource(id = R.string.profile_setup_edit_photo_description),
                        tint = HiraWhite,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Zone des champs de saisie (largeur max 320 dp pour respecter la grille de référence 360x800)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 320.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Champ Nom d'utilisateur
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.profile_username_label),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = HiraGrayMedium,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { input ->
                            // Préserve ou insère le @ proprement sans générer de double @
                            val cleaned = if (input.startsWith("@")) {
                                "@" + input.substring(1).replace("@", "").trimStart()
                            } else {
                                input.replace("@", "")
                            }
                            username = cleaned
                            if (usernameError != null) usernameError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_username_input"),
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.profile_username_hint),
                                color = Color(0xFF9CA3AF),
                                fontSize = 14.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        isError = usernameError != null,
                        supportingText = usernameError?.let { resId ->
                            {
                                Text(
                                    text = stringResource(id = resId),
                                    color = Color(0xFFD32F2F),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    modifier = Modifier.testTag("profile_username_error")
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF111111),
                            unfocusedTextColor = Color(0xFF111111),
                            focusedContainerColor = HiraWhite,
                            unfocusedContainerColor = HiraWhite,
                            focusedBorderColor = HiraRoyalBlue,
                            unfocusedBorderColor = HiraBorder,
                            errorBorderColor = Color(0xFFD32F2F)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Champ Nom affiché
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.profile_display_name_label),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = HiraGrayMedium,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { input ->
                            displayName = input
                            if (displayNameError != null) displayNameError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_display_name_input"),
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.profile_display_name_hint),
                                color = Color(0xFF9CA3AF),
                                fontSize = 14.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                validateAndSubmit(
                                    username = username,
                                    displayName = displayName,
                                    onUsernameError = { usernameError = it },
                                    onDisplayNameError = { displayNameError = it },
                                    onSuccess = onFinish
                                )
                            }
                        ),
                        isError = displayNameError != null,
                        supportingText = displayNameError?.let { resId ->
                            {
                                Text(
                                    text = stringResource(id = resId),
                                    color = Color(0xFFD32F2F),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    modifier = Modifier.testTag("profile_display_name_error")
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF111111),
                            unfocusedTextColor = Color(0xFF111111),
                            focusedContainerColor = HiraWhite,
                            unfocusedContainerColor = HiraWhite,
                            focusedBorderColor = HiraRoyalBlue,
                            unfocusedBorderColor = HiraBorder,
                            errorBorderColor = Color(0xFFD32F2F)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f, fill = false).heightIn(min = 32.dp))

            // Bouton principal bleu « Terminer »
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 320.dp)
                    .padding(bottom = 24.dp, top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        validateAndSubmit(
                            username = username,
                            displayName = displayName,
                            onUsernameError = { usernameError = it },
                            onDisplayNameError = { displayNameError = it },
                            onSuccess = onFinish
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("profile_finish_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HiraRoyalBlue,
                        contentColor = HiraWhite
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.profile_setup_finish),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HiraWhite,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }
    }
}

/**
 * Validation minimale cohérente du profil HIRA.
 */
private fun validateAndSubmit(
    username: String,
    displayName: String,
    onUsernameError: (Int) -> Unit,
    onDisplayNameError: (Int) -> Unit,
    onSuccess: (username: String, displayName: String) -> Unit
) {
    val trimmedUser = username.trim()
    val trimmedName = displayName.trim()
    var valid = true

    if (trimmedUser.isEmpty() || trimmedUser == "@") {
        onUsernameError(R.string.profile_setup_username_required)
        valid = false
    }

    if (trimmedName.isEmpty()) {
        onDisplayNameError(R.string.profile_setup_display_name_required)
        valid = false
    }

    if (valid) {
        val finalUsername = if (trimmedUser.startsWith("@")) trimmedUser else "@$trimmedUser"
        onSuccess(finalUsername, trimmedName)
    }
}


private fun suggestUsername(displayName: String): String {
    val base = displayName
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), "_")
        .trim('_')
        .take(24)

    return if (base.isBlank()) "" else "@$base"
}
