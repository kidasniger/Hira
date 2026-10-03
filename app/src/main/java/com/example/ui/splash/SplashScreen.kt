package com.example.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.HiraLogo
import com.example.ui.theme.HiraRoyalBlue
import com.example.ui.theme.HiraTheme
import com.example.ui.theme.HiraWhite
import com.example.ui.theme.HiraWhite80

/**
 * Premier écran de l'application : Le Splash Screen HIRA.
 *
 * Éléments conformes à la maquette de référence :
 * - Fond bleu royal HIRA (#0043FF) uni, sans dégradé ni ombre
 * - Logo oiseau HIRA blanc animé et centré (140dp)
 * - Titre "Hira" (36sp, 800)
 * - Slogan "Parlez simplement." (16sp, alpha 80%)
 * - Composition minimaliste, fluide et légère pour tous les terminaux Android
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onSplashFinished: () -> Unit = {}
) {
    // Animation d'apparition fluide et élégante à l'ouverture
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.88f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Apparition du logo oiseau
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
        // Fondu enchaîné doux du texte
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )
        // Transition vers l'écran d'accueil après animation
        kotlinx.coroutines.delay(1200L)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HiraRoyalBlue)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .testTag("splash_content"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo oiseau officiel blanc animé
            HiraLogo(
                size = 140.dp,
                tint = HiraWhite,
                isAnimated = true,
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
                    .testTag("splash_logo")
            )

            // Espacement exact de la maquette (24dp)
            Spacer(modifier = Modifier.height(24.dp))

            // Titre "Hira"
            Text(
                text = stringResource(id = R.string.splash_title),
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HiraWhite,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .testTag("splash_title")
            )

            // Espacement exact de la maquette (8dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Sous-titre "Parlez simplement."
            Text(
                text = stringResource(id = R.string.splash_tagline),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = HiraWhite80,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .testTag("splash_tagline")
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun SplashScreenPreview() {
    HiraTheme {
        SplashScreen()
    }
}
