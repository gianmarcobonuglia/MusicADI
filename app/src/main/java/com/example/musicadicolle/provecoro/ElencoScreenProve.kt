package com.example.musicadicolle.provecoro

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.musicadicolle.Account
import com.example.musicadicolle.NavigationViewModel
import com.example.musicadicolle.Pdf
import com.example.musicadicolle.PdfListItem
import com.example.musicadicolle.generale.updatePdfFavoriteStatus
import com.example.musicadicolle.getPdfListFromFirebaseProve
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElencoScreenProve(navController: NavHostController, navigationViewModel: NavigationViewModel) {

    var pdfList by remember { mutableStateOf<List<Pdf>>(emptyList()) }
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

    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Elenco", "Spartiti", "Home", "Testo", "Servizi")

    Log.d("Account: ", account.toString())

    Box(modifier = Modifier.fillMaxSize()) {

        if (user != null) {
            AdaptiveLayoutProve(navController = navController, navView = navigationViewModel, url = user.photoUrl.toString()) {

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

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = searchText,
                            onValueChange = { searchText = it },
                            label = { Text("Filtra i cantici") },
                            shape = MaterialTheme.shapes.large
                        )
                    }

                    // Recupera i PDF dal database Firebase
                    getPdfListFromFirebaseProve() { pdf ->
                        pdfList = pdf.filter {
                            it.numero.startsWith(searchText) ||
                                    it.nome.lowercase(Locale.getDefault())
                                        .contains(searchText.lowercase(Locale.getDefault()))
                        }
                    }

                    tipoPdf = "Coro"

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = 4.dp)
                    ) {
                        items(pdfList.sortedBy {
                            it.numero.padStart(
                                3,
                                '0'
                            )
                        }) { pdf ->
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
                                onPdfClick = {},
                                onPdfPress = {}
                            )
                        }
                    }
                }
            }
        }
    }
}

