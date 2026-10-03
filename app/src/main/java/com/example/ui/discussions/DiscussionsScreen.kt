package com.example.ui.discussions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HiraBottomNavigation
import com.example.ui.components.HiraNavDestination
import com.example.ui.components.HiraLogo
import com.example.ui.theme.HiraBorder
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraWhite
import com.hira.kidas.R

/**
 * ÉCRAN 9 — LISTE DES DISCUSSIONS (ACCUEIL)
 *
 * - En-tête Hira + recherche
 * - Zone centrale réservée aux discussions réelles
 * - Barre de navigation basse : Discussions / Groupes / Contacts / Réglages
 *
 * Aucun contenu de démonstration n'est affiché ici.
 * Les discussions réelles seront alimentées par les données de l'application.
 */
@Composable
fun DiscussionsScreen(
    onSearchClick: () -> Unit = {},
    onContactsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
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

            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("discussions_list")
            )

            HiraBottomNavigation(
                selected = HiraNavDestination.DISCUSSIONS,
                onDestinationClick = { destination ->
                    if (destination == HiraNavDestination.CONTACTS) {
                        onContactsClick()
                    }
                },
                modifier = Modifier.testTag("discussions_bottom_navigation")
            )
        }
    }
}
