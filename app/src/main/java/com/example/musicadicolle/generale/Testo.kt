package com.example.musicadicolle.generale

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.musicadicolle.Account
import com.example.musicadicolle.NavigationViewModel
import com.example.musicadicolle.Pdf
import com.example.musicadicolle.PdfListItem
import com.example.musicadicolle.R
import com.example.musicadicolle.getPdfListTxtFromFirebase
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun TestoScreen(navController: NavHostController, navigationViewModel: NavigationViewModel) {

    val nomeCurrentUser = FirebaseAuth.getInstance().currentUser?.displayName ?: "Anonimo"

    val pdfUrl = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var numeroPdf by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isPdfLoaded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val raccolte = listOf("Inni di Lode / Aggiunta", "Evangelizzazione", "Culto dei Giovani")
    var raccoltaSelezionata by remember { mutableStateOf(raccolte[0]) }

    var rowModifier by remember { mutableFloatStateOf(1f) }
    var notRowModifier by remember { mutableFloatStateOf(3f) }

    var visible by remember { mutableStateOf(true) }
    val pagerState = rememberPagerState()
    var searchText by remember { mutableStateOf("") }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val options = listOf("Inni di Lode / Aggiunta", "Evangelizzazione", "Culto dei giovani")
    var selectedIndex by remember { mutableIntStateOf(0) }
    var selectedList by remember { mutableIntStateOf(0) }
    var pdfList by remember { mutableStateOf<List<Pdf>>(emptyList()) }
    val tipoPdf by remember { mutableStateOf("") }
    var nomePdf by remember { mutableStateOf("") }

    val auth = Firebase.auth

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

    if (user != null) {
        AdaptiveLayoutSpartiti(
            navController = navController,
            navView = navigationViewModel,
            url = user.photoUrl.toString(),
            visible = visible,
            modifier = Modifier.fillMaxSize()
        ) {

            if (screenWidth >= 1200.dp) {
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
                            horizontalArrangement = Arrangement.Absolute.SpaceBetween, // Spazia i componenti all'interno della riga
                            verticalAlignment = Alignment.CenterVertically // Allinea i figli verticalmente
                        ) {
                            OutlinedTextField(
                                value = numeroPdf,
                                onValueChange = { numeroPdf = it },
                                label = { Text("Cerca") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Search,
                                    keyboardType = KeyboardType.Number
                                ), //KeyboardOptions(keyboardType = KeyboardType.Number)
                                shape = MaterialTheme.shapes.large
                            )

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
                                        1 -> painterResource(id = R.drawable.g9)
                                        2 -> painterResource(id = R.drawable.g10)  // Aggiungi altri casi per gli altri elementi se necessario
                                        else -> painterResource(id = R.drawable.baseline_star_outline_24) // Icona di fallback nel caso in cui non ci sia corrispondenza
                                    }
                                    var text by remember { mutableStateOf("") }
                                    if (screenWidth > 1200.dp) {
                                        text = when (index) {
                                            0 -> "Inni di Lode / Aggiunta"
                                            1 -> "Evangelizzazione"
                                            2 -> "Culto dei giovani"
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
                                                painter = icon,
                                                contentDescription = label,
                                                tint = Color.Unspecified
                                            )
                                        },
                                        label = { Text(text = text) }
                                    )
                                }
                            }

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

                                            val db = Firebase.firestore

                                            val numeroPdfInt = numeroPdf.toInt()
                                            var path = ""
                                            when (selectedIndex) {
                                                0 -> path =
                                                    if (numeroPdfInt > 700) "TestiAggiunta" else "TestiInni"

                                                1 -> path = "TestiEvangelizzazione"
                                                2 -> path = "TestiCultoGiovani"
                                            }

                                            Log.d("Firestore database", "Raccolta: $path")

                                            val querySnapshot = db.collection(path)
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
                                            Log.e(
                                                "PdfViewer",
                                                "Error:",
                                                e
                                            ) // Add logging for debugging
                                        } finally {
                                            isLoading =
                                                false // Imposta lo stato del caricamento su false dopo il caricamento del PDF
                                            isPdfLoaded = true
                                            notRowModifier = 0f
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .padding(start = 8.dp)
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.baseline_search_24),
                                    contentDescription = "Cerca"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cerca Testo")
                            }
                        }
                    }
                }
            } else if (screenWidth < 1200.dp) {
                //sheets music searcher mobiles
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
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding()
                                .zIndex(3f),
                            ) {
                            //Dropdown menu
                            Row(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(20.dp)
                                    .zIndex(2f)
                                    .animateContentSize()
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center, // Spazia i componenti all'interno della riga
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { expanded = true }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(
                                            text = raccoltaSelezionata,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        IconaDropdownAnimata(expanded = expanded)
                                    }

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        raccolte.forEach { raccolta ->
                                            DropdownMenuItem(
                                                text = { Text(raccolta) },
                                                onClick = {
                                                    raccoltaSelezionata = raccolta
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .background(color = MaterialTheme.colorScheme.background)
                                    .padding(20.dp)
                                    .fillMaxWidth()
                                    .zIndex(2f)
                                    .animateContentSize(),
                                horizontalArrangement = Arrangement.SpaceBetween, // Spazia i componenti all'interno della riga
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = numeroPdf,
                                    onValueChange = { numeroPdf = it },
                                    label = { Text("Cerca") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.Search,
                                        keyboardType = KeyboardType.Number
                                    ),
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

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

                                                val db = Firebase.firestore

                                                val numeroPdfInt = numeroPdf.toInt()
                                                var path = ""
                                                when (selectedIndex) {
                                                    0 -> path =
                                                        if (numeroPdfInt > 700) "TestiAggiunta" else "TestiInni"

                                                    1 -> path = "TestiEvangelizzazione"
                                                    2 -> path = "TestiCultoGiovani"
                                                }

                                                Log.d("Firestore database", "Raccolta: $path")

                                                val querySnapshot = db.collection(path)
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
                                                        errorMessage.value =
                                                            "Documento non trovato."
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                errorMessage.value =
                                                    "Errore durante il recupero del documento: ${e.message}"
                                                Log.e(
                                                    "PdfViewer",
                                                    "Error:",
                                                    e
                                                ) // Add logging for debugging
                                            } finally {
                                                isLoading =
                                                    false // Imposta lo stato del caricamento su false dopo il caricamento del PDF
                                                isPdfLoaded = true
                                                notRowModifier = 0f
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .weight(1f)
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.baseline_search_24),
                                        contentDescription = "Cerca"
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Cerca Testo")
                                }
                            }
                        }
                    }
                }
            }

            //horizontal pages
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding()
                    .zIndex(3f),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally(),
                    modifier = Modifier
                        .zIndex(3f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        HorizontalPagerIndicator(
                            pagerState = pagerState,
                            activeColor = MaterialTheme.colorScheme.inversePrimary
                        )
                    }
                    HorizontalPager(
                        state = pagerState,
                        count = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(
                                when (pagerState.currentPage) {
                                    0 -> 300.dp
                                    1 -> 550.dp
                                    else -> 550.dp
                                }
                            )
                    ) { page ->
                        when (page) {
                            0 -> {
                                //MenuPage()
                            }

                            1 -> {
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                modifier = Modifier.fillMaxWidth(),
                                                value = searchText,
                                                onValueChange = {   searchText = it },
                                                label = { Text("Filtra i cantici") },
                                                shape = MaterialTheme.shapes.large
                                            )
                                        }

                                        // Sezione RadioButton per la selezione
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                        ) {
                                            // Ciclo per creare i RadioButton con un id per ciascun elemento
                                            (0..3).forEach { index ->
                                                RadioButton(
                                                    selected = selectedList == index,
                                                    onClick = { selectedList = index },
                                                )
                                            }
                                        }

                                        // Recupera i PDF dal database Firebase
                                        getPdfListTxtFromFirebase(selectedList) { pdf ->
                                            pdfList = pdf.filter {
                                                it.numero.startsWith(searchText) ||
                                                        it.nome.lowercase(Locale.getDefault())
                                                            .contains(
                                                                searchText.lowercase(
                                                                    Locale.getDefault()
                                                                )
                                                            )
                                            }
                                        }

                                        // Sezione per visualizzare la lista LazyColumn
                                        LazyColumn(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding() // Padding per evitare sovrapposizione con la Row dei RadioButton
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
                                                    onFavoriteChange = { _, _, _ -> },
                                                    modifier = Modifier,
                                                    onPdfClick = { //Chiude la tastiera
                                                        val inputMethodManager =
                                                            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                                        val currentFocus =
                                                            (context as? Activity)?.currentFocus
                                                        val token = currentFocus?.windowToken
                                                        inputMethodManager.hideSoftInputFromWindow(
                                                            token,
                                                            0
                                                        )

                                                        isLoading =
                                                            true // Imposta lo stato del caricamento su true

                                                        // Coroutine per caricare il PDF
                                                        coroutineScope.launch {
                                                            try {
                                                                visible = !visible
                                                                delay(1000)

                                                                val db = Firebase.firestore

                                                                numeroPdf = pdf.numero
                                                                var path = ""
                                                                when (selectedList) {
                                                                    0 -> path =
                                                                        "TestiInni"

                                                                    1 -> path =
                                                                        "TestiAggiunta"

                                                                    2 -> path =
                                                                        "TestiEvangelizzazione"

                                                                    3 -> path = "TestCultoGiovani"
                                                                }
                                                                val pathUtente =
                                                                    "Users/$nomeCurrentUser/$path"

                                                                val querySnapshotUtente =
                                                                    db.collection(pathUtente)
                                                                        .whereEqualTo(
                                                                            "numero",
                                                                            numeroPdf
                                                                        )
                                                                        .get()
                                                                        .await()

                                                                if (!querySnapshotUtente.isEmpty) {
                                                                    val document =
                                                                        querySnapshotUtente.documents.first()
                                                                    nomePdf =
                                                                        document.getString("nome")
                                                                            .toString()
                                                                    pdfUrl.value =
                                                                        document.getString("url")
                                                                            ?: ""
                                                                    Log.d(
                                                                        "PdfViewer",
                                                                        "Caricato PDF utente: $pdfUrl.value"
                                                                    )
                                                                } else {
                                                                    val querySnapshot =
                                                                        db.collection(path)
                                                                            .whereEqualTo(
                                                                                "numero",
                                                                                numeroPdf
                                                                            )
                                                                            .get()
                                                                            .await()

                                                                    for (document in querySnapshot.documents) {
                                                                        nomePdf =
                                                                            document.getString("nome")
                                                                                .toString()
                                                                        val pdfUri =
                                                                            document.getString("url")
                                                                        if (pdfUri != null) {
                                                                            pdfUrl.value = pdfUri
                                                                            Log.d(
                                                                                "PdfViewer",
                                                                                "PDF URL retrieved: $pdfUrl.value"
                                                                            )
                                                                            break // Esci dal ciclo una volta trovato il documento
                                                                        } else {
                                                                            errorMessage.value =
                                                                                "Documento non trovato."
                                                                        }
                                                                    }
                                                                }
                                                            } catch (e: Exception) {
                                                                errorMessage.value =
                                                                    "Errore durante il recupero del documento: ${e.message}"
                                                                Log.e(
                                                                    "PdfViewer",
                                                                    "Error:",
                                                                    e
                                                                ) // Add logging for debugging
                                                            } finally {
                                                                isLoading =
                                                                    false // Imposta lo stato del caricamento su false dopo il caricamento del PDF
                                                                isPdfLoaded = true
                                                                notRowModifier = 0f
                                                            }
                                                        }
                                                    },
                                                    onPdfPress = {  },
                                                )
                                            }
                                        }
                                    }
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
                                        notRowModifier = when (!visible) {
                                            false -> 3f
                                            true -> 0f
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
                                            val connection =
                                                url.openConnection() as HttpURLConnection
                                            if (connection.responseCode == 200) {
                                                val inputStream =
                                                    BufferedInputStream(connection.inputStream)
                                                launch {
                                                    try {
                                                        pdfView.fromStream(inputStream)
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
}