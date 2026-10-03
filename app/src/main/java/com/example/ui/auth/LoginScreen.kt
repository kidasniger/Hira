package com.example.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.GoogleSignInHelper
import com.example.ui.components.HiraLogo
import com.example.ui.theme.HiraBorder
import com.example.ui.theme.HiraGrayDark
import com.example.ui.theme.HiraGrayLight
import com.example.ui.theme.HiraGrayMedium
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraWhite
import com.hira.kidas.R
import kotlinx.coroutines.launch

/**
 * ÉCRAN 5 — Connexion
 *
 * Interface épurée et légère :
 * - Fond blanc (#FFFFFF)
 * - Titre "Connexion"
 * - Bouton "Continuer avec Google" via Credential Manager
 * - Séparateur "ou"
 * - Champs E-mail et Mot de passe (avec bascule afficher/masquer)
 * - Lien "Mot de passe oublié ?" (message neutre protecteur)
 * - Bouton principal "Se connecter" (#0043FF)
 * - Lien vers "Créer un compte"
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    onNavigateToRegister: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val googleSignInHelper = remember { GoogleSignInHelper(context) }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    // Retour visuel immédiat pendant l'ouverture de Credential Manager Google.
    var isGoogleLoading by rememberSaveable { mutableStateOf(false) }

    // Quitter l'application sur retour arrière depuis Connexion
    BackHandler {
        onBack()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HiraWhite)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .imePadding()
            .testTag("login_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Logo officiel HIRA
            HiraLogo(
                size = 72.dp,
                tint = HiraRoyalBlue,
                isAnimated = false,
                modifier = Modifier.testTag("login_logo")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Titre de l'écran
            Text(
                text = stringResource(id = R.string.auth_login_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF111111),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.testTag("login_title")
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(id = R.string.auth_login_subtitle),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = HiraGrayDark,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Bouton Continuer avec Google
            OutlinedButton(
                onClick = {
                    if (uiState.isLoading || isGoogleLoading) return@OutlinedButton
                    isGoogleLoading = true
                    scope.launch {
                        try {
                            val tokenResult = googleSignInHelper.getGoogleIdToken()
                            tokenResult.fold(
                                onSuccess = { token ->
                                    viewModel.signInWithGoogleToken(token)
                                },
                                onFailure = { error ->
                                    viewModel.onGoogleSignInError(error)
                                }
                            )
                        } finally {
                            // Pour une authentification réussie, uiState.isLoading prend le relais
                            // pendant l'appel Firebase. En cas d'annulation/erreur, on réactive le bouton.
                            isGoogleLoading = false
                        }
                    }
                },
                enabled = !uiState.isLoading && !isGoogleLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_google_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = HiraWhite,
                    contentColor = Color(0xFF111111)
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(HiraBorder),
                    width = 1.dp
                )
            ) {
                if (isGoogleLoading || uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = HiraRoyalBlue,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Logo Google officiel
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = stringResource(id = R.string.auth_google_continue),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(id = R.string.auth_google_continue),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF111111),
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Séparateur "ou"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = HiraBorder,
                    thickness = 1.dp
                )
                Text(
                    text = stringResource(id = R.string.auth_or_divider),
                    color = HiraGrayMedium,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = HiraBorder,
                    thickness = 1.dp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Champ E-mail
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (uiState.emailError != null) viewModel.clearErrors()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_email_input"),
                label = { Text(stringResource(id = R.string.auth_email_label), color = HiraGrayDark) },
                placeholder = { Text(stringResource(id = R.string.auth_email_hint), color = HiraGrayMedium) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = uiState.emailError != null,
                supportingText = uiState.emailError?.let {
                    { Text(text = stringResource(id = it), color = Color(0xFFD32F2F), fontSize = 12.sp) }
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

            Spacer(modifier = Modifier.height(12.dp))

            // Champ Mot de passe
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (uiState.passwordError != null) viewModel.clearErrors()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                label = { Text(stringResource(id = R.string.auth_password_label), color = HiraGrayDark) },
                placeholder = { Text(stringResource(id = R.string.auth_password_hint), color = HiraGrayMedium) },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.signInWithEmail(email, password)
                    }
                ),
                trailingIcon = {
                    IconButton(
                        onClick = { isPasswordVisible = !isPasswordVisible },
                        modifier = Modifier.testTag("login_toggle_password_visibility")
                    ) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = stringResource(
                                id = if (isPasswordVisible) R.string.auth_hide_password else R.string.auth_show_password
                            ),
                            tint = HiraGrayDark
                        )
                    }
                },
                isError = uiState.passwordError != null,
                supportingText = uiState.passwordError?.let {
                    { Text(text = stringResource(id = it), color = Color(0xFFD32F2F), fontSize = 12.sp) }
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

            // Lien "Mot de passe oublié ?"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.sendPasswordReset(email)
                    },
                    modifier = Modifier.testTag("login_forgot_password_button")
                ) {
                    Text(
                        text = stringResource(id = R.string.auth_forgot_password),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HiraRoyalBlue,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }

            // Message d'erreur global
            if (uiState.generalError != null) {
                Text(
                    text = stringResource(id = uiState.generalError),
                    color = Color(0xFFD32F2F),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = if (uiState.debugErrorDetails.isNullOrBlank()) 8.dp else 2.dp)
                        .testTag("login_general_error")
                )
            }

            // Détails de l'exception et code d'erreur affichés pour recopie
            if (!uiState.debugErrorDetails.isNullOrBlank()) {
                Text(
                    text = uiState.debugErrorDetails,
                    color = Color.Gray,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("login_debug_error_details")
                )
            }

            // Message d'information neutre (ex: mot de passe oublié)
            if (uiState.infoMessage != null) {
                Text(
                    text = stringResource(id = uiState.infoMessage),
                    color = Color(0xFF2E7D32),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("login_info_message")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bouton principal Se connecter
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.signInWithEmail(email, password)
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_submit_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HiraRoyalBlue,
                    contentColor = HiraWhite
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = HiraWhite,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.auth_login_button),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lien vers Création de compte
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.auth_no_account_yet),
                    fontSize = 14.sp,
                    color = HiraGrayDark,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.width(6.dp))
                TextButton(
                    onClick = onNavigateToRegister,
                    modifier = Modifier.testTag("login_go_to_signup_button")
                ) {
                    Text(
                        text = stringResource(id = R.string.auth_go_to_signup),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = HiraRoyalBlue,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }
    }
}
