package com.example.ui.chat

import android.text.format.Formatter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HiraBlack
import com.example.ui.theme.HiraGrayMedium
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraRoyalBlueLight
import com.example.ui.theme.HiraWhite
import com.hira.kidas.R
import kotlin.math.roundToInt

sealed interface LargeFileDownloadState {
    data object Ready : LargeFileDownloadState
    data class Downloading(val progress: Float) : LargeFileDownloadState
    data object Completed : LargeFileDownloadState
    data class Error(val message: String) : LargeFileDownloadState
}

@Composable
fun LargeFileDownloadScreen(
    contactName: String?,
    contactInitials: String? = null,
    contactStatus: String? = null,
    fileName: String,
    fileSizeBytes: Long?,
    mimeType: String?,
    state: LargeFileDownloadState = LargeFileDownloadState.Ready,
    onBack: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    onCancel: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val displayName = contactName?.trim().takeUnless { it.isNullOrBlank() }
        ?: stringResource(R.string.chat_private_default_contact)
    val initials = contactInitials?.trim().takeUnless { it.isNullOrBlank() }
        ?: initialsFor(displayName)
    val formattedSize = fileSizeBytes?.takeIf { it >= 0L }?.let {
        Formatter.formatFileSize(context, it)
    } ?: stringResource(R.string.large_file_size_unknown)
    val progress = (state as? LargeFileDownloadState.Downloading)
        ?.progress
        ?.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("large_file_download_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(HiraWhite)
                    .border(1.dp, Color(0xFFEEEEEE))
                    .padding(horizontal = 12.dp)
                    .testTag("large_file_download_header"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("large_file_download_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.chat_private_back_description),
                        tint = HiraBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(HiraRoyalBlueLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HiraRoyalBlue,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = displayName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HiraBlack,
                        fontFamily = FontFamily.SansSerif,
                        maxLines = 1
                    )
                    contactStatus?.trim()?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            color = HiraGrayMedium,
                            fontFamily = FontFamily.SansSerif,
                            maxLines = 1
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("large_file_download_content"),
                verticalArrangement = Arrangement.Top
            ) {
                FileTransferCard(
                    fileName = fileName,
                    fileSize = formattedSize,
                    mimeType = mimeType,
                    state = state,
                    progress = progress,
                    onDownloadClick = onDownloadClick,
                    onCancel = onCancel
                )
            }
        }
    }
}

@Composable
private fun FileTransferCard(
    fileName: String,
    fileSize: String,
    mimeType: String?,
    state: LargeFileDownloadState,
    progress: Float?,
    onDownloadClick: () -> Unit,
    onCancel: () -> Unit
) {
    val isVideo = mimeType?.startsWith("video/") == true

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(HiraWhite)
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(14.dp))
            .testTag("large_file_transfer_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(0xFFF3F4F6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isVideo) Icons.Outlined.VideoFile else Icons.Outlined.Description,
                contentDescription = null,
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(44.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fileName,
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HiraBlack,
                    fontFamily = FontFamily.SansSerif,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = fileSize,
                    fontSize = 11.sp,
                    color = Color(0xFF888888),
                    fontFamily = FontFamily.SansSerif
                )
            }

            when (state) {
                LargeFileDownloadState.Ready -> {
                    Button(
                        onClick = onDownloadClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("large_file_download_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HiraRoyalBlue,
                            contentColor = HiraWhite
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.large_file_download),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                is LargeFileDownloadState.Downloading -> {
                    val percent = ((progress ?: 0f) * 100f).roundToInt().coerceIn(0, 100)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFE5E7EB))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(percent / 100f)
                                    .height(4.dp)
                                    .background(HiraRoyalBlue)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.large_file_downloading, percent),
                                modifier = Modifier.weight(1f),
                                fontSize = 11.sp,
                                color = Color(0xFF888888)
                            )
                            Button(
                                onClick = onCancel,
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("large_file_cancel_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF3F4F6),
                                    contentColor = HiraBlack
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.large_file_cancel),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                LargeFileDownloadState.Completed -> {
                    Text(
                        text = stringResource(R.string.large_file_downloaded),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HiraRoyalBlue
                    )
                }

                is LargeFileDownloadState.Error -> {
                    Text(
                        text = state.message,
                        fontSize = 12.sp,
                        color = Color(0xFFB42318)
                    )
                }
            }
        }
    }
}

private fun initialsFor(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.size >= 2 -> (parts.first().first().toString() + parts.last().first()).uppercase()
        parts.size == 1 -> parts.first().take(2).uppercase()
        else -> "?"
    }
}
