package com.example.data.auth

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Référentiel d'authentification pour HIRA.
 * - Initialise FirebaseAuth uniquement à la demande pour éviter les plantages
 * - Convertit les tâches Firebase en coroutines avec suspension annulable maison
 * - Relance systématiquement CancellationException
 */
class AuthRepository(
    private val firebaseAuthSupplier: () -> FirebaseAuth = { FirebaseAuth.getInstance() }
) {

    private fun getAuth(): FirebaseAuth {
        return try {
            firebaseAuthSupplier()
        } catch (e: Exception) {
            Log.e(TAG, "Échec lors de l'accès à FirebaseAuth: ${e.message}", e)
            throw e
        }
    }

    /**
     * Inscription par e-mail et mot de passe.
     */
    suspend fun signUp(email: String, password: String): Result<AuthUser> {
        return try {
            val auth = getAuth()
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).awaitTask()
            val user = result.user ?: throw IllegalStateException("Utilisateur introuvable après création.")
            Log.i(TAG, "Utilisateur créé avec succès (UID: ${user.uid})")
            Result.success(
                AuthUser(
                    uid = user.uid,
                    email = user.email,
                    isNewUser = true,
                    displayName = user.displayName,
                    photoUrl = user.photoUrl?.toString()
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val errorCode = (e as? FirebaseAuthException)?.errorCode
            Log.e(TAG, "signUp failed - Class: ${e.javaClass.name}, Message: ${e.message}, ErrorCode: $errorCode", e)
            Result.failure(e)
        }
    }

    /**
     * Connexion par e-mail et mot de passe.
     */
    suspend fun signIn(email: String, password: String): Result<AuthUser> {
        return try {
            val auth = getAuth()
            val result = auth.signInWithEmailAndPassword(email.trim(), password).awaitTask()
            val user = result.user ?: throw IllegalStateException("Utilisateur introuvable après connexion.")
            Log.i(TAG, "Utilisateur connecté avec succès par e-mail (UID: ${user.uid})")
            Result.success(
                AuthUser(
                    uid = user.uid,
                    email = user.email,
                    isNewUser = false,
                    displayName = user.displayName,
                    photoUrl = user.photoUrl?.toString()
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val errorCode = (e as? FirebaseAuthException)?.errorCode
            Log.e(TAG, "signIn failed - Class: ${e.javaClass.name}, Message: ${e.message}, ErrorCode: $errorCode", e)
            Result.failure(e)
        }
    }

    /**
     * Connexion avec identifiant Google (idToken).
     */
    suspend fun signInWithGoogle(idToken: String): Result<AuthUser> {
        return try {
            val auth = getAuth()
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).awaitTask()
            val user = result.user ?: throw IllegalStateException("Utilisateur introuvable après connexion Google.")
            val isNew = result.additionalUserInfo?.isNewUser ?: false
            Log.i(TAG, "Connexion Google réussie dans Firebase Auth (UID: ${user.uid}, isNewUser: $isNew)")
            Result.success(
                AuthUser(
                    uid = user.uid,
                    email = user.email,
                    isNewUser = isNew,
                    displayName = user.displayName,
                    photoUrl = user.photoUrl?.toString()
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val errorCode = (e as? FirebaseAuthException)?.errorCode
            Log.e(
                TAG,
                "signInWithGoogle failed - Class: ${e.javaClass.name}, Message: ${e.message}, ErrorCode: $errorCode",
                e
            )
            Result.failure(e)
        }
    }

    /**
     * Envoi d'un e-mail de réinitialisation de mot de passe.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            val auth = getAuth()
            auth.sendPasswordResetEmail(email.trim()).awaitTask()
            Log.i(TAG, "E-mail de réinitialisation envoyé avec succès")
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val errorCode = (e as? FirebaseAuthException)?.errorCode
            Log.e(TAG, "sendPasswordResetEmail failed - Class: ${e.javaClass.name}, Message: ${e.message}, ErrorCode: $errorCode", e)
            Result.failure(e)
        }
    }

    /**
     * Déconnexion de l'utilisateur.
     */
    fun signOut() {
        try {
            getAuth().signOut()
            Log.i(TAG, "Déconnexion Firebase Auth effectuée")
        } catch (e: Exception) {
            Log.w(TAG, "Erreur lors de la déconnexion Firebase: ${e.message}")
        }
    }

    /**
     * Vérifie si un utilisateur est déjà connecté.
     */
    fun isSignedIn(): Boolean {
        return try {
            getAuth().currentUser != null
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Retourne l'adresse e-mail de l'utilisateur actuellement connecté, le cas échéant.
     */
    fun currentEmail(): String? {
        return try {
            getAuth().currentUser?.email
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Helper suspendCancellableCoroutine maison pour attendre les tâches Task Firebase
     * sans ajouter de bibliothèque supplémentaire.
     */
    private suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (continuation.isCancelled) return@addOnCompleteListener
            if (task.isSuccessful) {
                continuation.resume(task.result)
            } else {
                val exception = task.exception ?: Exception("Échec de l'opération Firebase.")
                continuation.resumeWithException(exception)
            }
        }
    }

    companion object {
        private const val TAG = "AuthRepository"
    }
}
