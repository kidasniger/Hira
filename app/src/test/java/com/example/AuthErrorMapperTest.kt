package com.example

import com.example.data.auth.AuthErrorMapper
import com.hira.kidas.R
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests unitaires JUnit purs pour le mappage des erreurs Firebase Authentication.
 */
class AuthErrorMapperTest {

    @Test
    fun `test conversion code ERROR_INVALID_CREDENTIAL`() {
        val res = AuthErrorMapper.fromCode("ERROR_INVALID_CREDENTIAL")
        assertEquals(R.string.auth_error_invalid_credential, res)
    }

    @Test
    fun `test conversion code ERROR_WRONG_PASSWORD`() {
        val res = AuthErrorMapper.fromCode("ERROR_WRONG_PASSWORD")
        assertEquals(R.string.auth_error_wrong_password, res)
    }

    @Test
    fun `test conversion code ERROR_USER_NOT_FOUND`() {
        val res = AuthErrorMapper.fromCode("ERROR_USER_NOT_FOUND")
        assertEquals(R.string.auth_error_user_not_found, res)
    }

    @Test
    fun `test conversion code ERROR_EMAIL_ALREADY_IN_USE`() {
        val res = AuthErrorMapper.fromCode("ERROR_EMAIL_ALREADY_IN_USE")
        assertEquals(R.string.auth_error_email_already_in_use, res)
    }

    @Test
    fun `test conversion code ERROR_WEAK_PASSWORD`() {
        val res = AuthErrorMapper.fromCode("ERROR_WEAK_PASSWORD")
        assertEquals(R.string.auth_error_weak_password, res)
    }

    @Test
    fun `test conversion code ERROR_NETWORK_REQUEST_FAILED`() {
        val res = AuthErrorMapper.fromCode("ERROR_NETWORK_REQUEST_FAILED")
        assertEquals(R.string.auth_error_network, res)
    }

    @Test
    fun `test conversion code ERROR_TOO_MANY_REQUESTS`() {
        val res = AuthErrorMapper.fromCode("ERROR_TOO_MANY_REQUESTS")
        assertEquals(R.string.auth_error_too_many_requests, res)
    }

    @Test
    fun `test conversion code ERROR_OPERATION_NOT_ALLOWED`() {
        val res = AuthErrorMapper.fromCode("ERROR_OPERATION_NOT_ALLOWED")
        assertEquals(R.string.auth_error_operation_not_allowed, res)
    }

    @Test
    fun `test conversion code standard en minuscules et tirets`() {
        assertEquals(R.string.auth_error_invalid_credential, AuthErrorMapper.fromCode("invalid-credential"))
        assertEquals(R.string.auth_error_user_not_found, AuthErrorMapper.fromCode("user-not-found"))
        assertEquals(R.string.auth_error_wrong_password, AuthErrorMapper.fromCode("wrong-password"))
        assertEquals(R.string.auth_error_email_already_in_use, AuthErrorMapper.fromCode("email-already-in-use"))
    }

    @Test
    fun `test conversion code inconnu ou nul`() {
        assertEquals(R.string.auth_error_generic, AuthErrorMapper.fromCode(null))
        assertEquals(R.string.auth_error_generic, AuthErrorMapper.fromCode("CODE_INCONNU_12345"))
    }
}
