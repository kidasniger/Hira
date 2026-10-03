package com.example

import com.example.utils.SemVerUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests unitaires pour la comparaison de versions SemVer dans l'updater HIRA.
 */
class SemVerUtilTest {

    @Test
    fun `version nettoyee supprime les prefixes v`() {
        assertEquals("1.0.4", SemVerUtil.cleanVersion("v1.0.4"))
        assertEquals("1.0.4", SemVerUtil.cleanVersion("V1.0.4"))
        assertEquals("1.0.4", SemVerUtil.cleanVersion(" 1.0.4 "))
    }

    @Test
    fun `detection d une version plus recente`() {
        assertTrue(SemVerUtil.isNewerVersion("1.0.1", "1.0.0"))
        assertTrue(SemVerUtil.isNewerVersion("v1.0.2", "1.0.1"))
        assertTrue(SemVerUtil.isNewerVersion("1.1.0", "1.0.9"))
        assertTrue(SemVerUtil.isNewerVersion("2.0.0", "1.9.9"))
        assertTrue(SemVerUtil.isNewerVersion("1.0.10", "1.0.9"))
    }

    @Test
    fun `egalite de versions`() {
        assertEquals(0, SemVerUtil.compareVersions("1.0.0", "1.0.0"))
        assertEquals(0, SemVerUtil.compareVersions("v1.0.0", "1.0.0"))
        assertEquals(0, SemVerUtil.compareVersions("1.0", "1.0.0"))
        assertFalse(SemVerUtil.isNewerVersion("1.0.0", "1.0.0"))
        assertFalse(SemVerUtil.isNewerVersion("v1.0.4", "1.0.4"))
    }

    @Test
    fun `version distante plus ancienne`() {
        assertFalse(SemVerUtil.isNewerVersion("0.9.9", "1.0.0"))
        assertFalse(SemVerUtil.isNewerVersion("1.0.1", "1.0.2"))
        assertFalse(SemVerUtil.isNewerVersion("1.0.0", "2.0.0"))
        assertTrue(SemVerUtil.compareVersions("0.9.9", "1.0.0") < 0)
    }
}
