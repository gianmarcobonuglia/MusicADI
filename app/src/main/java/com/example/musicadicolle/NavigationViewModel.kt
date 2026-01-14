package com.example.musicadicolle

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NavigationViewModel(context: Context) : ViewModel() {

    var destination by mutableStateOf("")  // Stato per la destinazione della navigazione

    // Istanza di FirebaseAuth
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Istanza di Firestore
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Inizializza l'helper di sign-in con Google
    val signInWithGoogleHelper = SignInWithGoogleHelper(
        context = context,
        auth = auth,
        firestore = firestore,
        onSuccess = {
            destination = destination  // Aggiorna la destinazione in base al successo
        },
        onFailure = { errorMessage ->
            // Gestione del fallimento (puoi anche aggiornare lo stato con un messaggio di errore se necessario)
            println("Sign-In failed: $errorMessage")
        }
    )
}
