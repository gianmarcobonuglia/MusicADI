package com.example.musicadicolle

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(navController: NavHostController, navigationViewModel: NavigationViewModel) {

    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val auth = Firebase.auth

    val firestore = FirebaseFirestore.getInstance()

    Log.d("LoginScreen", navigationViewModel.destination)

    val signInEmail = SignInWithEmailClass(context)

    val signInWithGoogleHelper = SignInWithGoogleHelper(
        context = context,
        auth = auth,
        firestore = firestore,
        onSuccess = { navController.navigate(navigationViewModel.destination) },
        onFailure = { message -> errorMessage = message }
    )

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        signInWithGoogleHelper.handleSignInResult(result)
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("MusicADI", color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.padding(24.dp), style = MaterialTheme.typography.displaySmall, fontFamily = FontFamily.SansSerif)

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(50.dp))
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(18.dp, 18.dp))
                        .padding(24.dp)
                        .fillMaxSize()
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .fillMaxHeight()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text("Che piacere vederti", textAlign = TextAlign.Center)
                        Text("Accedi qui sotto", textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(32.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            placeholder = { Text("Inserisci la password") },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (passwordVisible)
                                    painterResource(id = R.drawable.ic_visibility_off) // Icona per nascondere
                                else
                                    painterResource(id = R.drawable.ic_visibility_on) // Icona per mostrare

                                IconButton(onClick = {
                                    passwordVisible = !passwordVisible
                                }) {
                                    Icon(
                                        painter = image,
                                        contentDescription = if (passwordVisible) "Nascondi password" else "Mostra password",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                if (email.isEmpty() && password.isEmpty())
                                    Toast.makeText(
                                        context,
                                        "Compilare tutti i campi!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                else {
                                    signInEmail.signInWithEmail(
                                        email,
                                        password,
                                        navController,
                                        navigationViewModel
                                    )
                                    navigationViewModel.destination = "choosescreen"
                                }
                            }) {
                            Text(text = "Accedi!")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Password dimenticata?",
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("ForgottenPassword")
                                }
                        )

                        Spacer(modifier = Modifier.height(50.dp))

                        HorizontalDivider(thickness = 1.dp, color = Color.Black)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Oppure accedi con")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                colors = ButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = Color.Gray,
                                    disabledContentColor = Color.White
                                ),
                                onClick = {
                                    signInWithGoogleHelper.launchSignIn(googleSignInLauncher)
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.google_logo),
                                    contentDescription = "Login con Google"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Google")
                            }
                        }
                        Spacer(modifier = Modifier.height(50.dp))
                        Text(
                            "Non fai parte del nostro gruppo? \nRegistrati ora!",
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("SignIn")
                                },
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(50.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.splash_screen),
                                contentDescription = "image"
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "© 2024 MusicADI Colle. \n Accedendo accetti i nostri Termini e Condizioni d'uso.\n Visita il sito Web per ulteriori informazioni.\n\nTutti i diritti riservati.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Carousel(
    items: List<Painter>, // Lista delle immagini da visualizzare nel carousel
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Box() {
        HorizontalPager(
            state = pagerState,
            count = items.size,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Image(
                painter = items[page],
                contentDescription = "Carousel Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(16.dp)
            )
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = Color.Gray,
            indicatorHeight = 8.dp,
            indicatorWidth = 8.dp
        )
    }
}
