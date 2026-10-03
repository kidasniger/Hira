package com.example.ui.update

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppUpdateInfo
import com.example.data.model.UpdateDownloadState
import com.example.data.repository.UpdateRepository
import com.example.utils.ApkInstaller
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel centralisant la gestion des mises à jour applicatives HIRA.
 */
class UpdateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UpdateRepository(application.applicationContext)
    val updateState: StateFlow<UpdateDownloadState> = repository.updateState

    /**
     * Déclenche une vérification de mise à jour.
     */
    fun checkForUpdates(force: Boolean = false) {
        viewModelScope.launch {
            repository.checkForUpdate(force = force)
        }
    }

    /**
     * Lance le téléchargement de la mise à jour.
     */
    fun startDownload(updateInfo: AppUpdateInfo) {
        viewModelScope.launch {
            repository.startDownload(updateInfo)
        }
    }

    /**
     * Tente de lancer l'installation de l'APK validé via Android PackageInstaller.
     */
    fun installUpdate(apkFile: File): Boolean {
        val context = getApplication<Application>().applicationContext

        if (!ApkInstaller.canRequestPackageInstalls(context)) {
            ApkInstaller.openInstallPermissionSettings(context)
            return false
        }

        val result = ApkInstaller.installApk(context, apkFile)
        return result.isSuccess
    }

    /**
     * Ouvre les paramètres système pour autoriser l'installation.
     */
    fun requestInstallPermission() {
        val context = getApplication<Application>().applicationContext
        ApkInstaller.openInstallPermissionSettings(context)
    }

    /**
     * Ferme l'interface de mise à jour.
     */
    fun dismiss() {
        repository.dismissUpdate()
    }
}
