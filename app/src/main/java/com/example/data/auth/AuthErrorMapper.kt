package com.example.data.auth

import androidx.annotation.StringRes
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.hira.kidas.R

/**
 * Mappe les codes et exceptions Firebase Authentication en ressources string françaises.
 */
object AuthErrorMapper {

    /**
     * Convertit un code d'erreur textuel Firebase en identifiant de ressource chaîne.
     */
    @StringRes
    fun fromCode(code: String?): Int {
        if (code == null) return R.string.auth_error_generic

        val normalized = code.trim().uppercase().replace("-", "_")

        return when {
            normalized.contains("INVALID_CREDENTIAL") -> R.string.auth_error_invalid_credential
            normalized.contains("WRONG_PASSWORD") -> R.string.auth_error_wrong_password
            normalized.contains("USER_NOT_FOUND") -> R.string.auth_error_user_not_found
            normalized.contains("EMAIL_ALREADY_IN_USE") -> R.string.auth_error_email_already_in_use
            normalized.contains("WEAK_PASSWORD") -> R.string.auth_error_weak_password
            normalized.contains("NETWORK") -> R.string.auth_error_network
            normalized.contains("TOO_MANY_REQUESTS") -> R.string.auth_error_too_many_requests
            normalized.contains("OPERATION_NOT_ALLOWED") -> R.string.auth_error_operation_not_allowed
            else -> R.string.auth_error_generic
        }
    }

    /**
     * Convertit une exception en ressource string française appropriée.
     */
    @StringRes
    fun fromThrowable(throwable: Throwable?): Int {
        return when (throwable) {
            is FirebaseAuthException -> fromCode(throwable.errorCode)
            is FirebaseNetworkException -> R.string.auth_error_network
            is FirebaseTooManyRequestsException -> R.string.auth_error_too_many_requests
            else -> fromCode(throwable?.message)
        }
    }
}
