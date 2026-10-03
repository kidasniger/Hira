package com.example.data.remote

import com.example.data.model.AppUpdateInfo
import com.example.data.model.UpdateCheckResult
import com.example.utils.SemVerUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Service d'interrogation de l'API GitHub Releases pour détecter les nouvelles versions de HIRA.
 */
class GitHubUpdateService(
    private val releaseApiUrl: String = DEFAULT_GITHUB_API_URL
) {
    companion object {
        const val DEFAULT_GITHUB_API_URL = "https://api.github.com/repos/kidasniger/Hira/releases/latest"
        private const val CONNECT_TIMEOUT_MS = 8000
        private const val READ_TIMEOUT_MS = 12000
    }

    /**
     * Interroge l'API GitHub et compare avec la version actuellement installée.
     * @param currentVersionName Version actuelle (ex: BuildConfig.VERSION_NAME)
     * @param currentVersionCode Code de version actuel (ex: BuildConfig.VERSION_CODE)
     */
    suspend fun checkLatestRelease(
        currentVersionName: String,
        currentVersionCode: Long
    ): UpdateCheckResult = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(releaseApiUrl)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                setRequestProperty("User-Agent", "HIRA-Android-Updater")
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                // Aucune release publiée sur le dépôt
                return@withContext UpdateCheckResult.NoUpdateAvailable
            }

            if (responseCode !in 200..299) {
                return@withContext UpdateCheckResult.Error(
                    "GitHub API a répondu avec le code HTTP $responseCode"
                )
            }

            val responseBody = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
            parseReleaseJson(responseBody, currentVersionName, currentVersionCode)
        } catch (e: Exception) {
            UpdateCheckResult.Error(
                message = e.localizedMessage ?: "Erreur de connexion lors de la vérification de mise à jour",
                throwable = e
            )
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Parse la réponse JSON d'une GitHub Release et extrait l'APK approprié.
     */
    fun parseReleaseJson(
        jsonString: String,
        currentVersionName: String,
        currentVersionCode: Long
    ): UpdateCheckResult {
        return try {
            val json = JSONObject(jsonString)

            val tagName = json.optString("tag_name", "").trim()
            if (tagName.isEmpty()) {
                return UpdateCheckResult.NoUpdateAvailable
            }

            val cleanRemoteVersion = SemVerUtil.cleanVersion(tagName)
            val cleanCurrentVersion = SemVerUtil.cleanVersion(currentVersionName)

            // Règle PARTIE 6 : Une mise à jour n'est disponible que si version distante > version installée
            if (!SemVerUtil.isNewerVersion(cleanRemoteVersion, cleanCurrentVersion)) {
                return UpdateCheckResult.NoUpdateAvailable
            }

            val releaseTitle = json.optString("name", "HIRA v$cleanRemoteVersion")
            val releaseNotes = json.optString("body", "Nouvelle mise à jour disponible.")
            val publishedAt = json.optString("published_at", "")

            // Recherche de l'asset APK dans la liste
            val assetsArray = json.optJSONArray("assets")
            if (assetsArray == null || assetsArray.length() == 0) {
                return UpdateCheckResult.Error("La release $tagName ne contient aucun fichier APK joint.")
            }

            var apkUrl: String? = null
            var apkName: String? = null
            var apkSize: Long = 0
            var sha256Url: String? = null

            // Priorité : recherche un fichier qui commence par 'Hira' et finit par '.apk'
            for (i in 0 until assetsArray.length()) {
                val asset = assetsArray.getJSONObject(i)
                val name = asset.optString("name", "")
                val downloadUrl = asset.optString("browser_download_url", "")
                val size = asset.optLong("size", 0L)

                if (name.endsWith(".apk", ignoreCase = true)) {
                    if (apkUrl == null || name.startsWith("Hira", ignoreCase = true)) {
                        apkUrl = downloadUrl
                        apkName = name
                        apkSize = size
                    }
                } else if (name.endsWith(".sha256", ignoreCase = true)) {
                    sha256Url = downloadUrl
                }
            }

            if (apkUrl.isNullOrEmpty() || apkName.isNullOrEmpty()) {
                return UpdateCheckResult.Error("Aucun asset APK n'a été trouvé dans la release $tagName.")
            }

            // Calcul du versionCode cible : doit être supérieur au versionCode actuel
            val targetVersionCode = currentVersionCode + 1

            UpdateCheckResult.UpdateAvailable(
                AppUpdateInfo(
                    versionName = cleanRemoteVersion,
                    versionCode = targetVersionCode,
                    releaseTitle = releaseTitle,
                    releaseNotes = releaseNotes,
                    apkDownloadUrl = apkUrl,
                    apkFileName = apkName,
                    apkSizeBytes = apkSize,
                    publishedAt = publishedAt,
                    sha256Checksum = null,
                    isForceUpdate = false
                )
            )
        } catch (e: Exception) {
            UpdateCheckResult.Error("Échec du traitement des données de mise à jour: ${e.message}", e)
        }
    }
}
