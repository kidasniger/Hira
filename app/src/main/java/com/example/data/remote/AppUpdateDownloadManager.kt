package com.example.data.remote

import android.content.Context
import android.os.Environment
import com.example.data.model.AppUpdateInfo
import com.example.utils.ApkValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Gestionnaire de téléchargement d'APK pour HIRA.
 * Prévient les téléchargements en doublon et gère le suivi de la progression.
 */
class AppUpdateDownloadManager(private val context: Context) {

    /**
     * Dossier sécurisé de stockage des APKs de mise à jour.
     */
    fun getUpdatesDir(): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "updates")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Nom du fichier APK ciblé pour une version donnée.
     */
    fun getTargetApkFile(updateInfo: AppUpdateInfo): File {
        val safeFileName = if (updateInfo.apkFileName.isNotBlank()) {
            updateInfo.apkFileName
        } else {
            "Hira-v${updateInfo.versionName}.apk"
        }
        return File(getUpdatesDir(), safeFileName)
    }

    /**
     * Vérifie si un APK déjà téléchargé pour cette version existe et est valide.
     * Si oui, évite tout re-téléchargement inutile (Partie 13).
     */
    fun getExistingValidApk(updateInfo: AppUpdateInfo, currentVersionCode: Long): File? {
        val file = getTargetApkFile(updateInfo)
        if (file.exists() && file.length() > 0) {
            val validation = ApkValidator.validateApk(context, file, updateInfo, currentVersionCode)
            if (validation is ApkValidator.ValidationResult.Valid) {
                return file
            }
        }
        return null
    }

    sealed class DownloadProgress {
        data class Progress(val bytesRead: Long, val totalBytes: Long, val percent: Int) : DownloadProgress()
        data class Completed(val apkFile: File) : DownloadProgress()
        data class Failed(val error: String) : DownloadProgress()
    }

    /**
     * Télécharge l'APK avec suivi de la progression en temps réel.
     */
    fun downloadApk(updateInfo: AppUpdateInfo): Flow<DownloadProgress> = flow {
        val targetFile = getTargetApkFile(updateInfo)
        val tempFile = File(targetFile.parentFile, "${targetFile.name}.download")

        var connection: HttpURLConnection? = null
        try {
            val url = URL(updateInfo.apkDownloadUrl)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 15000
                readTimeout = 30000
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", "HIRA-Android-Updater")
            }

            // Gestion des redirections HTTP (301, 302, etc. très fréquentes sur GitHub Releases)
            var redirectCount = 0
            while (connection?.responseCode in 300..399 && redirectCount < 5) {
                val newUrl = connection?.getHeaderField("Location") ?: break
                connection?.disconnect()
                connection = (URL(newUrl).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 15000
                    readTimeout = 30000
                    setRequestProperty("User-Agent", "HIRA-Android-Updater")
                }
                redirectCount++
            }

            val responseCode = connection?.responseCode ?: -1
            if (responseCode !in 200..299) {
                emit(DownloadProgress.Failed("Échec du téléchargement (HTTP $responseCode)"))
                return@flow
            }

            val contentLength = connection?.contentLengthLong ?: updateInfo.apkSizeBytes
            val inputStream = connection?.inputStream ?: throw IllegalStateException("Flux de données vide")

            if (tempFile.exists()) {
                tempFile.delete()
            }

            FileOutputStream(tempFile).use { outputStream ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                var totalBytesRead = 0L
                var lastReportedPercent = -1

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead

                    val percent = if (contentLength > 0) {
                        ((totalBytesRead * 100) / contentLength).toInt().coerceIn(0, 100)
                    } else {
                        0
                    }

                    if (percent != lastReportedPercent) {
                        lastReportedPercent = percent
                        emit(DownloadProgress.Progress(totalBytesRead, contentLength, percent))
                    }
                }
                outputStream.flush()
            }

            // Renommer le fichier temporaire vers le fichier final
            if (targetFile.exists()) {
                targetFile.delete()
            }
            if (!tempFile.renameTo(targetFile)) {
                tempFile.copyTo(targetFile, overwrite = true)
                tempFile.delete()
            }

            emit(DownloadProgress.Completed(targetFile))
        } catch (e: Exception) {
            if (tempFile.exists()) {
                tempFile.delete()
            }
            emit(DownloadProgress.Failed(e.localizedMessage ?: "Erreur réseau pendant le téléchargement"))
        } finally {
            connection?.disconnect()
        }
    }.flowOn(Dispatchers.IO)
}
