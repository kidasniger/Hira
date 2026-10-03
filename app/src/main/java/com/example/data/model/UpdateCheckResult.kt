package com.example.data.model

import java.io.File

/**
 * Résultat d'une vérification de mise à jour auprès de GitHub Releases.
 */
sealed class UpdateCheckResult {
    data object NoUpdateAvailable : UpdateCheckResult()
    data class UpdateAvailable(val updateInfo: AppUpdateInfo) : UpdateCheckResult()
    data class Throttled(val message: String) : UpdateCheckResult()
    data class Error(val message: String, val throwable: Throwable? = null) : UpdateCheckResult()
}

/**
 * État du processus de mise à jour au niveau UI.
 */
sealed class UpdateDownloadState {
    data object Idle : UpdateDownloadState()
    data object Checking : UpdateDownloadState()
    data class UpdateAvailablePrompt(val updateInfo: AppUpdateInfo) : UpdateDownloadState()
    data class Downloading(
        val updateInfo: AppUpdateInfo,
        val progressPercent: Int,
        val bytesDownloaded: Long,
        val totalBytes: Long
    ) : UpdateDownloadState() {
        val downloadedFormatted: String
            get() {
                val mbDownloaded = bytesDownloaded.toDouble() / (1024 * 1024)
                val mbTotal = totalBytes.toDouble() / (1024 * 1024)
                return "%.1f Mo / %.1f Mo".format(java.util.Locale.FRENCH, mbDownloaded, mbTotal)
            }
    }
    data class Validating(val updateInfo: AppUpdateInfo) : UpdateDownloadState()
    data class ReadyToInstall(val updateInfo: AppUpdateInfo, val apkFile: File) : UpdateDownloadState()
    data class Error(val message: String, val canRetry: Boolean = true) : UpdateDownloadState()
}
