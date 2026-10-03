package com.example.ui.auth

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthErrorMapper
import com.example.data.auth.AuthUser
import com.example.data.auth.AuthRepository
import com.example.data.auth.AuthValidator
import com.example.data.auth.GoogleNoAccountException
import com.example.data.auth.GoogleSignInCancelledException
import com.google.firebase.auth.FirebaseAuthException
import com.hira.kidas.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * État de l'interface utilisateur pour l'authentification.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null,
    @StringRes val confirmPasswordError: Int? = null,
    @StringRes val cguError: Int? = null,
    @StringRes val generalError: Int? = null,
    val debugErrorDetails: String? = null,
    @StringRes val infoMessage: Int? = null
)

/**
 * Événements uniques de navigation après authentification.
 */
sealed interface AuthNavigationEvent {
    data class NavigateSuccess(val user: AuthUser) : AuthNavigationEvent
}

/**
 * ViewModel gérant la logique des écrans Connexion et Création de compte.
 */
class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _navigationChannel = Channel<AuthNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationChannel.receiveAsFlow()

    /**
     * Connexion par e-mail et mot de passe.
     */
    fun signInWithEmail(email: String, password: String) {
        if (_uiState.value.isLoading) return

        clearErrors()

        var hasError = false
        if (!AuthValidator.isValidEmail(email)) {
            _uiState.update { it.copy(emailError = R.string.auth_error_invalid_email) }
            hasError = true
        }

        if (password.isBlank()) {
            _uiState.update { it.copy(passwordError = R.string.auth_error_weak_password) }
            hasError = true
        }

        if (hasError) return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = authRepository.signIn(email, password)
            _uiState.update { it.copy(isLoading = false) }

            result.fold(
                onSuccess = { user ->
                    _navigationChannel.send(AuthNavigationEvent.NavigateSuccess(user = user))
                },
                onFailure = { error ->
                    val errorRes = AuthErrorMapper.fromThrowable(error)
                    _uiState.update { it.copy(generalError = errorRes) }
                }
            )
        }
    }

    /**
     * Création de compte par e-mail et mot de passe.
     */
    fun signUpWithEmail(
        email: String,
        password: String,
        confirmPassword: String,
        isCguAccepted: Boolean
    ) {
        if (_uiState.value.isLoading) return

        clearErrors()

        var hasError = false
        if (!AuthValidator.isValidEmail(email)) {
            _uiState.update { it.copy(emailError = R.string.auth_error_invalid_email) }
            hasError = true
        }

        if (!AuthValidator.isValidPassword(password)) {
            _uiState.update { it.copy(passwordError = R.string.auth_error_weak_password) }
            hasError = true
        }

        if (!AuthValidator.doPasswordsMatch(password, confirmPassword)) {
            _uiState.update { it.copy(confirmPasswordError = R.string.auth_error_passwords_mismatch) }
            hasError = true
        }

        if (!isCguAccepted) {
            _uiState.update { it.copy(cguError = R.string.auth_cgu_required) }
            hasError = true
        }

        if (hasError) return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = authRepository.signUp(email, password)
            _uiState.update { it.copy(isLoading = false) }

            result.fold(
                onSuccess = { user ->
                    _navigationChannel.send(AuthNavigationEvent.NavigateSuccess(user = user))
                },
                onFailure = { error ->
                    val errorRes = AuthErrorMapper.fromThrowable(error)
                    _uiState.update { it.copy(generalError = errorRes) }
                }
            )
        }
    }

    /**
     * Connexion Google avec l'idToken obtenu du Credential Manager.
     */
    fun signInWithGoogleToken(idToken: String) {
        if (_uiState.value.isLoading) return

        clearErrors()
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            _uiState.update { it.copy(isLoading = false) }

            result.fold(
                onSuccess = { user ->
                    _navigationChannel.send(AuthNavigationEvent.NavigateSuccess(user = user))
                },
                onFailure = { error ->
                    val errorRes = AuthErrorMapper.fromThrowable(error)
                    val errorCode = (error as? FirebaseAuthException)?.errorCode
                    val debugText = buildString {
                        append(error.javaClass.name)
                        if (errorCode != null) {
                            append(" [errorCode: ")
                            append(errorCode)
                            append("]")
                        }
                        if (!error.message.isNullOrBlank()) {
                            append(" - ")
                            append(error.message)
                        }
                    }
                    _uiState.update { it.copy(generalError = errorRes, debugErrorDetails = debugText) }
                }
            )
        }
    }

    /**
     * Gestion des erreurs lors de la récupération du token Google dans l'UI.
     */
    fun onGoogleSignInError(error: Throwable) {
        val errorRes = when (error) {
            is GoogleSignInCancelledException -> R.string.auth_error_google_cancelled
            is GoogleNoAccountException -> R.string.auth_error_google_no_account
            else -> AuthErrorMapper.fromThrowable(error)
        }
        val errorCode = (error as? FirebaseAuthException)?.errorCode
        val debugText = buildString {
            append(error.javaClass.name)
            if (errorCode != null) {
                append(" [errorCode: $errorCode]")
            }
            if (!error.message.isNullOrBlank()) {
                append(" - ${error.message}")
            }
        }
        _uiState.update {
            it.copy(
                generalError = errorRes,
                debugErrorDetails = debugText,
                isLoading = false
            )
        }
    }

    /**
     * Réinitialisation du mot de passe.
     * Affiche un message neutre systématiquement pour des raisons de confidentialité.
     */
    fun sendPasswordReset(email: String) {
        if (_uiState.value.isLoading) return

        clearErrors()

        if (!AuthValidator.isValidEmail(email)) {
            _uiState.update { it.copy(emailError = R.string.auth_error_invalid_email) }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            // L'appel est exécuté en arrière-plan, mais le message reste neutre même en cas d'erreur
            authRepository.sendPasswordResetEmail(email)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    infoMessage = R.string.auth_forgot_password_neutral_message
                )
            }
        }
    }

    /**
     * Efface les erreurs affichées.
     */
    fun clearErrors() {
        _uiState.update {
            it.copy(
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                cguError = null,
                generalError = null,
                debugErrorDetails = null
            )
        }
    }

    /**
     * Efface le message d'information.
     */
    fun clearInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }
}
