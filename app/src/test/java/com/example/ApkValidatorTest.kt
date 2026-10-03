package com.example

import com.example.data.model.AppUpdateInfo
import com.example.utils.ApkValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Tests unitaires pour les contrôles de validation APK avant installation.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ApkValidatorTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun `calcul sha256 valide sur contenu connu`() {
        val testFile = tempFolder.newFile("test.txt")
        testFile.writeText("HIRA_TEST_INTEGRITY")

        val sha256 = ApkValidator.calculateSha256(testFile)
        assertTrue(sha256.isNotBlank())
        assertEquals(64, sha256.length)
    }

    @Test
    fun `validation echoue si fichier inexistant`() {
        val nonExistentFile = File(tempFolder.root, "does_not_exist.apk")
        val updateInfo = AppUpdateInfo(
            versionName = "1.0.4",
            versionCode = 4,
            releaseTitle = "v1.0.4",
            releaseNotes = "",
            apkDownloadUrl = "http://example.com/apk",
            apkFileName = "Hira-v1.0.4.apk",
            apkSizeBytes = 1000,
            publishedAt = ""
        )

        val result = ApkValidator.validateApk(
            context = androidx.test.core.app.ApplicationProvider.getApplicationContext(),
            apkFile = nonExistentFile,
            updateInfo = updateInfo,
            currentVersionCode = 1
        )

        assertTrue(result is ApkValidator.ValidationResult.Invalid)
    }

    @Test
    fun `validation echoue si fichier vide de 0 octet`() {
        val emptyFile = tempFolder.newFile("empty.apk")
        val updateInfo = AppUpdateInfo(
            versionName = "1.0.4",
            versionCode = 4,
            releaseTitle = "v1.0.4",
            releaseNotes = "",
            apkDownloadUrl = "http://example.com/apk",
            apkFileName = "Hira-v1.0.4.apk",
            apkSizeBytes = 1000,
            publishedAt = ""
        )

        val result = ApkValidator.validateApk(
            context = androidx.test.core.app.ApplicationProvider.getApplicationContext(),
            apkFile = emptyFile,
            updateInfo = updateInfo,
            currentVersionCode = 1
        )

        assertTrue(result is ApkValidator.ValidationResult.Invalid)
    }
}
