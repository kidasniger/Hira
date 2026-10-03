package com.example.data.repository

import android.content.Context
import com.hira.kidas.BuildConfig
import com.example.data.model.AppUpdateInfo
import com.example.data.model.UpdateCheckResult
import com.example.data.model.UpdateDownloadState
import com.example.data.remote.AppUpdateDownloadManager
import com.example.data.remote.GitHubUpdateService
import com.example.utils.ApkValidator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/**
 * Référentiel centralisé pour les vérifications et téléchargements de mises à jour HIRA.
 */
class UpdateRepository(
    private val context: Context,
    private val gitHubService: GitHubUpdateService = GitHubUpdateService(),
    private val downloadManager: AppUpdateDownloadManager = AppUpdateDownloadManager(context)
) {
    companion object {
        // Durée minimale entre deux vérifications automatiques (1 heure)
        private const val AUTO_CHECK_THROTTLE_INTERVAL_MS = 60 * 60 * 1000L
    }

    private var lastAutoCheckTimestamp: Long = 0L

    private val _updateState = MutableStateFlow<UpdateDownloadState>(UpdateDownloadState.Idle)
    val updateState: StateFlow<UpdateDownloadState> = _updateState.asStateFlow()

    private var cachedUpdateInfo: AppUpdateInfo? = null

    /**
     * Vérifie la présence d'une nouvelle version sur GitHub Releases.
     * @param force Si true, ignore le délai de temporisation (ex: déclenchement manuel dans les réglages).
     */
    suspend fun checkForUpdate(force: Boolean = false): UpdateCheckResult {
        val now = System.currentTimeMillis()
        if (!force && (now - lastAutoCheckTimestamp < AUTO_CHECK_THROTTLE_INTERVAL_MS)) {
            return UpdateCheckResult.Throttled("Vérification récente déjà effectuée.")
        }

        _updateState.value = UpdateDownloadState.Checking

        val currentVersionName = BuildConfig.VERSION_NAME
        val currentVersionCode = BuildConfig.VERSION_CODE.toLong()

        val result = gitHubService.checkLatestRelease(currentVersionName, currentVersionCode)
        lastAutoCheckTimestamp = System.currentTimeMillis()

        when (result) {
            is UpdateCheckResult.UpdateAvailable -> {
                cachedUpdateInfo = result.updateInfo

                // Vérifier si cet APK a déjà été téléchargé lors d'une session précédente (Partie 13)
                val existingApk = downloadManager.getExistingValidApk(result.updateInfo, currentVersionCode)
                if (existingApk != null) {
                    _updateState.value = UpdateDownloadState.ReadyToInstall(result.updateInfo, existingApk)
                } else {
                    _updateState.value = UpdateDownloadState.UpdateAvailablePrompt(result.updateInfo)
                }
            }
            is UpdateCheckResult.NoUpdateAvailable -> {
                _updateState.value = UpdateDownloadState.Idle
            }
            is UpdateCheckResult.Throttled -> {
                // Conserver l'état actuel
            }
            is UpdateCheckResult.Error -> {
                // En cas d'erreur de vérification automatique, ne pas bloquer l'application
                _updateState.value = UpdateDownloadState.Idle
            }
        }

        return result
    }

    /**
     * Démarre le téléchargement de la mise à jour actuellement disponible.
     */
    suspend fun startDownload(updateInfo: AppUpdateInfo) {
        val currentVersionCode = BuildConfig.VERSION_CODE.toLong()

        // Vérifier d'abord si l'APK est déjà présent et valide
        val existingApk = downloadManager.getExistingValidApk(updateInfo, currentVersionCode)
        if (existingApk != null) {
            _updateState.value = UpdateDownloadState.ReadyToInstall(updateInfo, existingApk)
            return
        }

        downloadManager.downloadApk(updateInfo).collect { progress ->
            when (progress) {
                is AppUpdateDownloadManager.DownloadProgress.Progress -> {
                    _updateState.value = UpdateDownloadState.Downloading(
                        updateInfo = updateInfo,
                        progressPercent = progress.percent,
                        bytesDownloaded = progress.bytesRead,
                        totalBytes = progress.totalBytes
                    )
                }
                is AppUpdateDownloadManager.DownloadProgress.Completed -> {
                    // Validation obligatoire de l'APK téléchargé avant installation (Partie 10)
                    _updateState.value = UpdateDownloadState.Validating(updateInfo)
                    val validation = ApkValidator.validateApk(
                        context = context,
                        apkFile = progress.apkFile,
                        updateInfo = updateInfo,
                        currentVersionCode = currentVersionCode
                    )

                    when (validation) {
                        is ApkValidator.ValidationResult.Valid -> {
                            _updateState.value = UpdateDownloadState.ReadyToInstall(updateInfo, progress.apkFile)
                        }
                        is ApkValidator.ValidationResult.Invalid -> {
                            _updateState.value = UpdateDownloadState.Error(
                                "Échec de validation de l'APK: ${validation.reason}"
                            )
                        }
                    }
                }
                is AppUpdateDownloadManager.DownloadProgress.Failed -> {
                    _updateState.value = UpdateDownloadState.Error(progress.error)
                }
            }
        }
    }

    /**
     * Ferme l'état d'affichage de la mise à jour (ex: bouton "Plus tard").
     */
    fun dismissUpdate() {
        _updateState.value = UpdateDownloadState.Idle
    }

    /**
     * Retourne l'info de mise à jour en cache si disponible.
     */
    fun getCachedUpdateInfo(): AppUpdateInfo? = cachedUpdateInfo
}
