package com.example.ui.groups

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.ui.theme.HiraGrayDark
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraRoyalBlueLight
import com.example.ui.theme.HiraWhite
import com.hira.kidas.R

data class GroupContact(
    val id: String,
    val displayName: String,
    val initials: String
)

@Composable
fun NewGroupScreen(
    contacts: List<GroupContact>,
    onBack: () -> Unit = {},
    onCreateGroup: (groupName: String, selectedContactIds: List<String>) -> Unit = { _, _ -> },
    onGroupPhotoClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var groupName by rememberSaveable { mutableStateOf("") }
    var selectedIds by rememberSaveable { mutableStateOf<Set<String>>(emptySet()) }

    val canCreate = groupName.trim().isNotEmpty() && selectedIds.isNotEmpty()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HiraWhite)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("new_group_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(HiraWhite)
                    .border(1.dp, Color(0xFFEEEEEE))
                    .padding(horizontal = 16.dp)
                    .testTag("new_group_header"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("new_group_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.new_group_back_description),
                            tint = HiraBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Outlined.Group,
                        contentDescription = null,
                        tint = HiraBlack,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.new_group_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HiraBlack,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Text(
                    text = stringResource(R.string.new_group_step),
                    fontSize = 12.sp,
                    color = HiraGrayDark,
                    fontFamily = FontFamily.SansSerif
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("new_group_identity"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5E7EB))
                        .clickable(onClick = onGroupPhotoClick)
                        .testTag("new_group_photo_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = stringResource(R.string.new_group_photo_description),
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(22.dp)
                    )
                }

                BasicTextField(
                    value = groupName,
                    onValueChange = { groupName = it.take(60) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp)
                        .testTag("new_group_name_field"),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        color = HiraBlack,
                        fontFamily = FontFamily.SansSerif
                    ),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (groupName.isBlank()) {
                                Text(
                                    text = stringResource(R.string.new_group_name_hint),
                                    fontSize = 14.sp,
                                    color = Color(0xFF9CA3AF),
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFF3F4F6))
            )

            Text(
                text = stringResource(R.string.new_group_select_members, selectedIds.size),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("new_group_member_title"),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = HiraBlack,
                fontFamily = FontFamily.SansSerif
            )

            if (contacts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.new_group_no_contacts),
                        fontSize = 14.sp,
                        color = HiraGrayDark,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("new_group_contact_list")
                ) {
                    items(
                        items = contacts,
                        key = { it.id }
                    ) { contact ->
                        val selected = selectedIds.contains(contact.id)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clickable {
                                    selectedIds = if (selected) {
                                        selectedIds - contact.id
                                    } else {
                                        selectedIds + contact.id
                                    }
                                }
                                .padding(horizontal = 16.dp)
                                .testTag("new_group_contact_\${contact.id}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(HiraRoyalBlueLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = contact.initials,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = HiraRoyalBlue,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = contact.displayName,
                                modifier = Modifier.weight(1f),
                                fontSize = 15.sp,
                                color = HiraBlack,
                                fontFamily = FontFamily.SansSerif,
                                maxLines = 1
                            )

                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(if (selected) HiraRoyalBlue else HiraWhite)
                                    .border(
                                        width = if (selected) 0.dp else 1.5.dp,
                                        color = Color(0xFFD1D5DB),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = null,
                                        tint = HiraWhite,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HiraWhite)
                    .border(1.dp, Color(0xFFEEEEEE))
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (canCreate) {
                            onCreateGroup(groupName.trim(), selectedIds.toList())
                        }
                    },
                    enabled = canCreate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("new_group_create_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HiraRoyalBlue,
                        contentColor = HiraWhite,
                        disabledContainerColor = Color(0xFFE5E7EB),
                        disabledContentColor = Color(0xFF9CA3AF)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.new_group_create),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }
    }
}
