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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import com.hira.kidas.R
import com.example.ui.components.HiraLogo
import com.example.ui.theme.HiraBorder
import com.example.ui.theme.HiraGrayDark
import com.example.ui.theme.HiraGrayMedium
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraRoyalBlueLight
import com.example.ui.theme.HiraTheme
import com.example.ui.theme.HiraWhite

/**
 * ÉCRAN 3 — ACCUEIL 2/3 : "Vos données restent chez vous"
 *
 * Éléments conformes à l'identité visuelle HIRA et à la maquette :
 * - Fond blanc épuré (#FFFFFF)
 * - Navigation : bouton retour vers Accueil 1/3 et bouton "Passer" en haut à droite
 * - Illustration centrale valorisant la souveraineté et la protection des données
 *   (disque bleu clair #E6EBFF avec l'oiseau HIRA bleu royal et anneau de protection vectoriel minimal)
 * - Titre "Vos données restent chez vous" (26sp, ExtraBold)
 * - Sous-titre court et lisible (15sp, gris neutre #555555)
 * - Indicateur de pagination 2/3 (point inactif + pilule bleu royal active + point inactif)
 * - Bouton d'action principal "Suivant" en bleu royal plat (#0043FF)
 * - Navigation système retour intégrée vers Accueil 1/3 (BackHandler)
 */
@Composable
fun OnboardingScreen2(
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Gestion du retour système Android : retour direct vers Accueil 1/3
    BackHandler {
        onBackClick()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HiraWhite)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("onboarding_screen_2")
    ) {
        // Barre supérieure : retour à gauche + "Passer" à droite
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
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

            TextButton(
                onClick = onSkipClick,
                modifier = Modifier
                    .height(48.dp)
                    .padding(end = 8.dp)
                    .testTag("onboarding_skip_button")
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_skip),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HiraGrayMedium,
                    fontFamily = FontFamily.SansSerif
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
            // Illustration centrale : disque bleu clair avec logo HIRA et contour de protection souveraine
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(HiraRoyalBlueLight)
                    .testTag("onboarding_2_illustration"),
                contentAlignment = Alignment.Center
            ) {
                // Anneau de protection minimaliste vectoriel (aucun dégradé, aucun effet lourd)
                Canvas(modifier = Modifier.size(160.dp)) {
                    val strokeWidth = 2.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2f
                    drawCircle(
                        color = HiraRoyalBlue.copy(alpha = 0.25f),
                        radius = radius,
                        style = Stroke(width = strokeWidth)
                    )

                    // Petit verrou minimaliste plat en bas à droite
                    val lockCenter = center.copy(y = center.y + 48.dp.toPx())
                    val badgeRadius = 14.dp.toPx()
                    drawCircle(
                        color = HiraRoyalBlue,
                        radius = badgeRadius,
                        center = lockCenter
                    )

                    // Corps du cadenas (rectangle blanc plat)
                    val lockBodyWidth = 10.dp.toPx()
                    val lockBodyHeight = 8.dp.toPx()
                    val lockBodyLeft = lockCenter.x - lockBodyWidth / 2f
                    val lockBodyTop = lockCenter.y - lockBodyHeight / 2f + 2.dp.toPx()
                    drawRect(
                        color = HiraWhite,
                        topLeft = androidx.compose.ui.geometry.Offset(lockBodyLeft, lockBodyTop),
                        size = androidx.compose.ui.geometry.Size(lockBodyWidth, lockBodyHeight)
                    )

                    // Anse du cadenas (arc blanc plat)
                    val arcPath = Path().apply {
                        val arcLeft = lockCenter.x - 3.5f.dp.toPx()
                        val arcTop = lockBodyTop - 5.dp.toPx()
                        moveTo(arcLeft, lockBodyTop)
                        cubicTo(
                            arcLeft, arcTop,
                            arcLeft + 7.dp.toPx(), arcTop,
                            arcLeft + 7.dp.toPx(), lockBodyTop
                        )
                    }
                    drawPath(
                        path = arcPath,
                        color = HiraWhite,
                        style = Stroke(width = 1.6f.dp.toPx())
                    )
                }

                // Logo officiel HIRA
                HiraLogo(
                    size = 96.dp,
                    tint = HiraRoyalBlue,
                    isAnimated = false,
                    modifier = Modifier.testTag("onboarding_2_logo")
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Titre de l'écran 2
            Text(
                text = stringResource(id = R.string.onboarding_2_title),
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF111111),
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_2_title")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sous-titre descriptif
            Text(
                text = stringResource(id = R.string.onboarding_2_tagline),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = HiraGrayDark,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .testTag("onboarding_2_description")
            )
        }

        // Zone inférieure : indicateur 2/3 et bouton Suivant
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Indicateur de pagination 2/3
            Row(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .semantics { contentDescription = "Écran 2 sur 3" }
                    .testTag("onboarding_page_indicator"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Écran 1 (inactif)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(HiraBorder)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Écran 2 (actif) : pilule bleu royal allongée
                Box(
                    modifier = Modifier
                        .width(26.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(HiraRoyalBlue)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Écran 3 (inactif)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(HiraBorder)
                )
            }

            // Bouton principal d'avancement
            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("onboarding_next_button"),
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
                    text = stringResource(id = R.string.onboarding_next),
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
fun OnboardingScreen2Preview() {
    HiraTheme {
        OnboardingScreen2(
            onNextClick = {},
            onSkipClick = {},
            onBackClick = {}
        )
    }
}
