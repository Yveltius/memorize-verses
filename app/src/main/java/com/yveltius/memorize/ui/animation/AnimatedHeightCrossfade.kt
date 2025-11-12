package com.yveltius.memorize.ui.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedHeightCrossfade(
    targetState: Any?,
    modifier: Modifier = Modifier,
    animationSpec: FiniteAnimationSpec<IntSize> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    ),
    content: @Composable (Any?) -> Unit
) {
    val density = LocalDensity.current

    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = {
           // Crossfade
            fadeIn(tween(220, delayMillis = 90))
                .togetherWith(fadeOut(tween(90)))
            // Height animation
            expandVertically(animationSpec, expandFrom = Alignment.Top)
                .togetherWith(shrinkVertically(animationSpec, shrinkTowards = Alignment.Top))
        },
        contentAlignment = Alignment.TopStart
    ) { state ->
        Box(
            modifier = Modifier
                .animateContentSize(animationSpec) // Smooth height changes
        ) {
            content(state)
        }
    }
}