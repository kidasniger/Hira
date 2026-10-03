package com.example.utils

/**
 * Utilitaire de manipulation et comparaison de versions sémantiques (SemVer : MAJOR.MINOR.PATCH).
 */
object SemVerUtil {

    /**
     * Nettoie une chaîne de version en retirant le préfixe 'v' ou 'V' et les espaces superflus.
     */
    fun cleanVersion(version: String): String {
        return version.trim().removePrefix("v").removePrefix("V").trim()
    }

    /**
     * Compare deux versions au format MAJOR.MINOR.PATCH.
     * @return > 0 si version1 > version2,
     *         0 si version1 == version2,
     *         < 0 si version1 < version2.
     */
    fun compareVersions(version1: String, version2: String): Int {
        val clean1 = cleanVersion(version1)
        val clean2 = cleanVersion(version2)

        // Extraction des chiffres principaux en ignorant les suffixes de pré-version (ex: -beta)
        val parts1 = clean1.split("-")[0].split(".").mapNotNull { it.toIntOrNull() }
        val parts2 = clean2.split("-")[0].split(".").mapNotNull { it.toIntOrNull() }

        val maxLength = maxOf(parts1.size, parts2.size, 3)

        for (i in 0 until maxLength) {
            val v1 = parts1.getOrElse(i) { 0 }
            val v2 = parts2.getOrElse(i) { 0 }

            if (v1 != v2) {
                return v1.compareTo(v2)
            }
        }
        return 0
    }

    /**
     * Indique si [remoteVersion] est strictement plus récente que [currentVersion].
     */
    fun isNewerVersion(remoteVersion: String, currentVersion: String): Boolean {
        return compareVersions(remoteVersion, currentVersion) > 0
    }
}
