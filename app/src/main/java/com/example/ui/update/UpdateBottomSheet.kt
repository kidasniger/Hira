package com.example.ui.update

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppUpdateInfo
import com.example.data.model.UpdateDownloadState
import com.example.ui.components.HiraLogo
import com.example.ui.theme.HiraBorder
import com.example.ui.theme.HiraGrayDark
import com.example.ui.theme.HiraGrayLight
import com.example.ui.theme.HiraGrayMedium
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraWhite
import com.example.utils.ApkInstaller

/**
 * Interface modale de mise à jour HIRA.
 * Respecte strictement la charte graphique : flat design, bleu royal #0043FF, blanc et gris neutres.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateBottomSheet(
    state: UpdateDownloadState,
    onDownloadClick: (AppUpdateInfo) -> Unit,
    onInstallClick: (java.io.File) -> Unit,
    onDismiss: () -> Unit,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    if (state is UpdateDownloadState.Idle || state is UpdateDownloadState.Checking) {
        return
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = HiraWhite,
        tonalElevation = 0.dp,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .background(HiraBorder, RoundedCornerShape(2.dp))
            )
        },
        modifier = modifier.testTag("update_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state) {
                is UpdateDownloadState.UpdateAvailablePrompt -> {
                    UpdatePromptContent(
                        updateInfo = state.updateInfo,
                        onDownloadClick = { onDownloadClick(state.updateInfo) },
                        onDismiss = onDismiss
                    )
                }
                is UpdateDownloadState.Downloading -> {
                    DownloadingContent(state = state)
                }
                is UpdateDownloadState.Validating -> {
                    ValidatingContent(updateInfo = state.updateInfo)
                }
                is UpdateDownloadState.Installing -> {
                    InstallingContent()
                }
                is UpdateDownloadState.ReadyToInstall -> {
                    ReadyToInstallContent(
                        onInstallClick = { onInstallClick(state.apkFile) },
                        onRequestPermission = onRequestPermission,
                        onDismiss = onDismiss
                    )
                }
                is UpdateDownloadState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onDismiss = onDismiss
                    )
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun UpdatePromptContent(
    updateInfo: AppUpdateInfo,
    onDownloadClick: () -> Unit,
    onDismiss: () -> Unit
) {
    // En-tête avec logo officiel HIRA
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(HiraRoyalBlue, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        HiraLogo(size = 40.dp, isAnimated = false)
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Mise à jour disponible",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF111111),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Spacer(modifier = Modifier.height(20.dp))

    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = HiraGrayLight
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Ce qui a été fait",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111111)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = userSafeReleaseNotes(updateInfo.releaseNotes),
                fontSize = 13.sp,
                color = HiraGrayDark,
                lineHeight = 18.sp
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Boutons d'action
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("update_later_button"),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, HiraBorder)
        ) {
            Text(
                text = "Plus tard",
                color = Color(0xFF111111),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = onDownloadClick,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("update_download_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = HiraRoyalBlue,
                contentColor = HiraWhite
            )
        ) {
            Text(
                text = "Télécharger",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DownloadingContent(state: UpdateDownloadState.Downloading) {
    Text(
        text = "Téléchargement de la mise à jour",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF111111)
    )

    Spacer(modifier = Modifier.height(16.dp))

    LinearProgressIndicator(
        progress = { state.progressPercent / 100f },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .testTag("update_progress_bar"),
        color = HiraRoyalBlue,
        trackColor = Color(0xFFE5E7EB),
    )

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
    ) {
        Text(
            text = "${state.progressPercent}%",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = HiraRoyalBlue
        )
        Text(
            text = state.downloadedFormatted,
            fontSize = 13.sp,
            color = HiraGrayMedium
        )
    }

    Spacer(modifier = Modifier.height(12.dp))



    Text(
        text = "Le téléchargement se poursuit en arrière-plan.",
        fontSize = 12.sp,
        color = HiraGrayMedium,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ValidatingContent(updateInfo: AppUpdateInfo) {
    Text(
        text = "Vérification de la mise à jour",
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF111111)
    )
    Spacer(modifier = Modifier.height(12.dp))
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp),
        color = HiraRoyalBlue,
        trackColor = HiraGrayLight
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Contrôle de l'intégrité et de la signature de l'APK...",
        fontSize = 12.sp,
        color = HiraGrayMedium,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ReadyToInstallContent(
    onInstallClick: () -> Unit,
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val hasInstallPermission = ApkInstaller.canRequestPackageInstalls(context)

    Text(
        text = "Mise à jour prête",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF111111)
    )

    Spacer(modifier = Modifier.height(8.dp))

    Spacer(modifier = Modifier.height(20.dp))

    // Si Android 8.0+ exige l'autorisation de source inconnue
    if (!hasInstallPermission) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFFBEB),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Autorisation requise par Android",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pour appliquer la mise à jour, vous devez autoriser HIRA à installer des applications dans les paramètres.",
                    fontSize = 12.sp,
                    color = Color(0xFF78350F),
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Ouvrir les paramètres", fontSize = 12.sp, color = HiraWhite)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    Button(
        onClick = onInstallClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("update_install_button"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = HiraRoyalBlue,
            contentColor = HiraWhite
        )
    ) {
        Text(
            text = "Installer la mise à jour",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedButton(
        onClick = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, HiraBorder)
    ) {
        Text(
            text = "Plus tard",
            fontSize = 14.sp,
            color = Color(0xFF111111)
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onDismiss: () -> Unit
) {
    Text(
        text = "Mise à jour",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF111111)
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = message,
        fontSize = 14.sp,
        color = Color(0xFFDC2626),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(20.dp))

    Button(
        onClick = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = HiraRoyalBlue)
    ) {
        Text("Fermer", color = HiraWhite, fontWeight = FontWeight.SemiBold)
    }
}


@Composable
private fun HiraUpdateLogo() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(HiraRoyalBlue, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        HiraLogo(size = 40.dp, isAnimated = false)
    }
}

@Composable
private fun InstallingContent() {
    HiraUpdateLogo()

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Installation de la mise à jour en cours",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF111111),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(18.dp))

    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .testTag("update_install_progress"),
        color = HiraRoyalBlue,
        trackColor = Color(0xFFE5E7EB)
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = "HIRA applique la mise à jour.",
        fontSize = 12.sp,
        color = HiraGrayMedium,
        textAlign = TextAlign.Center
    )
}

private fun userSafeReleaseNotes(rawNotes: String): String {
    val cleaned = rawNotes
        .lineSequence()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .filterNot { it.startsWith("#") }
        .filterNot { it.contains("What's Changed", ignoreCase = true) }
        .filterNot { it.contains("Full Changelog", ignoreCase = true) }
        .map { line ->
            line
                .replace(Regex("https?://\\S+"), "")
                .replace(Regex("\\[([^]]+)\\]\\([^)]*\\)"), "$1")
                .replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1")
                .removePrefix("*")
                .removePrefix("-")
                .trim()
        }
        .filter { it.isNotBlank() }
        .map(::translateReleaseChange)
        .distinct()
        .take(5)
        .toList()

    return if (cleaned.isEmpty()) {
        "Améliorations et corrections incluses dans cette mise à jour."
    } else {
        cleaned.joinToString(prefix = "• ", separator = "\n• ")
    }
}

private fun translateReleaseChange(value: String): String {
    val text = value.trim()
    val match = Regex("Implement screen\\s+(\\d+)\\s*(?::|and)\\s*(.+)$", RegexOption.IGNORE_CASE).find(text)

    if (match != null) {
        return when (match.groupValues[1]) {
            "14" -> "Écran 14 : création de groupe"
            "15" -> "Écran 15 : discussion de groupe"
            "16" -> "Écran 16 : profil utilisateur et navigation"
            else -> "Écran " + match.groupValues[1] + " : " + match.groupValues[2]
        }
    }

    return when {
        text.startsWith("Security:", ignoreCase = true) ->
            "Sécurité : " + text.substringAfter(":").trim()
        text.startsWith("Fix ", ignoreCase = true) ->
            "Correction : " + text.removePrefix("Fix ").trim()
        else -> text
    }
}
