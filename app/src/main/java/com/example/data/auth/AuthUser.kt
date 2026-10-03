package com.example.data.auth

/**
 * Données d'un utilisateur authentifié dans HIRA.
 */
data class AuthUser(
    val uid: String,
    val email: String?,
    val isNewUser: Boolean
)
