package com.example.mahilashaktiunnativ2.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.mahilashaktiunnativ2.R

@Composable
fun SplashScreen(

    onNavigate: () -> Unit

) {

    var startAnimation by remember {

        mutableStateOf(false)
    }

    val scaleAnimation by animateFloatAsState(

        targetValue =
            if (startAnimation) 1f else 0.85f,

        animationSpec =
            tween(1200),

        label = ""
    )

    val alphaAnimation by animateFloatAsState(

        targetValue =
            if (startAnimation) 1f else 0f,

        animationSpec =
            tween(1200),

        label = ""
    )

    LaunchedEffect(true) {

        startAnimation = true

        delay(3800)

        onNavigate()
    }

    Box(

        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFFFDFDFD)
            ),

        contentAlignment =
            Alignment.Center
    ) {

        Column(

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Image(

                painter = painterResource(
                    id = R.drawable.welcome_image
                ),

                contentDescription = null,

                modifier = Modifier
                    .size(420.dp)
                    .scale(scaleAnimation)
                    .alpha(alphaAnimation)
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Text(

                text =
                    "Empowering Women Through Unity",

                fontSize = 17.sp,

                fontWeight =
                    FontWeight.Medium,

                color =
                    Color(0xFF2E7D32)
            )
        }
    }
}