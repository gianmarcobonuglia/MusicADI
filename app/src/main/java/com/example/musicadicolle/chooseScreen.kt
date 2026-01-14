package com.example.musicadicolle

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ChooseScreen(
    navController: NavHostController,
    navigationViewModel: NavigationViewModel,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screen_height = configuration.screenHeightDp.dp
    val color = listOf(Color(0xFF328228), Color(0xFF66A1B4), Color(0xFF1792EB))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(color)),
        contentAlignment = Alignment.Center,
    ) {

        Box(
            Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            PathEffectSquiggles(
                color = Color(0xFF0263AD),
                wavelength = 2000f,
                amplitude = 500f,
                durationMillis = 10000,
                modifier = Modifier.fillMaxHeight()
            )
            PathEffectSquiggles(
                color = Color(0xFF00C1FF),
                wavelength = 1200f,
                amplitude = 300f,
                durationMillis = 7000,
                modifier = Modifier.fillMaxHeight()
            )
            PathEffectSquiggles(
                color = Color(0xFF67C9FD),
                wavelength = 1200f,
                amplitude = 100f,
                durationMillis = 4000,
                modifier = Modifier.fillMaxHeight()
            )
        }

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ){
            // 🔹 Card sfocata sopra lo sfondo
            GlassmorphismCard(
                modifier = Modifier
                    .padding(8.dp)
                    .height(screen_height * 0.4f),
                blurRadius = 32.dp,
                cardOffsetY = 8.dp
            ) {
                CardCulto(navController, navigationViewModel, screenWidth)
            }

            // 🔹 Card Prove
            GlassmorphismCard(
                modifier = Modifier
                    .padding(8.dp)
                    .height(screen_height * 0.4f),
                blurRadius = 32.dp,
                cardOffsetY = 8.dp
            ) {
                CardProve(navController, navigationViewModel, screenWidth)
            }
        }

        Footer(context)
    }
}

@Composable
fun CardCulto(
    navController: NavHostController,
    navigationViewModel: NavigationViewModel,
    screenWidth: Dp,
) {
    Card(
        modifier = Modifier
            .widthIn(max = screenWidth * 0.9f)
            .clickable {
                navigationViewModel.destination = "mainscreen"
                navController.navigate("mainscreen")
            }
            .padding(8.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Accedi per il culto!",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Per i culti e le evangelizzazioni",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            Image(
                painter = painterResource(id = R.drawable.disegno),
                contentDescription = "Culto",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .aspectRatio(1f)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun CardProve(
    navController: NavHostController,
    navigationViewModel: NavigationViewModel,
    screenWidth: Dp,
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.BottomCenter // Centra in basso
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = screenWidth * 0.9f)
                .clickable {
                    navigationViewModel.destination = "prove"
                    navController.navigate("prove")
                }
                .padding(8.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.disegno_prove),
                    contentDescription = "Prove",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .aspectRatio(1f)
                        .padding(16.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Accedi per il Gruppo Colle!",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Per le prove del coro giovani e del Gruppo Colle",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

}

@Composable
fun Footer(context: Context) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "© 2024 MusicADI Colle. Tutti i diritti riservati.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Visita il nostro sito web!",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://musicadicolle.altervista.org/")
                        )
                        context.startActivity(intent)
                    }
            )
        }
    }
}

@Composable
fun PathEffectSquiggles(
    color: Color,
    wavelength: Float,
    amplitude: Float,
    durationMillis: Int,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        val waveLengthPx = wavelength
        val amplitudePx = amplitude
        val phaseShift = waveLengthPx * phase

        val height = size.height
        val width = size.width

        val path = Path().apply {
            moveTo(-waveLengthPx + phaseShift, height / 2)

            var x = -waveLengthPx + phaseShift
            while (x < width + waveLengthPx) {
                relativeQuadraticTo(
                    waveLengthPx / 4, -amplitudePx,
                    waveLengthPx / 2, 0f
                )
                relativeQuadraticTo(
                    waveLengthPx / 4, amplitudePx,
                    waveLengthPx / 2, 0f
                )
                x += waveLengthPx
            }

            // Chiudi la forma per riempirla
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = path,
            color = color
        )
    }
}

@Composable
fun GlassmorphismCard(
    modifier: Modifier = Modifier,
    blurRadius: Dp = 16.dp,
    backgroundColor: Color = Color.White.copy(alpha = 0.15f),
    cornerRadius: Dp = 16.dp,
    cardOffsetY: Dp = 0.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .offset(y = cardOffsetY)
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.Transparent)
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(cornerRadius))
    ) {
        // Sfondo sfocato e semitrasparente
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor)
                .blur(blurRadius)
        )

        // Contenuto in primo piano
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
        ) {
            content()
        }
    }
}
