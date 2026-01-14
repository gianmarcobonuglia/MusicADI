@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.example.musicadicolle.generale

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.musicadicolle.Account
import com.example.musicadicolle.NavigationViewModel
import com.example.musicadicolle.Pdf
import com.example.musicadicolle.PdfListItem
import com.example.musicadicolle.R
import com.example.musicadicolle.errorMessage
import com.example.musicadicolle.getPdfListFromFirebase
import com.example.musicadicolle.retrieveAndOpenPdf
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElencoScreen(navController: NavHostController, navigationViewModel: NavigationViewModel) {

    var context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    var pdfList by remember { mutableStateOf<List<Pdf>>(emptyList()) }

    var selectedPdf by remember { mutableStateOf<Pdf?>(null) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val options = listOf("Inni di Lode", "Aggiunta", "Evangelizzazione", "Culto dei giovani")
    var selectedIndex by remember { mutableIntStateOf(0) }

    var tipoPdf by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }

    val auth = Firebase.auth

    val user = auth.currentUser
    val account = Account.fromFirebaseUser(user)

    if (user != null) {
        account?.name = user.displayName.toString()
        account?.email = user.email.toString()
        account?.photoUrl = user.photoUrl.toString()
        account?.idToken = user.uid
    }

    Log.d("Elenco / Account: ", account.toString())

    if (user != null) {
        AdaptiveLayout(
            navController = navController,
            navView = navigationViewModel,
            url = user.photoUrl.toString()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
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
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .animateContentSize(),
                        space = 8.dp,
                    ) {
                        options.forEachIndexed { index, label ->
                            val icon = when (index) {
                                0 -> painterResource(id = R.drawable.g8)
                                1 -> painterResource(id = R.drawable.inni)
                                2 -> painterResource(id = R.drawable.g9)
                                3 -> painterResource(id = R.drawable.g10)  // Aggiungi altri casi per gli altri elementi se necessario
                                else -> Icons.Filled.Favorite // Icona di fallback nel caso in cui non ci sia corrispondenza
                            }
                            var text by remember { mutableStateOf("") }
                            if (screenWidth > 1200.dp) {
                                text = when (index) {
                                    0 -> "Inni di Lode"
                                    1 -> "Aggiunta"
                                    2 -> "Evangelizzazione"
                                    3 -> "Culto dei giovani"
                                    else -> "" // Aggiungi altri casi per gli altri elementi se necessario
                                }
                            }

                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
                                ),
                                onClick = { selectedIndex = index },
                                selected = index == selectedIndex,
                                icon = {
                                    Icon(
                                        painter = icon as Painter,
                                        contentDescription = label,
                                        tint = Color.Unspecified
                                    )
                                },
                                label = { Text(text = text) }
                            )
                        }
                    }
                }
                val textShow = when (selectedIndex) {
                    0 -> "Inni di Lode"
                    1 -> "Aggiunta"
                    2 -> "Evangelizzazione"
                    3 -> "Culto dei giovani"
                    else -> "..."
                }
                if (screenWidth < 1200.dp)
                    Text(text = textShow)

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
                        modifier = Modifier.fillMaxWidth(),
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Filtra i cantici") },
                        shape = MaterialTheme.shapes.large
                    )
                }

                // Recupera i PDF dal database Firebase
                getPdfListFromFirebase(selectedIndex) { pdf ->
                    pdfList = pdf.filter {
                        it.numero.startsWith(searchText) ||
                                it.nome.lowercase(Locale.getDefault())
                                    .contains(searchText.lowercase(Locale.getDefault()))
                    }
                }

                tipoPdf = when (selectedIndex) {
                    0 -> "Inni"
                    1 -> "Aggiunta"
                    2 -> "Evangelizzazione"
                    3 -> "Culto dei giovani"
                    else -> "Coro"
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 4.dp)
                ) {
                    items(pdfList.sortedBy { it.numero.padStart(3, '0') }) { pdf ->
                        PdfListItem(
                            pdf = pdf,
                            isFavorite = pdf.isFavorite,
                            tipoPdf = tipoPdf,
                            modifier = Modifier,
                            onFavoriteChange = { updatedPdf, isFavorite, tipoPdf ->
                                user.displayName?.let { userId ->
                                    updatePdfFavoriteStatus(updatedPdf, isFavorite, userId, tipoPdf)
                                }
                            },
                            onPdfClick = {
                                retrieveAndOpenPdf(
                                    coroutineScope = coroutine,
                                    context = context,
                                    pdf.numero,
                                    selectedIndex,
                                    errorMessage = errorMessage
                                )
                            },
                            onPdfPress = {
                                selectedPdf = pdf  // <- qui salvo il pdf selezionato
                            }
                        )
                    }
                }

                // Mostra il dialog solo se c’è un PDF selezionato
                selectedPdf?.let { pdf ->
                    Dialog(onDismissRequest = { selectedPdf = null }) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.disegno),
                                    contentDescription = "Thumbnail del cantico",
                                    modifier = Modifier.size(100.dp)
                                )
                                Text(text = "Numero: ${pdf.numero}", style = MaterialTheme.typography.titleMedium)
                                Text(text = "Nome: ${pdf.nome}", style = MaterialTheme.typography.bodyLarge)
                                Text(text = "Descrizione: null", style = MaterialTheme.typography.bodyMedium)

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(onClick = { selectedPdf = null }) {
                                    Text("Chiudi")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}