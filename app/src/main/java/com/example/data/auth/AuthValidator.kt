package com.example.data.auth

/**
 * Validateur pur Kotlin pour les données d'authentification.
 * Sans dépendance au framework Android afin de permettre une exécution
 * immédiate et fiable en tests unitaires purs JUnit sur JVM.
 */
object AuthValidator {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    /**
     * Valide le format d'une adresse e-mail.
     */
    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        val trimmed = email.trim()
        return EMAIL_REGEX.matches(trimmed)
    }

    /**
     * Valide la longueur minimale du mot de passe (8 caractères requis).
     */
    fun isValidPassword(password: String?): Boolean {
        if (password == null) return false
        return password.length >= 8
    }

    /**
     * Vérifie la stricte concordance entre le mot de passe et sa confirmation.
     */
    fun doPasswordsMatch(password: String?, confirmPassword: String?): Boolean {
        if (password.isNullOrEmpty() || confirmPassword.isNullOrEmpty()) return false
        return password == confirmPassword
    }
}
