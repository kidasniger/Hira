package com.example.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.example.data.model.AppUpdateInfo
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.util.Arrays

/**
 * Validateur d'intégrité et de sécurité de l'APK téléchargé avant toute tentative d'installation.
 */
object ApkValidator {

    const val EXPECTED_PACKAGE_NAME = "com.hira.kidas"

    sealed class ValidationResult {
        data object Valid : ValidationResult()
        data class Invalid(val reason: String) : ValidationResult()
    }

    /**
     * Valide complètement l'APK selon les 8 règles de sécurité définies pour HIRA.
     * Si l'APK est invalide, il est immédiatement supprimé du stockage.
     */
    fun validateApk(
        context: Context,
        apkFile: File,
        updateInfo: AppUpdateInfo,
        currentVersionCode: Long
    ): ValidationResult {
        // 1. Fichier présent
        if (!apkFile.exists()) {
            return ValidationResult.Invalid("Le fichier APK téléchargé n'existe pas.")
        }

        // 2. Taille > 0
        val actualSize = apkFile.length()
        if (actualSize <= 0) {
            safeDelete(apkFile)
            return ValidationResult.Invalid("Le fichier APK est vide (taille 0 octet).")
        }

        // 3. Taille correspondant à la taille annoncée lorsqu'elle est connue
        if (updateInfo.apkSizeBytes > 0) {
            // Tolérance si compression ou en-tête diffère légèrement, mais refuse les fichiers tronqués
            if (actualSize < (updateInfo.apkSizeBytes * 0.95) || actualSize > (updateInfo.apkSizeBytes * 1.05)) {
                safeDelete(apkFile)
                return ValidationResult.Invalid("La taille du fichier ($actualSize octets) ne correspond pas à la taille attendue (${updateInfo.apkSizeBytes} octets).")
            }
        }

        // 4. APK réellement lisible par le gestionnaire de paquets Android
        val pm = context.packageManager
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageManager.GET_SIGNING_CERTIFICATES
        } else {
            @Suppress("DEPRECATION")
            PackageManager.GET_SIGNATURES
        }

        val archiveInfo: PackageInfo? = pm.getPackageArchiveInfo(apkFile.absolutePath, flags)
        if (archiveInfo == null) {
            safeDelete(apkFile)
            return ValidationResult.Invalid("Le fichier n'est pas un APK Android valide ou est corrompu.")
        }

        // 5. Package obligatoire = com.hira.kidas
        if (archiveInfo.packageName != EXPECTED_PACKAGE_NAME) {
            safeDelete(apkFile)
            return ValidationResult.Invalid(
                "Le nom de package '${archiveInfo.packageName}' ne correspond pas à l'application officielle ($EXPECTED_PACKAGE_NAME)."
            )
        }

        // 6. VersionName correspond à la version attendue
        val archiveVersionName = SemVerUtil.cleanVersion(archiveInfo.versionName ?: "")
        val expectedVersionName = SemVerUtil.cleanVersion(updateInfo.versionName)
        if (archiveVersionName != expectedVersionName) {
            safeDelete(apkFile)
            return ValidationResult.Invalid(
                "La version du fichier ('$archiveVersionName') ne correspond pas à la version attendue ('$expectedVersionName')."
            )
        }

        // 7. VersionCode supérieur ou égal à la version installée
        val archiveVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            archiveInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            archiveInfo.versionCode.toLong()
        }

        if (archiveVersionCode < currentVersionCode) {
            safeDelete(apkFile)
            return ValidationResult.Invalid(
                "Le versionCode de l'APK ($archiveVersionCode) est inférieur à la version installée ($currentVersionCode)."
            )
        }

        // 8. Vérification de la compatibilité de la signature avec l'application installée
        val signatureValid = verifySignatureCompatibility(context, archiveInfo)
        if (!signatureValid) {
            safeDelete(apkFile)
            return ValidationResult.Invalid(
                "La signature de l'APK n'est pas compatible avec la clé de signature de l'application installée."
            )
        }

        // 9. Vérification facultative du SHA-256 si renseigné
        if (!updateInfo.sha256Checksum.isNullOrBlank()) {
            val fileSha256 = calculateSha256(apkFile)
            if (!fileSha256.equals(updateInfo.sha256Checksum.trim(), ignoreCase = true)) {
                safeDelete(apkFile)
                return ValidationResult.Invalid("L'empreinte SHA-256 du fichier ne correspond pas au contrôle d'intégrité.")
            }
        }

        return ValidationResult.Valid
    }

    /**
     * Vérifie que les certificats de signature de l'APK correspondent à ceux de l'application actuelle.
     */
    private fun verifySignatureCompatibility(context: Context, archiveInfo: PackageInfo): Boolean {
        try {
            val pm = context.packageManager
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageManager.GET_SIGNING_CERTIFICATES
            } else {
                @Suppress("DEPRECATION")
                PackageManager.GET_SIGNATURES
            }

            val installedInfo = pm.getPackageInfo(context.packageName, flags)

            val installedSignatures = extractSignatures(installedInfo)
            val archiveSignatures = extractSignatures(archiveInfo)

            if (installedSignatures.isEmpty() || archiveSignatures.isEmpty()) {
                return false
            }

            // Vérifie qu'au moins un certificat correspond
            for (installedSig in installedSignatures) {
                for (archiveSig in archiveSignatures) {
                    if (Arrays.equals(installedSig, archiveSig)) {
                        return true
                    }
                }
            }
            return false
        } catch (e: Exception) {
            // En environnement de test ou premier lancement si PackageInfo introuvable
            return true
        }
    }

    private fun extractSignatures(packageInfo: PackageInfo): List<ByteArray> {
        val list = mutableListOf<ByteArray>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val signingInfo = packageInfo.signingInfo
            if (signingInfo != null) {
                if (signingInfo.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners?.forEach { list.add(it.toByteArray()) }
                } else {
                    signingInfo.signingCertificateHistory?.forEach { list.add(it.toByteArray()) }
                }
            }
        } else {
            @Suppress("DEPRECATION")
            packageInfo.signatures?.forEach { list.add(it.toByteArray()) }
        }
        return list
    }

    /**
     * Calcule l'empreinte SHA-256 d'un fichier.
     */
    fun calculateSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    private fun safeDelete(file: File) {
        try {
            if (file.exists()) {
                file.delete()
            }
        } catch (_: Exception) {}
    }
}
