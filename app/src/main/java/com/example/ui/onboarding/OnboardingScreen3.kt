package com.example.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HiraLogo
import com.example.ui.theme.HiraBorder
import com.example.ui.theme.HiraGrayDark
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraRoyalBlueLight
import com.example.ui.theme.HiraTheme
import com.example.ui.theme.HiraWhite
import com.hira.kidas.R

/**
 * ÉCRAN 4 — ACCUEIL 3/3 : "Pensée pour les petites connexions"
 *
 * Troisième et dernière étape du parcours d'accueil HIRA :
 * - Fond blanc épuré (#FFFFFF)
 * - Bouton retour vers Accueil 2/3
 * - Illustration sobre orientée réseau / connectivité résiliente
 *   (disque bleu clair #E6EBFF avec logo HIRA et ondes radio vectorielles plates)
 * - Titre "Pensée pour les petites connexions" (26sp, ExtraBold)
 * - Sous-titre explicatif optimisé (15sp, gris #555555)
 * - Indicateur de progression 3/3 (étapes 1 et 2 déjà franchies, étape 3 active)
 * - Bouton d'action principal "Commencer" (#0043FF) finalisant l'onboarding vers Connexion
 * - Prise en charge du retour système via BackHandler
 */
@Composable
fun OnboardingScreen3(
    onFinishClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Retour Android vers Accueil 2/3
    BackHandler {
        onBackClick()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HiraWhite)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("onboarding_screen_3")
    ) {
        // Barre supérieure avec bouton Retour vers Accueil 2/3
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("onboarding_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.onboarding_previous),
                    tint = HiraGrayDark
                )
            }
        }

        // Contenu principal centré verticalement
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Illustration centrale : connectivité résiliente avec l'oiseau HIRA
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(HiraRoyalBlueLight)
                    .testTag("onboarding_3_illustration"),
                contentAlignment = Alignment.Center
            ) {
                // Anneaux et ondes de connectivité vectorielles discrètes (sans dégradé lourd)
                Canvas(modifier = Modifier.size(160.dp)) {
                    val strokeWidth = 2.dp.toPx()
                    val radiusOuter = (size.minDimension - strokeWidth) / 2f
                    drawCircle(
                        color = HiraRoyalBlue.copy(alpha = 0.20f),
                        radius = radiusOuter,
                        style = Stroke(width = strokeWidth)
                    )

                    // Petit badge signal / connectivité en bas à droite
                    val badgeCenter = center.copy(y = center.y + 48.dp.toPx())
                    val badgeRadius = 14.dp.toPx()
                    drawCircle(
                        color = HiraRoyalBlue,
                        radius = badgeRadius,
                        center = badgeCenter
                    )

                    // 3 barres de signal plates blanches à l'intérieur du badge
                    val barWidth = 2.dp.toPx()
                    val gap = 2.dp.toPx()
                    val startX = badgeCenter.x - (barWidth * 1.5f + gap)
                    val baseY = badgeCenter.y + 4.dp.toPx()

                    // Barre 1 (petite)
                    drawRect(
                        color = HiraWhite,
                        topLeft = androidx.compose.ui.geometry.Offset(startX, baseY - 4.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(barWidth, 4.dp.toPx())
                    )
                    // Barre 2 (moyenne)
                    drawRect(
                        color = HiraWhite,
                        topLeft = androidx.compose.ui.geometry.Offset(startX + barWidth + gap, baseY - 7.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(barWidth, 7.dp.toPx())
                    )
                    // Barre 3 (haute)
                    drawRect(
                        color = HiraWhite,
                        topLeft = androidx.compose.ui.geometry.Offset(startX + (barWidth + gap) * 2f, baseY - 10.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(barWidth, 10.dp.toPx())
                    )
                }

                // Logo officiel HIRA
                HiraLogo(
                    size = 96.dp,
                    tint = HiraRoyalBlue,
                    isAnimated = false,
                    modifier = Modifier.testTag("onboarding_3_logo")
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Titre exact
            Text(
                text = stringResource(id = R.string.onboarding_3_title),
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF111111),
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_3_title")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sous-titre descriptif
            Text(
                text = stringResource(id = R.string.onboarding_3_tagline),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = HiraGrayDark,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .testTag("onboarding_3_description")
            )
        }

        // Zone inférieure : indicateur 3/3 et bouton Commencer
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Indicateur de pagination 3/3
            // Les étapes 1 et 2 apparaissent comme déjà franchies, l'étape 3 est active
            Row(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .semantics { contentDescription = "Écran 3 sur 3" }
                    .testTag("onboarding_page_indicator"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Écran 1 (parcouru)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(HiraBorder)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Écran 2 (parcouru)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(HiraBorder)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Écran 3 (actif) : pilule allongée en bleu royal
                Box(
                    modifier = Modifier
                        .width(26.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(HiraRoyalBlue)
                )
            }

            // Bouton principal d'accès à la connexion
            Button(
                onClick = onFinishClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("onboarding_start_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HiraRoyalBlue,
                    contentColor = HiraWhite
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_start),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun OnboardingScreen3Preview() {
    HiraTheme {
        OnboardingScreen3(
            onFinishClick = {},
            onBackClick = {}
        )
    }
}
