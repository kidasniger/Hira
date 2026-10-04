package com.example.ui.groups

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberSaveable
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
import com.example.ui.theme.HiraGrayMedium
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraRoyalBlueLight
import com.example.ui.theme.HiraWhite
import com.hira.kidas.R

data class GroupChatMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val isOutgoing: Boolean
)

@Composable
fun GroupChatScreen(
    groupName: String?,
    memberCount: Int,
    messages: List<GroupChatMessage> = emptyList(),
    onBack: () -> Unit = {},
    onAttachmentClick: () -> Unit = {},
    onSendMessage: (String) -> Unit = {},
    onMoreClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val displayGroupName = groupName?.trim().takeUnless { it.isNullOrBlank() }
        ?: stringResource(R.string.group_chat_default_name)
    var messageText by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("group_chat_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(HiraWhite)
                    .border(1.dp, Color(0xFFEEEEEE))
                    .padding(horizontal = 12.dp)
                    .testTag("group_chat_header"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("group_chat_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.group_chat_back_description),
                        tint = HiraBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(HiraRoyalBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Group,
                        contentDescription = null,
                        tint = HiraWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = displayGroupName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HiraBlack,
                        fontFamily = FontFamily.SansSerif,
                        maxLines = 1
                    )
                    Text(
                        text = stringResource(R.string.group_chat_member_count, memberCount),
                        fontSize = 12.sp,
                        color = HiraGrayMedium,
                        fontFamily = FontFamily.SansSerif,
                        maxLines = 1
                    )
                }

                IconButton(
                    onClick = onMoreClick,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("group_chat_more_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = stringResource(R.string.group_chat_more_description),
                        tint = HiraBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("group_chat_message_list"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.group_chat_empty),
                        fontSize = 13.sp,
                        color = HiraGrayMedium,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("group_chat_message_list"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages, key = { it.id }) { message ->
                        GroupMessageBubble(message)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(HiraWhite)
                    .border(1.dp, Color(0xFFEEEEEE))
                    .padding(horizontal = 12.dp)
                    .testTag("group_chat_composer"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = onAttachmentClick,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("group_chat_attachment_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AttachFile,
                        contentDescription = stringResource(R.string.group_chat_attachment_description),
                        tint = HiraGrayMedium,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("group_chat_message_field"),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = HiraBlack,
                            fontFamily = FontFamily.SansSerif
                        ),
                        decorationBox = { innerTextField ->
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
                    )
                }

                val canSend = messageText.isNotBlank()
                IconButton(
                    onClick = {
                        val value = messageText.trim()
                        if (value.isNotEmpty()) {
                            onSendMessage(value)
                            messageText = ""
                        }
                    },
                    enabled = canSend,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (canSend) HiraRoyalBlue else Color(0xFFE5E7EB))
                        .testTag("group_chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = stringResource(R.string.group_chat_send_description),
                        tint = if (canSend) HiraWhite else Color(0xFF9CA3AF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupMessageBubble(message: GroupChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isOutgoing) Alignment.End else Alignment.Start
    ) {
        if (!message.isOutgoing) {
            Text(
                text = message.senderName,
                modifier = Modifier.padding(start = 4.dp),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = HiraRoyalBlue,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (message.isOutgoing) 18.dp else 4.dp,
                        bottomEnd = if (message.isOutgoing) 4.dp else 18.dp
                    )
                )
                .background(if (message.isOutgoing) HiraRoyalBlue else Color(0xFFE9EBEE))
                .padding(horizontal = 14.dp, vertical = 10.dp)
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
