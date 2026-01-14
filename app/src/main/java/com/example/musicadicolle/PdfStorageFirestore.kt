package com.example.musicadicolle

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

// Funzione per caricare un file PDF su Firebase Storage e aggiungere l'URL al Firestore
fun uploadPdf(
    nomePdf: String,
    numeroPdf: String,
    uri: Uri,
    tipoPdf: String,
    showSnackbar: () -> Unit, // Aggiungi parametro per controllare la visibilità dello Snackbar
) {
    var tipoPdfPath = ""
    when (tipoPdf) {
        "Evangelizzazione" ->
            tipoPdfPath = "Evangelizzazione"

        "Aggiunta" ->
            tipoPdfPath = "Spartiti"

        "Inni" ->
            tipoPdfPath = "SpartitiInni"

        "Culto dei Giovani" ->
            tipoPdfPath = "CultoGiovani"

        "Coro dei giovani/Gruppo Colle" ->
            tipoPdfPath = "Coro"
    }
    val storageRef =
        FirebaseStorage.getInstance().reference.child(tipoPdfPath).child("Cantico $numeroPdf")
    storageRef.putFile(uri)
        .addOnSuccessListener { _ ->
            // Il PDF è stato caricato con successo
            storageRef.downloadUrl
                .addOnSuccessListener { downloadUri ->
                    val pdfUrl = downloadUri.toString()
                    // Aggiungi l'URL del PDF al Firestore
                    addPdfUrlToFirestore(nomePdf, numeroPdf, pdfUrl, tipoPdf)
                    showSnackbar()// Imposta lo stato dello Snackbar su true
                }
        }
        .addOnFailureListener { exception ->
            Log.e("UploadPDF", "Error uploading PDF", exception)
            Log.d("UploadPDF", "URI PDF: $uri")
            // Gestisci l'errore, ad esempio mostrando un messaggio all'utente
        }
}

@SuppressLint("SuspiciousIndentation")
fun retrieveAndOpenPdf(
    coroutineScope: CoroutineScope,
    context: Context,
    numeroPdf: String,
    selectedIndex: Int,
    errorMessage: String, // Aggiunta variabile per gestire errori
) {
    var error = errorMessage
// Coroutine per caricare il PDF
    coroutineScope.launch {
        try {
            val db = Firebase.firestore
            val path = when (selectedIndex) {
                0 -> "SpartitiInni"
                1 -> "Spartiti"
                2 -> "Evangelizzazione"
                3 -> "CultoGiovani"
                else -> ""
            }

            Log.d("Firestore database", "Raccolta: $path")

            val querySnapshot = db.collection(path)
                .whereEqualTo("numero", numeroPdf)
                .get()
                .await()

            val document = querySnapshot.documents.firstOrNull()
            if (document != null) {
                val pdfUri = document.getString("url")
                if (pdfUri != null) {
                    Log.d("PdfViewer", "PDF URL retrieved: $pdfUri")
                    openPdf(pdfUri, context) // Apri il PDF con una funzione esterna
                } else {
                    error = "Documento non trovato."
                }
            } else {
                error = "Documento non trovato."
            }
        } catch (e: Exception) {
            error = "Errore durante il recupero del documento: ${e.message}"
            Log.e("PdfViewer", "Error:", e)
        }
    }
}

fun openPdf(pdfUri: String, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUri))
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    context.startActivity(intent)
}

