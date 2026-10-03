package com.example.data.model

/**
 * Informations sur une mise à jour d'application disponible.
 */
data class AppUpdateInfo(
    val versionName: String,
    val versionCode: Long,
    val releaseTitle: String,
    val releaseNotes: String,
    val apkDownloadUrl: String,
    val apkFileName: String,
    val apkSizeBytes: Long,
    val publishedAt: String,
    val sha256Checksum: String? = null,
    val isForceUpdate: Boolean = false
) {
    /**
     * Taille formatée lisible pour l'utilisateur (ex: 8.5 Mo).
     */
    val formattedSize: String
        get() {
            if (apkSizeBytes <= 0) return "Taille inconnue"
            val mb = apkSizeBytes.toDouble() / (1024 * 1024)
            return "%.1f Mo".format(java.util.Locale.FRENCH, mb)
        }
}
