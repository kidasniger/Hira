package com.example.ui.onboarding

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.R
import com.example.ui.components.HiraLogo
import com.example.ui.theme.HiraBorder
import com.example.ui.theme.HiraGrayDark
import com.example.ui.theme.HiraGrayMedium
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraRoyalBlueLight
import com.example.ui.theme.HiraTheme
import com.example.ui.theme.HiraWhite

/**
 * ÉCRAN 2 — ACCUEIL 1/3 : "Une messagerie légère"
 *
 * Éléments conformes à l'identité visuelle HIRA et à la maquette :
 * - Fond blanc épuré (#FFFFFF)
 * - Bouton "Passer" discret en haut à droite
 * - Illustration centrale valorisant la légèreté avec le logo officiel HIRA
 * - Titre fort "Une messagerie légère" (26-28sp, ExtraBold)
 * - Sous-titre explicatif court et lisible (15sp, gris neutre #555555)
 * - Indicateur de pagination 1/3 (pilule bleu royal active + 2 points inactifs)
 * - Bouton d'action principal "Suivant" en bleu royal plat (#0043FF)
 * - Comportement de retour Android sécurisé (évite le retour en boucle vers le Splash)
 */
@Composable
fun OnboardingScreen1(
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Gestion propre du bouton retour système : empêche tout retour involontaire vers le Splash
    BackHandler {
        onBack()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HiraWhite)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("onboarding_screen_1")
    ) {
        // En-tête : bouton "Passer" en haut à droite
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onSkipClick,
                modifier = Modifier
                    .height(48.dp)
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
            // Illustration centrale : cercle épuré bleu clair avec l'oiseau HIRA bleu royal
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(HiraRoyalBlueLight)
                    .testTag("onboarding_1_illustration"),
                contentAlignment = Alignment.Center
            ) {
                HiraLogo(
                    size = 110.dp,
                    tint = HiraRoyalBlue,
                    isAnimated = false,
                    modifier = Modifier.testTag("onboarding_1_logo")
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Titre de l'écran 1
            Text(
                text = stringResource(id = R.string.onboarding_1_title),
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF111111),
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_1_title")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sous-titre descriptif
            Text(
                text = stringResource(id = R.string.onboarding_1_tagline),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = HiraGrayDark,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .testTag("onboarding_1_description")
            )
        }

        // Zone inférieure : indicateur 1/3 et bouton Suivant
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Indicateur de pagination 1/3
            Row(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .semantics { contentDescription = "Écran 1 sur 3" }
                    .testTag("onboarding_page_indicator"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Écran 1 (actif) : pilule bleu royal allongée
                Box(
                    modifier = Modifier
                        .width(26.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(HiraRoyalBlue)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Écran 2 (inactif)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(HiraBorder)
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
fun OnboardingScreen1Preview() {
    HiraTheme {
        OnboardingScreen1(
            onNextClick = {},
            onSkipClick = {},
            onBack = {}
        )
    }
}
