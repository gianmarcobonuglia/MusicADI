package com.example.musicadicolle.provecoro

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.musicadicolle.Account
import com.example.musicadicolle.CalendarEvent
import com.example.musicadicolle.NavigationViewModel
import com.example.musicadicolle.Pdf
import com.example.musicadicolle.PdfListItem
import com.example.musicadicolle.R
import com.example.musicadicolle.errorMessage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ServiziScreenProve(navController: NavHostController, navigationViewModel: NavigationViewModel){

    var events by remember { mutableStateOf<List<CalendarEvent>>(emptyList()) }

    // Recupera gli eventi da Firestore
    LaunchedEffect(Unit) {
        getEventsFromFirestore { loadedEvents ->
            events = loadedEvents
        }
    }

    val auth = Firebase.auth
    val user = auth.currentUser
    val account = Account.fromFirebaseUser(user)

    if (user != null) {
        account?.name = user.displayName.toString()
        account?.email = user.email.toString()
        account?.photoUrl = user.photoUrl.toString()
        account?.idToken = user.uid
    }

    if (account == null){
        navController.navigate("login")
    }

    var showError by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // Avvia un nuovo coroutine
        launch {
            // Attendi per un certo periodo di tempo (ad esempio 3 secondi)
            delay(3000)
            // Imposta lo stato per nascondere il testo dell'errore
            showError = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (user != null) {
            AdaptiveLayoutProve(navController = navController, navView = navigationViewModel, url = user.photoUrl.toString()) {

                // Contenuto della schermata
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Prossimi Impegni",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CalendarElevatedScreen(events = events)

                    HorizontalDivider(
                        thickness = 2.dp,
                    )

                    Text(
                        text = "Cantici preferiti",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )

                    Spacer(modifier= Modifier.height(8.dp))

                    PreferitiScreen()

                    if(showError){
                        Text(errorMessage)
                    }
                }
            }
        }
    }
}

@Composable
fun PreferitiScreen() {
    var favoritePdfs by remember { mutableStateOf<List<Pdf>>(emptyList()) }
    val auth = Firebase.auth
    val user = auth.currentUser
    val tipoPdf = "Coro"

    LaunchedEffect(Unit) {
        user?.displayName?.let { userId ->
            getFavoritePdfsFromFirestore(userId) { loadedPdfs ->
                favoritePdfs = loadedPdfs
            }
        }
    }
    LazyColumn(modifier = Modifier.padding(8.dp)) {
        items(favoritePdfs) { pdf ->
            PdfListItem(pdf = pdf, isFavorite = pdf.isFavorite, tipoPdf = tipoPdf, modifier = Modifier, onFavoriteChange = { updatedPdf, isFavorite, tipoPdf ->
                user?.displayName?.let { userId ->
                    updatePdfFavoriteStatus(updatedPdf, isFavorite, userId, tipoPdf)
                }
            },
                onPdfClick = {},
                onPdfPress = {}
            )
        }
    }
}

fun getFavoritePdfsFromFirestore(userId: String, onPdfsLoaded: (List<Pdf>) -> Unit) {
    val db = Firebase.firestore
    db.collection("Preferiti")
        .whereEqualTo("userId", userId)
        .whereEqualTo("isFavorite", true)
        .whereEqualTo("tipoPdf", "Coro")
        .get()
        .addOnSuccessListener { result ->
            val favoritePdfs = result.mapNotNull { document ->
                document.getString("numero")?.let { numero ->
                    document.getString("nome")?.let { nome ->
                        document.getBoolean("isFavorite")?.let { isFavorite ->
                            Pdf(
                                numero = numero,
                                nome = nome,
                                isFavorite = isFavorite,
                                userId = userId
                            )
                        }
                    }
                }
            }
            onPdfsLoaded(favoritePdfs)
        }
        .addOnFailureListener { exception ->
            Log.e("FavoritePdfs", "Error getting documents: ", exception)
            onPdfsLoaded(emptyList())
        }
}

fun updatePdfFavoriteStatus(pdf: Pdf, isFavorite: Boolean, userId: String, tipoPdf: String) {
    val db = Firebase.firestore
    val docId = "${userId}_${pdf.numero}_$tipoPdf" // Usa un ID unico per utente e PDF
    val pdfData = mapOf(
        "numero" to pdf.numero,
        "nome" to pdf.nome,
        "isFavorite" to isFavorite,
        "userId" to userId,
        "tipoPdf" to tipoPdf
    )

    if (isFavorite) {
        db.collection("Preferiti").document(docId)
            .set(pdfData)
            .addOnSuccessListener {
                Log.d("UpdateFavorite", "PDF updated successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("UpdateFavorite", "Error updating PDF", exception)
            }
    } else{
        db.collection("Preferiti").document(docId)
            .delete()
            .addOnSuccessListener {
                Log.d("DeleteFavorite", "PDF deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("DeleteFavorite", "Error deleting PDF", exception)
            }
    }

}

@Composable
fun CalendarElevatedScreen(events: List<CalendarEvent>) {
    LazyColumn {
        items(events) { event ->
            CalendarCard(event = event)
        }
    }
}

@Composable
fun CalendarCard(event: CalendarEvent){

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = event.tipo, style = MaterialTheme.typography.bodyMedium)
                Text(text = event.titolo, style = MaterialTheme.typography.bodyMedium)
                Text(text = event.data, style = MaterialTheme.typography.bodySmall)
            }
            if (event.allDay) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_schedule_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun getEventsFromFirestore(onEventsLoaded: (List<CalendarEvent>) -> Unit) {
    val db = Firebase.firestore
    db.collection("Eventi")
        .get()
        .addOnSuccessListener { result ->
            val events = result.mapNotNull { document ->
                val tipo = document.getString("tipo")
                val titolo = document.getString("titolo")
                val data = document.getString("data")
                if (tipo != null && titolo != null && data != null) {
                    CalendarEvent(
                        tipo = tipo,
                        titolo = titolo,
                        data = data
                    )
                } else {
                    null
                }
            }
            onEventsLoaded(events)
        }
        .addOnFailureListener { exception ->
            Log.e("CalendarScreen", "Error getting documents: ", exception)
            onEventsLoaded(emptyList())
            errorMessage = "Errore durante il caricamento dei eventi"
        }
}


