package com.example.musicadicolle

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current
    val color = listOf(
        Color(0xFF4808FE),
        Color(0xFF4580FD),
        Color(0xFF44BCFC),
        Color(0xFF42F8FB),
        Color(0xFFA0FBFC),
        Color(0xFFFDFDFD),
    )

    LaunchedEffect(key1 = true) {
        // Aggiungi un ritardo per mostrare lo splash screen per la durata del video
        delay(7000) // Durata del video in millisecondi
        // Naviga alla schermata principale
        navController.navigate("choosescreen") {
            // Rimuovi lo splash screen dallo stack di navigazione
            popUpTo("splashscreen") { inclusive = true }
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxSize()
    ) {
        AndroidView(
            factory = { context ->
                VideoView(context).apply {
                    setVideoURI(Uri.parse("android.resource://${context.packageName}/${R.raw.video_splash}"))
                    setOnCompletionListener {
                        // Avvia l'Activity principale o l'Activity successiva dopo la splash screen
                        navController.navigate("choosescreen") {
                            popUpTo("splashscreen") { inclusive = true }
                        }
                    }
                    start()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = color,
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
        )
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = rememberNavController())
}
