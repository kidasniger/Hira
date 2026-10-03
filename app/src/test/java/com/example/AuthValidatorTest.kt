package com.example

import com.example.data.auth.AuthValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests unitaires JUnit purs pour le validateur d'authentification.
 */
class AuthValidatorTest {

    @Test
    fun `test validation email valide`() {
        assertTrue(AuthValidator.isValidEmail("contact@hira.chat"))
        assertTrue(AuthValidator.isValidEmail("user.name+tag@domain.co"))
        assertTrue(AuthValidator.isValidEmail("test1234@sub.example.com"))
    }

    @Test
    fun `test validation email invalide`() {
        assertFalse(AuthValidator.isValidEmail(null))
        assertFalse(AuthValidator.isValidEmail(""))
        assertFalse(AuthValidator.isValidEmail("   "))
        assertFalse(AuthValidator.isValidEmail("plainaddress"))
        assertFalse(AuthValidator.isValidEmail("@missingusername.com"))
        assertFalse(AuthValidator.isValidEmail("user@.com"))
        assertFalse(AuthValidator.isValidEmail("user@domain"))
    }

    @Test
    fun `test validation mot de passe minimum 8 caracteres`() {
        assertFalse(AuthValidator.isValidPassword(null))
        assertFalse(AuthValidator.isValidPassword(""))
        assertFalse(AuthValidator.isValidPassword("1234567"))
        assertTrue(AuthValidator.isValidPassword("12345678"))
        assertTrue(AuthValidator.isValidPassword("MonMotDePasseSecret2026!"))
    }

    @Test
    fun `test concordance des mots de passe`() {
        assertTrue(AuthValidator.doPasswordsMatch("monSecret123", "monSecret123"))
        assertFalse(AuthValidator.doPasswordsMatch("monSecret123", "autreSecret456"))
        assertFalse(AuthValidator.doPasswordsMatch("monSecret123", null))
        assertFalse(AuthValidator.doPasswordsMatch(null, "monSecret123"))
        assertFalse(AuthValidator.doPasswordsMatch("", ""))
    }
}