// Funzione per aggiungere l'URL del PDF al Firestore
fun addPdfUrlToFirestore(nomePdf: String, numeroPdf: String, pdfUrl: String, tipoPdf: String) {

    val db = FirebaseFirestore.getInstance()
    val pdfData = hashMapOf(
        "url" to pdfUrl,
        "nome" to nomePdf,
        "numero" to numeroPdf
        // Aggiungi altri campi del documento se necessario
    )

    var tipoPdfPath = ""
    when (tipoPdf) {
        "Evangelizzazione" ->
            tipoPdfPath = "Evangelizzazione"

        "Aggiunta" ->
            tipoPdfPath = "Spartiti"

        "Inni" ->
            tipoPdfPath = "SpartitiInni"

        "Culto dei Giovani" ->
            tipoPdfPath = "CultoGiovani"
    }

    // Aggiungi i dati del PDF al documento appropriato nel Firestore
    // Controllo se il documento esiste già
    db.collection(tipoPdfPath).document("Cantico $numeroPdf")
        .get()
        .addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                // Il documento esiste già, potresti voler gestire questa situazione
                Log.d("AddPdfUrlToFirestore", "Document already exists")
            } else {
                // Il documento non esiste, aggiungilo al Firestore
                db.collection(tipoPdfPath).document("Cantico $numeroPdf")
                    .set(pdfData)
                    .addOnSuccessListener {
                        Log.d("AddPdfUrlToFirestore", "PDF URL added successfully")
                        // Gestisci il successo, ad esempio mostrando un messaggio all'utente
                    }
                    .addOnFailureListener { exception ->
                        Log.e("AddPdfUrlToFirestore", "Error adding PDF URL", exception)
                        // Gestisci l'errore, ad esempio mostrando un messaggio all'utente
                    }
            }
        }
        .addOnFailureListener { exception ->
            Log.e("AddPdfUrlToFirestore", "Error adding PDF URL", exception)
            // Gestisci l'errore, ad esempio mostrando un messaggio all'utente
        }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PdfListItem(
    pdf: Pdf,
    isFavorite: Boolean,
    tipoPdf: String,
    modifier: Modifier,
    onFavoriteChange: (Pdf, Boolean, String) -> Unit,
    onPdfClick: (Pdf) -> Unit,
    onPdfPress: (Pdf) -> Unit,
) {

    var isTrueFavorite by remember { mutableStateOf(isFavorite) }
    val haptics = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .combinedClickable(
                    onClick = { onPdfClick(pdf) },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onPdfPress(pdf) }
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(
                            data = R.drawable.baseline_queue_music_24
                        ).apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                            placeholder(R.drawable.baseline_music_note_24)
                        }).build()
                    ),
                    contentDescription = "Thumbnail del PDF",
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(text = "Numero: ${pdf.numero}")
                    Text(text = "Nome: ${pdf.nome}")
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        isTrueFavorite = !isFavorite
                        onFavoriteChange(pdf, isTrueFavorite, tipoPdf)
                    }
                ) {
                    if (isTrueFavorite) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_star_24),
                            contentDescription = null
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_star_outline_24),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

// Funzione per recuperare i PDF dal database Firebase
fun getPdfListFromFirebase(
    selectedIndex: Int,
    onPdfListReady: (List<Pdf>) -> Unit,
) {
    val auth = Firebase.auth
    val user = auth.currentUser

    if (user != null) {
        val idToken = user.getIdToken(false).result?.token
        Log.d("Auth", "User ID: ${user.uid}, Email: ${user.email}, Token: $idToken")
        val account = Account.fromFirebaseUser(user)
        Log.d("getPdfListFromFirebase", "Account: $account")

        if (account != null) {
            val db = Firebase.firestore
            Log.d("getPdfListFromFirebase", "Database: $db")

            if (selectedIndex >= 0) {
                var path = ""
                when (selectedIndex) {
                    0 -> path = "SpartitiInni"
                    1 -> path = "Spartiti"
                    2 -> path = "Evangelizzazione"
                    3 -> path = "CultoGiovani"
                    4 -> path = "Coro"
                }

                Log.d("getPdfListFromFirebase", "TipoPdf: $path")
                Log.d("getPdfListFromFirebase", "CollectionPath: ${db.collection(path)}")

                db.collection(path)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        val pdfList = documentSnapshot.mapNotNull { document ->
                            document.getString("numero")?.let { numero ->
                                document.getString("nome")?.let { nome ->
                                    Pdf(
                                        numero = numero,
                                        nome = nome
                                    )
                                }
                            }
                        }
                        onPdfListReady(pdfList)
                    }
                    .addOnFailureListener { exception ->
                        Log.e(
                            "getPdfListFromFirebase",
                            "Error fetching PDF list: ${exception.message}",
                            exception
                        )
                        if (exception is FirebaseFirestoreException) {
                            Log.e(
                                "getPdfListFromFirebase",
                                "Firestore Error Code: ${exception.code}"
                            )
                        }
                        onPdfListReady(emptyList()) // Return an empty list on failure
                    }
            } else {
                Log.e("getPdfListFromFirebase", "tipoPdf is empty")
                onPdfListReady(emptyList()) // Return an empty list if tipoPdf is empty
            }
        } else {
            Log.e("getPdfListFromFirebase", "Account is null")
            onPdfListReady(emptyList()) // Return an empty list if account is null
        }
    } else {
        Log.e("getPdfListFromFirebase", "User is not authenticated")
        onPdfListReady(emptyList()) // Return an empty list if user is not authenticated
    }
}

