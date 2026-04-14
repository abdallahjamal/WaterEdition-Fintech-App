package com.abood.wateredition.presentation.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abood.wateredition.R
import com.abood.wateredition.ui.theme.CyanPrimary
import com.abood.wateredition.ui.theme.NavyBackground
import com.abood.wateredition.ui.theme.NavySurface
import com.abood.wateredition.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * Optimized SplashScreen for Seamless Handover from Core Splashscreen.
 * Starts with Alpha 1 to prevent flickering and animates the branding elements.
 */
@Composable
fun SplashScreen(
    onNavigateToList: () -> Unit
) {
    // Initial values set to 1.0 to match the static system splash icon
    val scale = remember { Animatable(1.0f) }
    val alpha = remember { Animatable(1.0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        // Step 1: Subtle Scale-up animation for the logo
        scale.animateTo(
            targetValue = 1.08f,
            animationSpec = tween(
                durationMillis = 1200,
                easing = EaseOutBack
            )
        )
        
        // Step 2: Fade-in the text branding shortly after
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        
        // Hold for the premium feel
        delay(1000)
        
        // Exit to main screen
        onNavigateToList()
    }

    // Professional Gradient Background matching the System Splash color
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        NavySurface,
                        NavyBackground,
                        NavyBackground
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo - Scaled relative to the system splash icon
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = "Water Edition Logo",
                modifier = Modifier
                    .size(160.dp) // Standard splash icon size
                    .scale(scale.value)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Branding Content - Fades in separately
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(textAlpha.value)
            ) {
                Text(
                    text = "WATER EDITION",
                    style = MaterialTheme.typography.displaySmall,
                    color = CyanPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "PREMIUM BILLING SYSTEM",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 4.sp
                )
            }
        }
    }
}
