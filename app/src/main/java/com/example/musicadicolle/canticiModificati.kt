package com.example.musicadicolle

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.musicadicolle.generale.AdaptiveLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

@Composable
fun CanticiModificatiScreen(
    navController: NavHostController,
    navigationViewModel: NavigationViewModel,
) {
    val auth = Firebase.auth
    val user = auth.currentUser
    val nomeCurrentUser = user?.displayName ?: "Anonimo"

    var collection by remember { mutableStateOf("")}
    var pdfList by remember { mutableStateOf<Map<String, List<Pdf>>>(emptyMap()) }
    val searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        getPdfListEditRealTime { pdfMap -> pdfList = pdfMap }
    }

    if (user != null) {
        AdaptiveLayout(
            navController = navController,
            navView = navigationViewModel,
            url = user.photoUrl.toString()
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {
                Column {
                    // Divider superiore
                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(24.dp))

                    // Titolo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cantici Modificati",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            painter = painterResource(R.drawable.baseline_edit_document_24),
                            contentDescription = "Cantici Modificati"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider()

                    if (pdfList.isEmpty()) {
                        Text(
                            text = "Nessun cantico modificato trovato.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        // Lista dei cantici modificati
                        val pdfState by rememberUpdatedState(pdfList)
                        LazyColumn {
                            pdfState.forEach { (subCollection, pdfs) ->
                                item {
                                    collection = when (subCollection) {
                                        "Spartiti" -> "Aggiunta"
                                        "SpartitiInni" -> "Inni di Lode"
                                        "Evangelizzazione" -> "Evangelizzazione"
                                        "Coro" -> "Coro"
                                        else -> "Spartiti"
                                    }
                                    Spacer(modifier = Modifier.height(32.dp))
                                    Text(
                                        text = collection,
                                        style = MaterialTheme.typography.headlineSmall,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )

                                }
                                items(pdfs.filter {
                                    it.numero.startsWith(searchText) || it.nome.lowercase(Locale.getDefault())
                                        .contains(searchText.lowercase(Locale.getDefault()))
                                }) { pdf ->
                                    PdfItem(pdf, nomeCurrentUser, subCollection){
                                        pdfList = it
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    HorizontalDivider()

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PdfItem(pdf: Pdf, nomeCurrentUser: String, collection: String, updateList: (Map<String, List<Pdf>>) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row() {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Numero: ${pdf.numero}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Nome: ${pdf.nome}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        deletePdf(pdf.numero.toInt(), nomeCurrentUser, collection, updateList)
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_delete_24),
                    contentDescription = "Elimina"
                )
            }
        }

    }
}

suspend fun deletePdf(numeroPdf: Int, nomeCurrentUser: String, collection: String, updateList: (Map<String, List<Pdf>>) -> Unit) {

    val db = FirebaseFirestore.getInstance()
    val collectionPath = "Users/$nomeCurrentUser/$collection"
    try {
        val documentRef = db.collection(collectionPath).document("Cantico $numeroPdf")
        val documentSnapshot = documentRef.get().await()
        Log.d("deletePDF", "Collection: $collection")

        if (documentSnapshot.exists()) {
            documentRef.delete().await()
            Log.d("Firestore", "PDF eliminato con successo: Cantico $numeroPdf")
            // Dopo l'eliminazione, aggiorna la lista dei PDF
            getPdfListEditRealTime { pdfMap ->
                updateList(pdfMap) // Passa la nuova lista aggiornata
            }
        } else {
            Log.d("Firestore", "Il PDF non esiste già: Cantico $numeroPdf")
        }
    } catch (e: Exception) {
        Log.e("Firestore", "Errore durante l'eliminazione del PDF", e)
    }
}

fun getPdfListEditRealTime(updateList: (Map<String, List<Pdf>>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val user = Firebase.auth.currentUser

    if (user != null) {
        val userCollectionRef = db.collection("Users").document(user.displayName ?: "UnknownUser")
        val pdfMap = mutableMapOf<String, List<Pdf>>()

        listOf("Spartiti", "SpartitiInni", "Evangelizzazione", "Coro").forEach { category ->
            val categoryRef = userCollectionRef.collection(category)

            // Aggiungiamo un listener in tempo reale per ogni sotto-collezione
            categoryRef.addSnapshotListener { documents, error ->
                if (error != null) {
                    Log.e("Firestore", "Errore nel caricamento della collezione $category", error)
                    return@addSnapshotListener
                }

                if (documents != null) {
                    val pdfList = documents.mapNotNull { doc -> doc.toObject(Pdf::class.java) }
                    pdfMap[category] = pdfList
                    updateList(pdfMap)  // Aggiorna la UI in tempo reale
                }
            }
        }
    }
}

