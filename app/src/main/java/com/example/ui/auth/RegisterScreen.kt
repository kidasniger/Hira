package com.example.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
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
import com.example.ui.theme.HiraBorder
import com.example.ui.theme.HiraGrayDark
import com.example.ui.theme.HiraGrayMedium
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraWhite
import com.hira.kidas.R

/**
 * ÉCRAN 6 — Création de compte
 *
 * Interface :
 * - Bouton retour en haut à gauche
 * - Logo officiel HIRA
 * - Titre "Créer un compte"
 * - Champs E-mail, Mot de passe et Confirmation (avec bascule afficher/masquer)
 * - Case à cocher "J'accepte les conditions d'utilisation"
 * - Bouton principal "Créer mon compte" (#0043FF)
 * - Lien vers "Se connecter"
 * - Gestion du retour système vers Connexion via BackHandler
 */
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isConfirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isCguAccepted by rememberSaveable { mutableStateOf(false) }

    // Retour système ramenant sur l'écran Connexion
    BackHandler {
        onBackToLogin()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HiraWhite)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .imePadding()
            .testTag("register_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Barre supérieure conforme à la maquette validée de l'écran 6
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp)
                    .background(HiraWhite),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToLogin,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("register_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.auth_go_to_login),
                        tint = HiraGrayDark
                    )
                }
                Text(
                    text = stringResource(id = R.string.auth_register_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111111),
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Contenu défilable de création de compte
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Champ E-mail
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (uiState.emailError != null) viewModel.clearErrors()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 320.dp)
                        .testTag("register_email_input"),
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
                        .widthIn(max = 320.dp)
                        .testTag("register_password_input"),
                    label = { Text(stringResource(id = R.string.auth_password_label), color = HiraGrayDark) },
                    placeholder = { Text(stringResource(id = R.string.auth_register_password_hint), color = HiraGrayMedium) },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = { isPasswordVisible = !isPasswordVisible },
                            modifier = Modifier.testTag("register_toggle_password_visibility")
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

                Spacer(modifier = Modifier.height(12.dp))

                // Champ Confirmation mot de passe
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        if (uiState.confirmPasswordError != null) viewModel.clearErrors()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 320.dp)
                        .testTag("register_confirm_password_input"),
                    label = { Text(stringResource(id = R.string.auth_confirm_password_label), color = HiraGrayDark) },
                    placeholder = { Text(stringResource(id = R.string.auth_confirm_password_hint), color = HiraGrayMedium) },
                    singleLine = true,
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.signUpWithEmail(email, password, confirmPassword, isCguAccepted)
                        }
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
                            modifier = Modifier.testTag("register_toggle_confirm_password_visibility")
                        ) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = stringResource(
                                    id = if (isConfirmPasswordVisible) R.string.auth_hide_password else R.string.auth_show_password
                                ),
                                tint = HiraGrayDark
                            )
                        }
                    },
                    isError = uiState.confirmPasswordError != null,
                    supportingText = uiState.confirmPasswordError?.let {
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

                Spacer(modifier = Modifier.height(8.dp))

                // Case CGU
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 320.dp)
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isCguAccepted,
                        onCheckedChange = {
                            isCguAccepted = it
                            if (uiState.cguError != null) viewModel.clearErrors()
                        },
                        modifier = Modifier.testTag("register_cgu_checkbox"),
                        colors = CheckboxDefaults.colors(
                            checkedColor = HiraRoyalBlue,
                            uncheckedColor = HiraBorder,
                            checkmarkColor = HiraWhite
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(id = R.string.auth_cgu_checkbox),
                        fontSize = 13.sp,
                        color = Color(0xFF555555),
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (uiState.cguError != null) {
                    Text(
                        text = stringResource(id = uiState.cguError),
                        color = Color(0xFFD32F2F),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 320.dp)
                            .padding(start = 12.dp, bottom = 4.dp)
                    )
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
                            .widthIn(max = 320.dp)
                            .padding(vertical = 4.dp)
                            .testTag("register_general_error")
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Bouton principal Créer mon compte
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.signUpWithEmail(email, password, confirmPassword, isCguAccepted)
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 320.dp)
                        .height(52.dp)
                        .testTag("register_submit_button"),
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
                            text = stringResource(id = R.string.auth_register_button),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Lien vers Se connecter
                Row(
                    modifier = Modifier.padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.auth_already_have_account),
                        fontSize = 14.sp,
                        color = HiraGrayDark,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    TextButton(
                        onClick = onBackToLogin,
                        modifier = Modifier.testTag("register_go_to_login_button")
                    ) {
                        Text(
                            text = stringResource(id = R.string.auth_go_to_login),
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
}
