package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.hira.kidas.R
import kotlinx.coroutines.CancellationException

/**
 * Exceptions personnalisées pour distinguer les scénarios d'échec de Google Sign-In.
 */
class GoogleSignInCancelledException(message: String = "Connexion Google annulée.") : Exception(message)
class GoogleNoAccountException(message: String = "Aucun compte Google disponible sur cet appareil.") : Exception(message)

/**
 * Gestionnaire pour la connexion Google via Jetpack Credential Manager.
 * Utilise exclusivement le client OAuth Web (default_web_client_id) généré par google-services.json.
 */
class GoogleSignInHelper(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    /**
     * Récupère le Client ID serveur configuré (default_web_client_id)
     * généré par le plugin Google Services à partir de app/google-services.json.
     */
    fun getServerClientId(): String {
        val clientIdFromR = try {
            context.getString(R.string.default_web_client_id).takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }

        if (!clientIdFromR.isNullOrBlank()) {
            return clientIdFromR
        }

        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        if (resId != 0) {
            val clientId = context.getString(resId).takeIf { it.isNotBlank() }
            if (clientId != null) {
                return clientId
            }
        }

        Log.e(TAG, "Client OAuth manquant: default_web_client_id introuvable dans les ressources générées par google-services.json")
        throw IllegalStateException("Le client OAuth Web (default_web_client_id) est introuvable. Vérifiez que google-services.json est configuré.")
    }

    /**
     * Déclenche la sélection d'un compte Google via Credential Manager et retourne l'idToken.
     */
    suspend fun getGoogleIdToken(): Result<String> {
        return try {
            val serverClientId = getServerClientId()
            Log.d(TAG, "Démarrage de la requête Credential Manager")

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
                val idToken = googleIdTokenCredential.idToken
                if (idToken.isNotBlank()) {
                    Log.i(TAG, "Google ID Token récupéré avec succès")
                    Result.success(idToken)
                } else {
                    Log.e(TAG, "Erreur Google ID Token: le jeton retourné est vide")
                    Result.failure(IllegalStateException("Le jeton d'authentification Google retourné est vide."))
                }
            } else {
                Log.e(TAG, "Erreur Credential Manager: type de credential inattendu (${credential::class.java.simpleName})")
                Result.failure(IllegalStateException("Type d'identifiant Google inattendu."))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: GetCredentialCancellationException) {
            Log.i(TAG, "Connexion Google annulée par l'utilisateur: ${e.message}")
            Result.failure(GoogleSignInCancelledException())
        } catch (e: NoCredentialException) {
            Log.w(TAG, "Aucun compte Google ou credential disponible sur l'appareil: ${e.message}")
            Result.failure(GoogleNoAccountException())
        } catch (e: Exception) {
            Log.e(TAG, "Erreur Credential Manager lors de la connexion Google: ${e.javaClass.simpleName} - ${e.message}", e)
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "GoogleSignInHelper"
    }
}
