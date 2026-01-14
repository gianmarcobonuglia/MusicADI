@file:Suppress("DEPRECATION")

package com.example.musicadicolle

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.navigation.NavHostController
import com.facebook.AccessToken
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


val auth = Firebase.auth

class SignInWithGoogleHelper(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,  // Aggiunto Firestore come parametro
    private val onSuccess: () -> Unit,
    private val onFailure: (String) -> Unit
)
{
    private val oneTapClient: SignInClient = Identity.getSignInClient(context)
    private val signInRequest: BeginSignInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()

    fun launchSignIn(launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    launcher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
                } catch (e: Exception) {
                    onFailure(e.message ?: "Sign in failed")
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Sign in failed")
            }
    }


    fun handleSignInResult(result: ActivityResult) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task: Task<AuthResult> ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if (user != null) {
                                // Crea un documento in Firestore con il nome dell'utente come ID del documento
                                val userData = mapOf(
                                    "name" to user.displayName,
                                    "email" to user.email,
                                    "photoUrl" to (user.photoUrl?.toString() ?: ""),
                                    "uid" to user.uid,
                                    "strumento" to "pianoforte"
                                )

                                firestore.collection("Utenti")
                                    .document(user.email ?: user.uid) // Usa il nome come ID, o UID se il nome è null
                                    .set(userData)
                                    .addOnSuccessListener {
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        onFailure("Failed to save user data: ${e.message}")
                                    }
                            } else {
                                onFailure("User is null after authentication")
                            }
                        } else {
                            onFailure(task.exception?.message ?: "Authentication failed")
                        }
                    }
            } else {
                onFailure("No ID token!")
            }
        } catch (e: ApiException) {
            onFailure(e.message ?: "Sign in failed")
        }
    }
}

class SignInWithFacebook : Activity() {
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("TAG", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}

class SignInWIthTwitter {

}

class SignInWithApple {

}

class SignInWithEmailClass (private val context: Context) {
    fun signInWithEmail(email: String, password: String, navController: NavHostController, navigationViewModel: NavigationViewModel) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithEmail:success")
                    val user = auth.currentUser

                    navController.navigate(navigationViewModel.destination)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    if ( task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            context,
                            "Autenticazione fallita, email o password errati!",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                    else if (task.exception is FirebaseNetworkException){
                        Toast.makeText(
                            context,
                            "Autenticazione fallita, non c'è nessuna connessione a Internet!",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
    }
}

class SignUpWithEmailClass (private val context: Context, private val onSuccess: () -> Unit,) {

    val firestore: FirebaseFirestore = Firebase.firestore

    fun signUp(email: String, password: String, nome: String, cognome: String, strumento: String, navController: NavHostController, navigationViewModel: NavigationViewModel){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    val user = auth.currentUser

                    val defaultProfileImageUrl = "https://firebasestorage.googleapis.com/v0/b/musicadi-27a1b.appspot.com/o/account-svgrepo-com.svg?alt=media&token=af5d803e-10d3-4a0b-b6f8-f271fb056985"

                    if (user != null) {
                        // Crea un documento in Firestore con il nome dell'utente come ID del documento
                        val userData = mapOf(
                            "email" to user.email,
                            "photoUrl" to defaultProfileImageUrl,
                            "name" to user.displayName,
                            "nome" to "$nome $cognome",
                            "strumento" to strumento,
                        )

                        firestore.collection("Utenti")
                            .document(user.email ?: user.uid) // Usa il nome come ID, o UID se il nome è null
                            .set(userData)
                            .addOnSuccessListener {
                                navController.navigate(navigationViewModel.destination)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Failed to save user data: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            context,
                            "User is null after authentication",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    if (task.exception is FirebaseNetworkException){
                        Toast.makeText(
                            context,
                            "Autenticazione fallita, non c'è nessuna connessione a Internet!",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
    }
}