// Funzione per recuperare i testi dal database Firebase
fun getPdfListTxtFromFirebase(
    selectedIndex: Int,
    onPdfListReady: (List<Pdf>) -> Unit,
) {
    val auth = Firebase.auth
    val user = auth.currentUser

    if (user != null) {
        val idToken = user.getIdToken(false).result?.token
        Log.d("Auth", "User ID: ${user.uid}, Email: ${user.email}, Token: $idToken")
        val account = Account.fromFirebaseUser(user)
        Log.d("getPdfListFromFirebase", "Account: $account")

        if (account != null) {
            val db = Firebase.firestore
            Log.d("getPdfListFromFirebase", "Database: $db")

            if (selectedIndex >= 0) {
                var path = ""
                when (selectedIndex) {
                    0 -> path = "TestiInni"
                    1 -> path = "TestiAggiunta"
                    2 -> path = "TestiEvangelizzazione"
                    3 -> path = "TestiCultoGiovani"
                    4 -> path = "TestiCoro"
                }

                Log.d("getPdfListFromFirebase", "TipoPdf: $path")
                Log.d("getPdfListFromFirebase", "CollectionPath: ${db.collection(path)}")

                db.collection(path)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        val pdfList = documentSnapshot.mapNotNull { document ->
                            document.getString("numero")?.let { numero ->
                                document.getString("nome")?.let { nome ->
                                    Pdf(
                                        numero = numero,
                                        nome = nome
                                    )
                                }
                            }
                        }
                        onPdfListReady(pdfList)
                    }
                    .addOnFailureListener { exception ->
                        Log.e(
                            "getPdfListFromFirebase",
                            "Error fetching PDF list: ${exception.message}",
                            exception
                        )
                        if (exception is FirebaseFirestoreException) {
                            Log.e(
                                "getPdfListFromFirebase",
                                "Firestore Error Code: ${exception.code}"
                            )
                        }
                        onPdfListReady(emptyList()) // Return an empty list on failure
                    }
            } else {
                Log.e("getPdfListFromFirebase", "tipoPdf is empty")
                onPdfListReady(emptyList()) // Return an empty list if tipoPdf is empty
            }
        } else {
            Log.e("getPdfListFromFirebase", "Account is null")
            onPdfListReady(emptyList()) // Return an empty list if account is null
        }
    } else {
        Log.e("getPdfListFromFirebase", "User is not authenticated")
        onPdfListReady(emptyList()) // Return an empty list if user is not authenticated
    }
}

fun getPdfListFromFirebaseProve(
    onPdfListReady: (List<Pdf>) -> Unit,
) {
    val auth = Firebase.auth
    val user = auth.currentUser

    if (user != null) {
        val idToken = user.getIdToken(false).result?.token
        Log.d("Auth", "User ID: ${user.uid}, Email: ${user.email}, Token: $idToken")
        val account = Account.fromFirebaseUser(user)
        Log.d("getPdfListFromFirebase", "Account: $account")

        if (account != null) {
            val db = Firebase.firestore
            Log.d("getPdfListFromFirebase", "Database: $db")

            val path = "Coro"


            Log.d("getPdfListFromFirebase", "TipoPdf: $path")
            Log.d("getPdfListFromFirebase", "CollectionPath: ${db.collection(path)}")

            db.collection(path)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val pdfList = documentSnapshot.mapNotNull { document ->
                        document.getString("numero")?.let { numero ->
                            document.getString("nome")?.let { nome ->
                                Pdf(
                                    numero = numero,
                                    nome = nome
                                )
                            }
                        }
                    }
                    onPdfListReady(pdfList)
                }
                .addOnFailureListener { exception ->
                    Log.e(
                        "getPdfListFromFirebase",
                        "Error fetching PDF list: ${exception.message}",
                        exception
                    )
                    if (exception is FirebaseFirestoreException) {
                        Log.e(
                            "getPdfListFromFirebase",
                            "Firestore Error Code: ${exception.code}"
                        )
                    }
                    onPdfListReady(emptyList()) // Return an empty list on failure
                }
        } else {
            Log.e("getPdfListFromFirebase", "Account is null")
            onPdfListReady(emptyList()) // Return an empty list if account is null
        }
    } else {
        Log.e("getPdfListFromFirebase", "User is not authenticated")
        onPdfListReady(emptyList()) // Return an empty list if user is not authenticated
    }
}

