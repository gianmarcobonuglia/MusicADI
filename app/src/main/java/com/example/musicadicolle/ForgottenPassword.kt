package com.example.musicadicolle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForgottenPasswordScreen(
    navController: NavHostController,
    navigationViewModel: NavigationViewModel,
) {

    var email by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(50.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Inserisci il tuo indirizzo email\nper ricevere le istruzioni di recupero", textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(25.dp))

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") }
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    Button(onClick = {
                        sendPasswordReset(email,
                            onSuccess = {
                                // Naviga a una schermata di successo
                                navController.navigate("successScreen")
                            },
                            onFailure = { exception ->
                                // Mostra un messaggio di errore
                                println("Errore: ${exception.message}")
                            }
                        )
                    }) {
                        Text("Invia")
                    }
                }
            }
        }
    }
}

fun sendPasswordReset(email: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                task.exception?.let {
                    onFailure(it)
                }
            }
        }
}
