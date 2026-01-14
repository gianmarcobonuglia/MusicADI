package com.example.musicadicolle

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@SuppressLint("UnrememberedMutableState")
@Composable
fun SignInScreen(navController: NavHostController, navigationViewModel: NavigationViewModel) {

    val context = LocalContext.current

    var passwordVisible by remember { mutableStateOf(false)}

    var destination by mutableStateOf("")

    val signUpEmail = SignUpWithEmailClass(
        context,
        onSuccess = {
        destination = destination  // Aggiorna la destinazione in base al successo
    },)

    var email by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var strumento by remember { mutableStateOf("") }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            navigationViewModel.signInWithGoogleHelper.handleSignInResult(result)
        }
    )

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(50.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        RoundedCornerShape(18.dp, 18.dp, 18.dp, 18.dp)
                    )
                    .padding(24.dp)
                    .wrapContentSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Box {
                        Text(
                            "Attenzione!\nÈ consigliato fare la registrazione\ncon Google!",
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    Button(onClick = {
                        try{
                            navigationViewModel.signInWithGoogleHelper.launchSignIn(
                            googleSignInLauncher
                            )
                        }
                        catch(e: Exception) {
                            Toast.makeText(
                                context,
                                "Errore durante il sign incon Google: $e",
                                Toast.LENGTH_LONG).show()
                            Log.e(
                                "GoogleSignIn",
                                "Error:",
                                e
                            )
                        }
                    }) {
                        Text("Registrati con Google!")
                    }

                    Spacer(modifier = Modifier.height(50.dp))

                    Text("Se non potete, registratevi compilate i campi richiesti...")

                    Spacer(modifier = Modifier.height(25.dp))

                    //nome
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome") },
                        shape = MaterialTheme.shapes.large,
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    //cognome
                    OutlinedTextField(
                        value = cognome,
                        onValueChange = { cognome = it },
                        label = { Text("Cognome") },
                        shape = MaterialTheme.shapes.large,
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    //email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        shape = MaterialTheme.shapes.large,
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    //password
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
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    //strumento
                    OutlinedTextField(
                        value = strumento,
                        onValueChange = { strumento = it },
                        label = { Text("Strumento") },
                        shape = MaterialTheme.shapes.large,
                    )

                    Spacer(modifier = Modifier.height(35.dp))

                    Button(
                        onClick = {
                            if (email.isEmpty() && password.isEmpty() && nome.isEmpty() && cognome.isEmpty() && strumento.isEmpty())
                                Toast.makeText(
                                    context,
                                    "Compilare tutti i campi!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            else {
                                signUpEmail.signUp(
                                    email, password, nome, cognome, strumento, navController, navigationViewModel
                                )
                                navigationViewModel.destination = "login"
                            }
                        },
                    ) {
                        Text("Registrati!")
                    }
                }
            }
        }
    }
}