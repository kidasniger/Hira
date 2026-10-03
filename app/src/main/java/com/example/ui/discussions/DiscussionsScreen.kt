package com.example.ui.discussions

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HiraLogo
import com.example.ui.theme.HiraBorder
import com.example.ui.theme.HiraGrayDark
import com.example.ui.theme.HiraGrayMedium
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraWhite
import com.hira.kidas.R

/**
 * ÉCRAN 9 — LISTE DES DISCUSSIONS (ACCUEIL)
 *
 * Reproduction du UI Kit V1 :
 * - En-tête Hira + recherche
 * - Liste compacte de conversations (72 dp par ligne)
 * - Avatar de 48 dp, nom, dernier message et heure
 * - Compteur de messages non lus
 * - Barre de navigation basse : Discussions / Groupes / Contacts / Réglages
 */
@Composable
fun DiscussionsScreen(
    onSearchClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val conversations = listOf(
        DiscussionPreview(R.string.discussions_sample_fatou_name, R.string.discussions_sample_fatou_message, R.string.discussions_sample_fatou_time, "FS", Color(0xFFE6EBFF), HiraRoyalBlue, 2),
        DiscussionPreview(R.string.discussions_sample_ibrahim_name, R.string.discussions_sample_ibrahim_message, R.string.discussions_sample_ibrahim_time, "ID", Color(0xFFF3F4F6), HiraGrayDark, 0),
        DiscussionPreview(R.string.discussions_sample_family_name, R.string.discussions_sample_family_message, R.string.discussions_sample_family_time, "FD", HiraRoyalBlue, HiraWhite, 5),
        DiscussionPreview(R.string.discussions_sample_maman_name, R.string.discussions_sample_maman_message, R.string.discussions_sample_maman_time, "M", Color(0xFFF3F4F6), HiraGrayDark, 0),
        DiscussionPreview(R.string.discussions_sample_aicha_name, R.string.discussions_sample_aicha_message, R.string.discussions_sample_aicha_time, "A", Color(0xFFE6EBFF), HiraRoyalBlue, 1),
        DiscussionPreview(R.string.discussions_sample_boutique_name, R.string.discussions_sample_boutique_message, R.string.discussions_sample_boutique_time, "B", Color(0xFFF3F4F6), HiraGrayDark, 0)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HiraWhite)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("discussions_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp)
                    .testTag("discussions_header"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HiraLogo(
                        size = 28.dp,
                        tint = HiraRoyalBlue,
                        isAnimated = false,
                        modifier = Modifier.testTag("discussions_logo")
                    )
                    Text(
                        text = stringResource(R.string.discussions_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111111),
                        fontFamily = FontFamily.SansSerif
                    )
                }
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("discussions_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = stringResource(R.string.discussions_search_description),
                        tint = Color(0xFF111111),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(HiraBorder)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("discussions_list")
            ) {
                items(conversations, key = { it.nameRes }) { conversation ->
                    DiscussionRow(conversation)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(HiraWhite)
                    .testTag("discussions_bottom_navigation"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavigationLabel(stringResource(R.string.discussions_nav_discussions), true)
                BottomNavigationLabel(stringResource(R.string.discussions_nav_groups), false)
                BottomNavigationLabel(stringResource(R.string.discussions_nav_contacts), false)
                BottomNavigationLabel(stringResource(R.string.discussions_nav_settings), false)
            }
        }
    }
}

@Composable
private fun DiscussionRow(
    discussion: DiscussionPreview,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 16.dp)
                .testTag("discussion_row_${discussion.initials}"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(discussion.avatarBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = discussion.initials,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = discussion.avatarContent,
                    fontFamily = FontFamily.SansSerif
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stringResource(discussion.nameRes),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111111),
                    fontFamily = FontFamily.SansSerif,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(discussion.messageRes),
                    fontSize = 13.sp,
                    color = HiraGrayDark,
                    fontFamily = FontFamily.SansSerif,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(discussion.timeRes),
                    fontSize = 12.sp,
                    color = HiraGrayMedium,
                    fontFamily = FontFamily.SansSerif
                )
                if (discussion.unread > 0) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(HiraRoyalBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = discussion.unread.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = HiraWhite,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(start = 76.dp)
                .background(Color(0xFFF5F5F5))
        )
    }
}

@Composable
private fun BottomNavigationLabel(
    text: String,
    active: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .weight(1f)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
            color = if (active) HiraRoyalBlue else Color(0xFF9CA3AF),
            fontFamily = FontFamily.SansSerif
        )
    }
}

@Immutable
private data class DiscussionPreview(
    val nameRes: Int,
    val messageRes: Int,
    val timeRes: Int,
    val initials: String,
    val avatarBackground: Color,
    val avatarContent: Color,
    val unread: Int
)
