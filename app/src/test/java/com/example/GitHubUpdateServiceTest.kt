package com.example

import com.example.data.model.UpdateCheckResult
import com.example.data.remote.GitHubUpdateService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Tests unitaires pour l'analyse des releases GitHub dans l'updater HIRA.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GitHubUpdateServiceTest {

    private val service = GitHubUpdateService()

    @Test
    fun `parsing release avec APK valide detecte une mise a jour`() {
        val sampleJson = """
        {
          "tag_name": "v1.0.4",
          "name": "HIRA v1.0.4",
          "body": "Correction de bugs et amélioration des performances.",
          "published_at": "2026-10-02T18:00:00Z",
          "assets": [
            {
              "name": "Hira-v1.0.4.apk",
              "browser_download_url": "https://github.com/kidasniger/Hira/releases/download/v1.0.4/Hira-v1.0.4.apk",
              "size": 8500000
            },
            {
              "name": "Hira-v1.0.4.apk.sha256",
              "browser_download_url": "https://github.com/kidasniger/Hira/releases/download/v1.0.4/Hira-v1.0.4.apk.sha256",
              "size": 65
            }
          ]
        }
        """.trimIndent()

        val result = service.parseReleaseJson(sampleJson, currentVersionName = "1.0.3", currentVersionCode = 3)

        assertTrue(result is UpdateCheckResult.UpdateAvailable)
        val update = (result as UpdateCheckResult.UpdateAvailable).updateInfo
        assertEquals("1.0.4", update.versionName)
        assertEquals(4L, update.versionCode)
        assertEquals("Hira-v1.0.4.apk", update.apkFileName)
        assertEquals("https://github.com/kidasniger/Hira/releases/download/v1.0.4/Hira-v1.0.4.apk", update.apkDownloadUrl)
        assertEquals(8500000L, update.apkSizeBytes)
    }

    @Test
    fun `parsing release avec version identique ne propose aucune mise a jour`() {
        val sampleJson = """
        {
          "tag_name": "v1.0.3",
          "name": "HIRA v1.0.3",
          "assets": [
            {
              "name": "Hira-v1.0.3.apk",
              "browser_download_url": "https://example.com/Hira-v1.0.3.apk",
              "size": 8000000
            }
          ]
        }
        """.trimIndent()

        val result = service.parseReleaseJson(sampleJson, currentVersionName = "1.0.3", currentVersionCode = 3)
        assertTrue(result is UpdateCheckResult.NoUpdateAvailable)
    }

    @Test
    fun `parsing release sans asset APK retourne une erreur lisible`() {
        val sampleJson = """
        {
          "tag_name": "v1.0.5",
          "name": "HIRA v1.0.5",
          "assets": [
            {
              "name": "source.zip",
              "browser_download_url": "https://example.com/source.zip",
              "size": 1000
            }
          ]
        }
        """.trimIndent()

        val result = service.parseReleaseJson(sampleJson, currentVersionName = "1.0.3", currentVersionCode = 3)
        assertTrue(result is UpdateCheckResult.Error)
    }

    @Test
    fun `parsing release avec liste assets vide retourne une erreur`() {
        val sampleJson = """
        {
          "tag_name": "v1.0.5",
          "name": "HIRA v1.0.5",
          "assets": []
        }
        """.trimIndent()

        val result = service.parseReleaseJson(sampleJson, currentVersionName = "1.0.3", currentVersionCode = 3)
        assertTrue(result is UpdateCheckResult.Error)
    }
}
