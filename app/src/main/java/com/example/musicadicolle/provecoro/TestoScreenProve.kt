package com.example.musicadicolle.provecoro

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.musicadicolle.Account
import com.example.musicadicolle.NavigationViewModel
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestoScreenProve(navController: NavHostController, navigationViewModel: NavigationViewModel) {

    val pdfUrl = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var numeroPdf by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isPdfLoaded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var rowModifier by remember { mutableFloatStateOf(1f) }

    var visible by remember { mutableStateOf(true) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val options = listOf("Inni di Lode", "Aggiunta", "Evangelizzazione", "Culto dei giovani")
    var selectedIndex by remember { mutableIntStateOf(0) }

    val auth = com.google.firebase.Firebase.auth

    val user = auth.currentUser
    val account = Account.fromFirebaseUser(user)

    if (user != null) {
        account?.name = user.displayName.toString()
        account?.email = user.email.toString()
        account?.photoUrl = user.photoUrl.toString()
        account?.idToken = user.uid
    }

    if (account == null) {
        Log.d("SpartitiScreen", "Utente non autenticato")
        navController.navigate("login")
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (user != null) {
            AdaptiveLayoutProve(navController = navController, navView = navigationViewModel, url = user.photoUrl.toString()) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding()
                        .zIndex(3f),
                ) {
                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInVertically(),
                        exit = slideOutVertically(),
                        modifier = Modifier
                            .zIndex(3f)
                            .background(color = MaterialTheme.colorScheme.background)
                    ) {
                        Row(
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.background)
                                .padding(20.dp)
                                .fillMaxWidth()
                                .zIndex(2f)
                                .animateContentSize(),
                            horizontalArrangement = Arrangement.SpaceBetween, // Spazia i componenti all'interno della riga
                            verticalAlignment = Alignment.CenterVertically // Allinea i figli verticalmente
                        ) {
                            OutlinedTextField(
                                value = numeroPdf,
                                onValueChange = { numeroPdf = it },
                                label = { Text("Cerca") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search, keyboardType = KeyboardType.Number), //KeyboardOptions(keyboardType = KeyboardType.Number)
                                shape = MaterialTheme.shapes.large
                            )

                            Button(
                                onClick = {

                                    //Chiude la tastiera
                                    val inputMethodManager =
                                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                    val currentFocus = (context as? Activity)?.currentFocus
                                    val token = currentFocus?.windowToken
                                    inputMethodManager.hideSoftInputFromWindow(token, 0)

                                    isLoading = true // Imposta lo stato del caricamento su true

                                    // Coroutine per caricare il PDF
                                    coroutineScope.launch {
                                        try {

                                            visible = !visible
                                            delay(1000)

                                            val db = com.google.firebase.Firebase.firestore

                                            val documents = db.collection("TestiCoro")



                                            Log.d("Firestore database", "Raccolta: $documents")

                                            val querySnapshot = documents
                                                .whereEqualTo("numero", numeroPdf)
                                                .get()
                                                .await()

                                            for (document in querySnapshot.documents) {
                                                val pdfUri = document.getString("url")
                                                if (pdfUri != null) {
                                                    pdfUrl.value = pdfUri
                                                    Log.d(
                                                        "PdfViewer",
                                                        "PDF URL retrieved: $pdfUrl.value"
                                                    )
                                                    break // Esci dal ciclo una volta trovato il documento
                                                } else {
                                                    errorMessage.value = "Documento non trovato."
                                                }
                                            }
                                        } catch (e: Exception) {
                                            errorMessage.value =
                                                "Errore durante il recupero del documento: ${e.message}"
                                            Log.e("PdfViewer", "Error:", e) // Add logging for debugging
                                        } finally {
                                            isLoading =
                                                false // Imposta lo stato del caricamento su false dopo il caricamento del PDF
                                            isPdfLoaded = true
                                        }
                                    }
                                },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text("Cerca cantico")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(top = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(rowModifier)
                ) {
                    if (isLoading) {
                        // Mostra l'indicatore di caricamento
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    // Mostra il contenuto normale
                    else if (isPdfLoaded) {
                        AndroidView(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(all = 4.dp),
                            factory = { context ->
                                PDFView(context, null)
                            },
                            update = { pdfView ->

                                pdfView.setOnClickListener {
                                    visible =
                                        when (visible) {
                                            false -> true
                                            true -> false
                                        }
                                    rowModifier = if (visible) {
                                        0f
                                    } else {
                                        1f
                                    }
                                }

                                pdfView.recycle()
                                pdfView.loadPages()

                                Log.d("PDFViewer", "PDF caricato con successo!")

                                if (!isLoading && isPdfLoaded) {
                                    CoroutineScope(Job()).launch(Dispatchers.IO) {
                                        val url = URL(pdfUrl.value)
                                        val connection = url.openConnection() as HttpURLConnection
                                        if (connection.responseCode == 200) {
                                            val inputStream =
                                                BufferedInputStream(connection.inputStream)
                                            launch {
                                                try {
                                                    val pdfDocument = pdfView.fromStream(inputStream)
                                                        .defaultPage(0)
                                                        .enableSwipe(true)
                                                        .swipeHorizontal(false)
                                                        .enableDoubletap(false)
                                                        .pageFitPolicy(FitPolicy.WIDTH)
                                                        .fitEachPage(true)
                                                        .load() // Carica il nuovo documento PDF
                                                    pdfView.jumpTo(0, true)
                                                } catch (e: Exception) {
                                                    errorMessage.value =
                                                        "Errore durante il recupero del PDF: ${e.message}"
                                                    Log.e("PdfViewer", "Error:", e)
                                                }
                                            }
                                        } else {
                                            errorMessage.value =
                                                "Errore di connessione: ${connection.responseCode}"
                                        }
                                    }
                                }
                                Log.d("PDFViewer", "Il PDF si dovrebbe vedere...")
                            }
                        )
                    } else if (errorMessage.value.isNotEmpty()) {
                        Text(text = errorMessage.value, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
