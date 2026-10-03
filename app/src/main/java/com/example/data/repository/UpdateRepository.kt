package com.example.data.repository

import android.content.Context
import com.example.data.model.AppUpdateInfo
import com.example.data.model.UpdateCheckResult
import com.example.data.model.UpdateDownloadState
import com.example.data.remote.AppUpdateDownloadManager
import com.example.data.remote.GitHubUpdateService
import com.example.utils.ApkValidator
import com.hira.kidas.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Référentiel centralisé pour les vérifications et téléchargements de mises à jour HIRA.
 */
class UpdateRepository(
    private val context: Context,
    private val gitHubService: GitHubUpdateService = GitHubUpdateService(),
    private val downloadManager: AppUpdateDownloadManager = AppUpdateDownloadManager(context)
) {
    companion object {
        private const val AUTO_CHECK_THROTTLE_INTERVAL_MS = 60 * 60 * 1000L
    }

    private var lastAutoCheckTimestamp: Long = 0L
    private val checkMutex = Mutex()

    private val _updateState = MutableStateFlow<UpdateDownloadState>(UpdateDownloadState.Idle)
    val updateState: StateFlow<UpdateDownloadState> = _updateState.asStateFlow()

    private var cachedUpdateInfo: AppUpdateInfo? = null

    /**
     * Vérifie la présence d'une nouvelle version sur GitHub Releases.
     * Une seule vérification réseau peut être active à la fois.
     */
    suspend fun checkForUpdate(force: Boolean = false): UpdateCheckResult = checkMutex.withLock {
        val now = System.currentTimeMillis()
        if (!force && (now - lastAutoCheckTimestamp < AUTO_CHECK_THROTTLE_INTERVAL_MS)) {
            return@withLock UpdateCheckResult.Throttled("Vérification récente déjà effectuée.")
        }

        _updateState.value = UpdateDownloadState.Checking

        val currentVersionName = BuildConfig.VERSION_NAME
        val currentVersionCode = BuildConfig.VERSION_CODE.toLong()

        val result = gitHubService.checkLatestRelease(currentVersionName, currentVersionCode)
        lastAutoCheckTimestamp = System.currentTimeMillis()

        when (result) {
            is UpdateCheckResult.UpdateAvailable -> {
                cachedUpdateInfo = result.updateInfo

                // La validation d'un APK existant ne doit jamais bloquer le thread UI.
                val existingApk = withContext(Dispatchers.IO) {
                    downloadManager.getExistingValidApk(result.updateInfo, currentVersionCode)
                }

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
                // Conserver l'état actuel.
            }

            is UpdateCheckResult.Error -> {
                // Une erreur de vérification ne doit pas bloquer l'application.
                _updateState.value = UpdateDownloadState.Idle
            }
        }

        return@withLock result
    }

    /**
     * Démarre le téléchargement.
     * L'état "Downloading" est publié immédiatement pour fournir un retour visuel
     * même lorsque GitHub met quelques secondes à répondre.
     */
    suspend fun startDownload(updateInfo: AppUpdateInfo) {
        val currentVersionCode = BuildConfig.VERSION_CODE.toLong()

        _updateState.value = UpdateDownloadState.Downloading(
            updateInfo = updateInfo,
            progressPercent = 0,
            bytesDownloaded = 0L,
            totalBytes = updateInfo.apkSizeBytes
        )

        val existingApk = withContext(Dispatchers.IO) {
            downloadManager.getExistingValidApk(updateInfo, currentVersionCode)
        }

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
                    _updateState.value = UpdateDownloadState.Validating(updateInfo)

                    val validation = withContext(Dispatchers.IO) {
                        ApkValidator.validateApk(
                            context = context,
                            apkFile = progress.apkFile,
                            updateInfo = updateInfo,
                            currentVersionCode = currentVersionCode
                        )
                    }

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

    fun dismissUpdate() {
        _updateState.value = UpdateDownloadState.Idle
    }

    fun getCachedUpdateInfo(): AppUpdateInfo? = cachedUpdateInfo
}
