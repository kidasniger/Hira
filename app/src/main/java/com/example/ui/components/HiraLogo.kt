package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Composant officiel du Logo HIRA.
 * Dessiné vectoriellement avec précision :
 *  1. Aile supérieure (courbée vers le haut-gauche)
 *  2. Corps et tête avec bec vers la droite et queue vers la gauche
 *  3. Aile inférieure / plume caudale (courbée vers le bas-gauche)
 *
 * L'animation reproduit le vol plané et le battement subtil de l'oiseau HIRA.
 * Conception ultra-légère sans surconsommation mémoire, fluide sur anciens et récents appareils.
 */
@Composable
fun HiraLogo(
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    tint: Color = Color.White,
    isAnimated: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hira_bird_animation")

    // Battement subtil de l'aile supérieure
    val topWingAngle by if (isAnimated) {
        infiniteTransition.animateFloat(
            initialValue = -3.5f,
            targetValue = 3.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "top_wing_angle"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    // Battement en léger déphasage pour l'aile inférieure
    val bottomWingAngle by if (isAnimated) {
        infiniteTransition.animateFloat(
            initialValue = 2.5f,
            targetValue = -2.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1400, delayMillis = 180, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bottom_wing_angle"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    // Ondulation légère du corps (vol plané)
    val bodyTranslation by if (isAnimated) {
        infiniteTransition.animateFloat(
            initialValue = -2.0f,
            targetValue = 2.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "body_translation"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    Box(
        modifier = modifier
            .size(size)
            .testTag("hira_logo"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val scale = this.size.minDimension / 100f

            // 1. Aile supérieure (pivot d'attache à la base de l'aile : 58.5, 49.0)
            rotate(
                degrees = topWingAngle,
                pivot = Offset(58.5f * scale, 49.0f * scale)
            ) {
                val topWingPath = Path().apply {
                    moveTo(28.5f * scale, 24.5f * scale)
                    cubicTo(
                        34.0f * scale, 32.0f * scale,
                        47.0f * scale, 43.5f * scale,
                        58.5f * scale, 49.0f * scale
                    )
                    cubicTo(
                        47.0f * scale, 46.5f * scale,
                        35.0f * scale, 38.0f * scale,
                        28.5f * scale, 24.5f * scale
                    )
                    close()
                }
                drawPath(path = topWingPath, color = tint)
            }

            // 2. Corps de l'oiseau (flottement vertical doux)
            translate(top = bodyTranslation * scale) {
                val bodyPath = Path().apply {
                    moveTo(31.0f * scale, 56.0f * scale)
                    // Ventre et poitrine vers le bec
                    cubicTo(
                        38.0f * scale, 61.5f * scale,
                        48.0f * scale, 62.5f * scale,
                        59.0f * scale, 54.0f * scale
                    )
                    cubicTo(
                        64.5f * scale, 49.5f * scale,
                        68.0f * scale, 44.0f * scale,
                        79.5f * scale, 43.5f * scale
                    )
                    // Sommet de la tête, nuque et dos vers la queue
                    cubicTo(
                        73.0f * scale, 40.5f * scale,
                        68.0f * scale, 39.5f * scale,
                        64.0f * scale, 42.0f * scale
                    )
                    cubicTo(
                        59.5f * scale, 45.5f * scale,
                        55.0f * scale, 49.0f * scale,
                        48.0f * scale, 52.0f * scale
                    )
                    cubicTo(
                        42.0f * scale, 54.5f * scale,
                        36.0f * scale, 55.5f * scale,
                        31.0f * scale, 56.0f * scale
                    )
                    close()
                }
                drawPath(path = bodyPath, color = tint)
            }

            // 3. Aile inférieure / plume caudale (pivot : 55.0, 65.0)
            rotate(
                degrees = bottomWingAngle,
                pivot = Offset(55.0f * scale, 65.0f * scale)
            ) {
                val bottomWingPath = Path().apply {
                    moveTo(29.5f * scale, 75.5f * scale)
                    cubicTo(
                        36.0f * scale, 68.5f * scale,
                        46.0f * scale, 65.0f * scale,
                        55.0f * scale, 65.0f * scale
                    )
                    cubicTo(
                        46.0f * scale, 71.0f * scale,
                        37.0f * scale, 75.0f * scale,
                        29.5f * scale, 75.5f * scale
                    )
                    close()
                }
                drawPath(path = bottomWingPath, color = tint)
            }
        }
    }
}