fun getPdfListEdit(updateList: (Map<String, List<Pdf>>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val user = Firebase.auth.currentUser

    if (user != null) {
        val userCollectionRef = db.collection("Users").document(user.displayName ?: "UnknownUser")

        userCollectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("Firestore", "Errore nel caricamento dei cantici", error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val pdfMap = mutableMapOf<String, List<Pdf>>()
                listOf("Spartiti", "SpartitiInni", "Evangelizzazione", "Coro").forEach { category ->
                    val categoryRef = userCollectionRef.collection(category)
                    categoryRef.get().addOnSuccessListener { documents ->
                        val pdfList = documents.mapNotNull { doc ->
                            doc.toObject(Pdf::class.java)
                        }
                        pdfMap[category] = pdfList
                        updateList(pdfMap)
                    }
                }
            } else {
                Log.d("Firestore", "Nessun documento trovato per l'utente.")
            }
        }
    }
}


// Aggiungi la funzione per avanzare alla pagina successiva del PDF
fun nextPage(pdfView: PDFView?) {
    pdfView?.let {
        val nextPage = it.currentPage + 1
        if (nextPage < it.pageCount) {
            it.jumpTo(nextPage, true) // Avanza alla pagina successiva del PDF
        }
    }
}

fun detectBluetoothSignal(context: Context, pdfView: PDFView?) {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    if (bluetoothAdapter == null) {
        // Il dispositivo non supporta il Bluetooth
        Log.e("Bluetooth", "Il dispositivo non supporta il Bluetooth")
        return
    }

    val bluetoothFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
    val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_CONNECTED -> {
                        // Avanza alla pagina successiva del PDF quando il dispositivo Bluetooth è connesso
                        nextPage(pdfView)
                    }

                    BluetoothAdapter.STATE_DISCONNECTED -> {
                        // Gestisci lo stato disconnesso se necessario
                    }
                }
            }
        }
    }

    // Controlla se il ricevitore è già registrato prima di registrarne uno nuovo
    val registeredReceiver = context.registerReceiver(null, bluetoothFilter)
    val alreadyRegistered = registeredReceiver != null

    if (!alreadyRegistered) {
        context.registerReceiver(bluetoothReceiver, bluetoothFilter)
    }
}

@Composable
fun IconFromUrl(url: String, modifier: Modifier = Modifier) {
    val painter = rememberAsyncImagePainter(url)
    Image(
        painter = painter,
        contentDescription = null, // Content description is important for accessibility, set it accordingly
        modifier = modifier
            .size(80.dp)
            .clip(CircleShape) // Adjust size as needed
            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
    )
}

fun uploadPhotoUrl(
    uri: Uri,
    showSnackbar: Boolean,
) {
    val user = Firebase.auth.currentUser
    val storageRef = user?.email?.let {
        FirebaseStorage.getInstance().reference.child("Utenti").child(it)
    }

    storageRef?.putFile(uri)?.addOnSuccessListener { _ ->
        // Il file è stato caricato con successo
        storageRef.downloadUrl
            .addOnSuccessListener { downloadUri ->
                val photoUrl = downloadUri.toString()
                // Aggiungi l'URL della foto a Firestore
                addPhotoUrlToFirestore(photoUrl)
                showSnackbar// Mostra la Snackbar
            }
    }?.addOnFailureListener { exception ->
        Log.e("UploadPhoto", "Errore durante il caricamento della foto", exception)
    }
}

fun addPhotoUrlToFirestore(photoUrl: String) {
    val user = Firebase.auth.currentUser
    val db = FirebaseFirestore.getInstance()

    if (user != null) {
        val userData = mapOf(
            "photoUrl" to photoUrl
        )

        user.email?.let {
            db.collection("Utenti").document(it)
                .update(userData)
                .addOnSuccessListener {
                    Log.d("Firestore", "URL della foto aggiornato con successo!")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Errore durante l'aggiornamento dell'URL della foto", e)
                }
        }
    }
}

suspend fun downloadPdf(context: Context, pdfUrl: String): File? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(pdfUrl)
            val connection = url.openConnection()
            connection.connect()

            val inputStream = connection.getInputStream()
            val file = File(context.cacheDir, "temp.pdf")

            file.outputStream().use { output ->
                inputStream.copyTo(output)
            }

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
