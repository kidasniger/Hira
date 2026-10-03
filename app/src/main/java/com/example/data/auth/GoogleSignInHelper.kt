package com.example.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CancellationException

/**
 * Exceptions personnalisées pour distinguer les scénarios d'échec de Google Sign-In.
 */
class GoogleSignInCancelledException(message: String = "Connexion Google annulée.") : Exception(message)
class GoogleNoAccountException(message: String = "Aucun compte Google disponible sur cet appareil.") : Exception(message)

/**
 * Gestionnaire pour la connexion Google via Jetpack Credential Manager.
 */
class GoogleSignInHelper(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    companion object {
        const val FALLBACK_SERVER_CLIENT_ID = "129923565999-3vatbhohksivgoks9ps7iaaugv4dcmu3.apps.googleusercontent.com"
    }

    /**
     * Récupère le Client ID serveur configuré (default_web_client_id) ou utilise la valeur de repli.
     */
    fun getServerClientId(): String {
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        return if (resId != 0) {
            try {
                context.getString(resId)
            } catch (_: Exception) {
                FALLBACK_SERVER_CLIENT_ID
            }
        } else {
            FALLBACK_SERVER_CLIENT_ID
        }
    }

    /**
     * Déclenche la sélection d'un compte Google et retourne l'idToken.
     */
    suspend fun getGoogleIdToken(): Result<String> {
        return try {
            val serverClientId = getServerClientId()
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = response.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                Result.success(googleIdTokenCredential.idToken)
            } else {
                Result.failure(IllegalStateException("Type d'identifiant Google inattendu."))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: GetCredentialCancellationException) {
            Result.failure(GoogleSignInCancelledException())
        } catch (_: NoCredentialException) {
            Result.failure(GoogleNoAccountException())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
