package com.example.ui.chat

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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HiraBlack
import com.example.ui.theme.HiraBorder
import com.example.ui.theme.HiraGrayMedium
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraRoyalBlueLight
import com.example.ui.theme.HiraWhite
import com.hira.kidas.R

/**
 * ÉCRAN 11 — CONVERSATION PRIVÉE
 *
 * Coquille visuelle uniquement : aucun message de démonstration n'est injecté.
 * Les messages affichés proviennent exclusivement de la liste fournie par
 * l'appelant et pourront être alimentés par le stockage local / temps réel.
 */
data class PrivateChatMessage(
    val id: String,
    val text: String,
    val isOutgoing: Boolean
)

@Composable
fun PrivateChatScreen(
    contactName: String?,
    contactInitials: String? = null,
    contactStatus: String? = null,
    messages: List<PrivateChatMessage> = emptyList(),
    onBack: () -> Unit = {},
    onAttachmentClick: () -> Unit = {},
    onSendMessage: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }

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
            .testTag("private_chat_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(HiraWhite)
                    .border(width = 1.dp, color = Color(0xFFEEEEEE), shape = RoundedCornerShape(0.dp))
                    .padding(horizontal = 12.dp)
                    .testTag("private_chat_header"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("private_chat_back_button")
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

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("private_chat_messages"),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                reverseLayout = false
            ) {
                items(
                    items = messages,
                    key = { it.id }
                ) { message ->
                    MessageBubble(message = message)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(HiraWhite)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFEEEEEE),
                        shape = RoundedCornerShape(0.dp)
                    )
                    .padding(horizontal = 12.dp)
                    .testTag("private_chat_input_bar"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = onAttachmentClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("private_chat_attachment_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AttachFile,
                        contentDescription = stringResource(R.string.chat_private_attachment_description),
                        tint = HiraGrayMedium,
                        modifier = Modifier.size(22.dp)
                    )
                }

                BasicTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(horizontal = 16.dp, vertical = 0.dp)
                        .testTag("private_chat_message_field"),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = HiraBlack,
                        fontFamily = FontFamily.SansSerif
                    ),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (messageText.isBlank()) {
                                Text(
                                    text = stringResource(R.string.chat_private_message_hint),
                                    fontSize = 14.sp,
                                    color = Color(0xFF9CA3AF),
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                val canSend = messageText.trim().isNotEmpty()

                IconButton(
                    onClick = {
                        val text = messageText.trim()
                        if (text.isNotEmpty()) {
                            onSendMessage(text)
                            messageText = ""
                        }
                    },
                    enabled = canSend,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (canSend) HiraRoyalBlue else Color(0xFFE5E7EB)
                        )
                        .testTag("private_chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = stringResource(R.string.chat_private_send_description),
                        tint = if (canSend) HiraWhite else Color(0xFF9CA3AF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: PrivateChatMessage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isOutgoing) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(if (message.isOutgoing) 0.72f else 0.72f)
                .clip(
                    if (message.isOutgoing) {
                        RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp)
                    } else {
                        RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp)
                    }
                )
                .background(if (message.isOutgoing) HiraRoyalBlue else Color(0xFFE9EBEE))
                .padding(horizontal = 14.dp, vertical = 11.dp)
        ) {
            Text(
                text = message.text,
                fontSize = 14.sp,
                color = if (message.isOutgoing) HiraWhite else HiraBlack,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

private fun initialsFor(name: String): String {
    val parts = name.trim().split(Regex("\s+")).filter { it.isNotBlank() }
    return when {
        parts.size >= 2 -> (parts.first().first().toString() + parts.last().first()).uppercase()
        parts.size == 1 -> parts.first().take(2).uppercase()
        else -> "?"
    }
}
