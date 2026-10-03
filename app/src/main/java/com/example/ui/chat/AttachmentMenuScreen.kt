package com.example.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

enum class AttachmentAction {
    PHOTO,
    VIDEO,
    DOCUMENT,
    VOICE
}

/**
 * ÉCRAN 12 — MENU DES PIÈCES JOINTES
 *
 * Le menu est présenté comme une feuille inférieure au-dessus de la
 * conversation. Aucun fichier ou contenu de démonstration n'est ajouté.
 */
@Composable
fun AttachmentMenuScreen(
    contactName: String?,
    contactInitials: String? = null,
    contactStatus: String? = null,
    onBack: () -> Unit = {},
    onAttachmentAction: (AttachmentAction) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val displayName = contactName?.trim().takeUnless { it.isNullOrBlank() }
        ?: stringResource(R.string.chat_private_default_contact)
    val initials = contactInitials?.trim().takeUnless { it.isNullOrBlank() }
        ?: initialsFor(displayName)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("attachment_menu_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(HiraWhite)
                    .border(1.dp, Color(0xFFEEEEEE))
                    .padding(horizontal = 12.dp)
                    .testTag("attachment_menu_header"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("attachment_menu_back_button")
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
                    contactStatus?.trim()?.takeIf { it.isNotBlank() }?.let { status ->
                        Text(
                            text = status,
                            fontSize = 12.sp,
                            color = HiraGrayMedium,
                            fontFamily = FontFamily.SansSerif,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("attachment_menu_conversation_area")
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x52000000))
                .testTag("attachment_menu_scrim")
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(HiraWhite)
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
                .align(Alignment.BottomCenter)
                .testTag("attachment_menu_sheet")
        ) {
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFE5E7EB))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AttachmentOption(
                    label = stringResource(R.string.attachment_photo),
                    contentDescription = stringResource(R.string.attachment_photo),
                    icon = Icons.Outlined.Image,
                    action = AttachmentAction.PHOTO,
                    onClick = onAttachmentAction,
                    modifier = Modifier.weight(1f)
                )
                AttachmentOption(
                    label = stringResource(R.string.attachment_video),
                    contentDescription = stringResource(R.string.attachment_video),
                    icon = Icons.Outlined.VideoLibrary,
                    action = AttachmentAction.VIDEO,
                    onClick = onAttachmentAction,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AttachmentOption(
                    label = stringResource(R.string.attachment_document),
                    contentDescription = stringResource(R.string.attachment_document),
                    icon = Icons.Outlined.Description,
                    action = AttachmentAction.DOCUMENT,
                    onClick = onAttachmentAction,
                    modifier = Modifier.weight(1f)
                )
                AttachmentOption(
                    label = stringResource(R.string.attachment_voice),
                    contentDescription = stringResource(R.string.attachment_voice),
                    icon = Icons.Outlined.Mic,
                    action = AttachmentAction.VOICE,
                    onClick = onAttachmentAction,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AttachmentOption(
    label: String,
    contentDescription: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    action: AttachmentAction,
    onClick: (AttachmentAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick(action) }
            .padding(12.dp)
            .testTag("attachment_option_\${action.name.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3F4F6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = HiraRoyalBlue,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = HiraBlack,
            fontFamily = FontFamily.SansSerif,
            maxLines = 1
        )
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
