package com.example.ui.profile

import coil.compose.AsyncImage
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HiraBorder
import com.example.ui.theme.HiraGrayDark
import com.example.ui.theme.HiraGrayLight
import com.example.ui.theme.HiraGrayMedium
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraRoyalBlueLight
import com.example.ui.theme.HiraWhite
import com.hira.kidas.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    displayName: String?,
    username: String? = null,
    email: String? = null,
    phoneNumber: String? = null,
    photoUrl: String? = null,
    memberSinceMillis: Long? = null,
    onBack: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cleanName = displayName?.trim().takeUnless { it.isNullOrBlank() }
    val cleanUsername = username?.trim().takeUnless { it.isNullOrBlank() }
    val cleanEmail = email?.trim().takeUnless { it.isNullOrBlank() }
    val cleanPhone = phoneNumber?.trim().takeUnless { it.isNullOrBlank() }
    val cleanPhoto = photoUrl?.trim().takeUnless { it.isNullOrBlank() }
    val memberSince = memberSinceMillis?.takeIf { it > 0L }?.let(::formatMemberSince)

    val visibleName = cleanName ?: stringResource(R.string.profile_unknown_name)
    val initials = initialsFor(visibleName)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HiraWhite)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("profile_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.dp, HiraBorder)
                    .padding(horizontal = 16.dp)
                    .testTag("profile_header"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("profile_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.profile_back_description),
                        tint = Color(0xFF111111),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.profile_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111111),
                    fontFamily = FontFamily.SansSerif
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .widthIn(max = 320.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(HiraRoyalBlueLight)
                        .testTag("profile_avatar"),
                    contentAlignment = Alignment.Center
                ) {
                    if (cleanPhoto != null) {
                        AsyncImage(
                            model = cleanPhoto,
                            contentDescription = stringResource(R.string.profile_avatar_description),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = initials,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = HiraRoyalBlue,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = visibleName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111111),
                    fontFamily = FontFamily.SansSerif,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("profile_display_name")
                )

                cleanUsername?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (it.startsWith("@")) it else "@$it",
                        fontSize = 14.sp,
                        color = HiraGrayDark,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.testTag("profile_username")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = onEditProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("profile_edit_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HiraBorder)
                ) {
                    Text(
                        text = stringResource(R.string.profile_edit),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111111),
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                ProfileInfoRow(
                    icon = Icons.Outlined.Email,
                    text = cleanEmail
                )

                ProfileInfoRow(
                    icon = Icons.Outlined.Phone,
                    text = cleanPhone
                )

                ProfileInfoRow(
                    icon = Icons.Outlined.Person,
                    text = memberSince
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String?
) {
    if (text == null) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFF3F4F6),
                shape = RoundedCornerShape(0.dp)
            )
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HiraGrayMedium,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF111111),
            fontFamily = FontFamily.SansSerif,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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

private fun formatMemberSince(timestamp: Long): String {
    return SimpleDateFormat("MMMM yyyy", Locale.FRENCH).format(Date(timestamp)).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.FRENCH) else it.toString()
    }.let { monthYear ->
        "Membre depuis $monthYear"
    }
}
