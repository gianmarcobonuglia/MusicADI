@file:OptIn(ExperimentalPagerApi::class, ExperimentalPagerApi::class)

package com.example.musicadicolle.generale

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.musicadicolle.Account
import com.example.musicadicolle.NavigationViewModel
import com.example.musicadicolle.Pdf
import com.example.musicadicolle.PdfListItem
import com.example.musicadicolle.R
import com.example.musicadicolle.getPdfListFromFirebase
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.pdftron.pdf.config.ViewerConfig
import com.pdftron.pdf.controls.DocumentActivity
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.Locale


// Dati relativi alle annotazioni
data class Line(
    val start: Offset,
    val end: Offset,
    val color: Color,
    val strokeWidth: Float,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpartitiScreen(navController: NavHostController, navigationViewModel: NavigationViewModel) {

    val context = LocalContext.current

    var pdfView: PDFView? = null

    val nomeCurrentUser = FirebaseAuth.getInstance().currentUser?.displayName ?: "Anonimo"

    var pdfList by remember { mutableStateOf<List<Pdf>>(emptyList()) }
    val tipoPdf by remember { mutableStateOf("") }
    var nomePdf by remember { mutableStateOf("") }
    val pagerState = rememberPagerState()
    var searchText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val raccolte = listOf("Inni di Lode / Aggiunta", "Evangelizzazione", "Culto dei Giovani")
    var raccoltaSelezionata by remember { mutableStateOf(raccolte[0]) }

    // Gestione della pagina corrente
    var currentPage by remember { mutableIntStateOf(0) } // Pagina iniziale

    var isDrawingMode by remember { mutableStateOf(false) }

    val pdfUrl = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var numeroPdf by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isPdfLoaded by remember { mutableStateOf(false) }

    var rowModifier by remember { mutableFloatStateOf(1f) }
    var notRowModifier by remember { mutableFloatStateOf(3f) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    var visible by remember { mutableStateOf(true) }
    val options = listOf("Inni di Lode / Aggiunta", "Evangelizzazione", "Culto dei giovani")
    var selectedIndex by remember { mutableIntStateOf(0) }
    var selectedList by remember { mutableIntStateOf(0) }

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
            modifier = Modifier
        ) {

            if (screenWidth >= 1200.dp) {
                Column( //sheets music searcher tablets
                    modifier = Modifier
                        .fillMaxSize()
                        .padding()
                        .zIndex(3f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top// Mantiene l'allineamento in alto
                ) {
                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInVertically(),
                        exit = slideOutVertically(),
                        modifier = Modifier
                            .zIndex(3f)
                    ) {
                        Row(
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.background)
                                .padding(20.dp)
                                .fillMaxWidth()
                                .zIndex(2f)
                                .animateContentSize(),
                            horizontalArrangement = Arrangement.SpaceBetween, // Spazia i componenti all'interno della riga
                            verticalAlignment = Alignment.CenterVertically // Allinea verticalmente al centro
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
                                shape = MaterialTheme.shapes.large,
                                modifier = Modifier
                                    .wrapContentSize()
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
                                        2 -> painterResource(id = R.drawable.g10)// Aggiungi altri casi per gli altri elementi se necessario
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
                                        selected = selectedIndex == index,
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
                                                    if (numeroPdfInt > 700) "Spartiti" else "SpartitiInni"

                                                1 -> path = "Evangelizzazione"
                                                2 -> path = "CultoGiovani"

                                            }
                                            val pathUtente = "Users/$nomeCurrentUser/$path"
                                            val userDocRef = db.collection(pathUtente)
                                                .whereEqualTo("numero", numeroPdf)
                                                .get()
                                                .await()
                                            if (!userDocRef.isEmpty) {
                                                val document = userDocRef.documents.first()
                                                nomePdf = document.getString("nome").toString()
                                                pdfUrl.value = document.getString("url") ?: ""
                                                Log.d(
                                                    "PdfViewer",
                                                    "Caricato PDF utente: $pdfUrl.value"
                                                )
                                            } else {
                                                Log.d("Firestore database", "Raccolta: $path")

                                                val querySnapshot = db.collection(path)
                                                    .whereEqualTo("numero", numeroPdf)
                                                    .get()
                                                    .await()

                                                for (document in querySnapshot.documents) {
                                                    nomePdf = document.getString("nome").toString()
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
                                                if (querySnapshot.isEmpty) {
                                                    errorMessage.value =
                                                        "Cantico non trovato!...\nNumero sbagliato?"
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
                                    .wrapContentSize()
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.baseline_search_24),
                                    contentDescription = "Cerca"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cerca Spartito")
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
                                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
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
                                    .background(
                                        MaterialTheme.colorScheme.background
                                    )
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
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.Search,
                                        keyboardType = KeyboardType.Number
                                    ),
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier.weight(1f).padding(8.dp)
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
                                                when (raccoltaSelezionata) {
                                                    "Inni di Lode / Aggiunta" -> path = if (numeroPdfInt > 700) "Spartiti" else "SpartitiInni"
                                                    "Evangelizzazione" -> path = "Evangelizzazione"
                                                    "Culto dei Giovani" -> path = "CultoGiovani"

                                                }
                                                val pathUtente = "Users/$nomeCurrentUser/$path"
                                                val userDocRef = db.collection(pathUtente)
                                                    .whereEqualTo("numero", numeroPdf)
                                                    .get()
                                                    .await()
                                                if (!userDocRef.isEmpty) {
                                                    val document = userDocRef.documents.first()
                                                    nomePdf = document.getString("nome").toString()
                                                    pdfUrl.value = document.getString("url") ?: ""
                                                    Log.d(
                                                        "PdfViewer",
                                                        "Caricato PDF utente: $pdfUrl.value"
                                                    )
                                                } else {
                                                    Log.d("Firestore database", "Raccolta: $path")

                                                    val querySnapshot = db.collection(path)
                                                        .whereEqualTo("numero", numeroPdf)
                                                        .get()
                                                        .await()

                                                    for (document in querySnapshot.documents) {
                                                        nomePdf =
                                                            document.getString("nome").toString()
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
                                                    if (querySnapshot.isEmpty) {
                                                        errorMessage.value =
                                                            "Cantico non trovato!...\nNumero sbagliato?"
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
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Cerca cantico")
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
                                    0 -> screenHeight * 0.5f
                                    1 -> screenHeight * 0.5f
                                    else -> screenHeight * 0.5f
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
                                        getPdfListFromFirebase(selectedList) { pdf ->
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
                                                                        "SpartitiInni"

                                                                    1 -> path =
                                                                        "Spartiti"

                                                                    2 -> path =
                                                                        "Evangelizzazione"

                                                                    3 -> path = "CultoGiovani"
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
                                                    onPdfPress = {},
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(top = 8.dp))

            Box( //view for sheets music, loading screen and canva
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

                if (isPdfLoaded) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = 4.dp),
                        factory = { context ->
                            PDFView(context, null).also {
                                pdfView =
                                    it // Salva l'istanza in modo da poterla utilizzare altrove
                            }
                        },
                        update = { pdfView ->

                            pdfView.setOnClickListener {
                                visible = !visible
                                notRowModifier = if (visible) 0f else 3f
                                rowModifier = if (visible) 0f else 1f
                            }

                            pdfView.recycle()
                            pdfView.loadPages()

                            Log.d("PDFViewer", "PDF caricato con successo!")

                            if (!isLoading && isPdfLoaded) {
                                CoroutineScope(Job()).launch(Dispatchers.IO) {
                                    try {
                                        val url = URL(pdfUrl.value)
                                        val connection = url.openConnection() as HttpURLConnection
                                        if (connection.responseCode == 200) {
                                            val inputStream =
                                                BufferedInputStream(connection.inputStream)
                                            withContext(Dispatchers.Main) {
                                                try {
                                                    pdfView.recycle()
                                                    pdfView.loadPages()
                                                    pdfView.fromStream(inputStream)
                                                        .defaultPage(0)
                                                        .enableSwipe(!isDrawingMode)
                                                        .swipeHorizontal(false)
                                                        .pageSnap(true)
                                                        .pageFling(true)
                                                        .pageFitPolicy(FitPolicy.WIDTH)
                                                        .fitEachPage(true)
                                                        .load()
                                                    pdfView.jumpTo(0, true)
                                                } catch (e: Exception) {
                                                    showToast(
                                                        context,
                                                        "Errore durante il caricamento del PDF: ${e.message}"
                                                    )
                                                    Log.e("PdfViewer", "Error:", e)
                                                }
                                            }
                                        } else {
                                            showToast(
                                                context,
                                                "Errore di connessione: ${connection.responseCode}"
                                            )
                                        }
                                    } catch (e: MalformedURLException) {
                                        showToast(
                                            context,
                                            "URL malformato: Cantico non trovato!... Numero sbagliato?\nErrore: ${e.message}!"
                                        )
                                    } catch (e: IOException) {
                                        showToast(context, "Errore di connessione: ${e.message}!")
                                    }
                                }
                            }
                        }
                    )
                    // Modalità Disegno
                    if (isDrawingMode) {
                        //PdfEditorScreen(context = context, pdfPath = pdfUrl.value, numeroPdf = numeroPdf, nomePdf = nomePdf)
                        PdfEditor(pdfUrl.value)
                    }

                    // Pulsanti di navigazione
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = {
                            pdfView?.let {
                                if (currentPage > 0) {
                                    currentPage--
                                    it.jumpTo(currentPage, true)
                                }
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_arrow_left_24),
                                contentDescription = "Pagina precedente"
                            )
                        }
                        IconButton(onClick = {
                            pdfView?.let {
                                currentPage++
                                it.jumpTo(currentPage, true)
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_arrow_right_24),
                                contentDescription = "Pagina successiva"
                            )
                        }
                    }

                    // Pulsante per attivare/disattivare la modalità Disegno
                    FloatingActionButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(20.dp),
                        onClick = {
                            isDrawingMode = !isDrawingMode
                        }
                    ) {
                        val painter =
                            if (isDrawingMode) {
                                painterResource(id = R.drawable.baseline_visibility_24)
                            } else {
                                painterResource(id = R.drawable.baseline_draw_24)
                            }
                        Icon(
                            painter = painter,
                            contentDescription = if (isDrawingMode) "Modalità Visualizzazione" else "Modalità Disegno"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IconaDropdownAnimata(expanded: Boolean) {
    // Angolo di rotazione animato: 0° se chiuso, 180° se aperto
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "Rotazione icona"
    )

    Icon(
        imageVector = Icons.Default.ArrowDropDown,
        contentDescription = "Freccia menu",
        tint = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier
            .size(24.dp)
            .rotate(rotation)
    )
}


@Composable
fun PdfEditorScreen(
    context: Context,
    pdfPath: String,
    numeroPdf: String,
    nomePdf: String,
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    var isEraser by remember { mutableStateOf(false) }
    var isNavigationMode by remember { mutableStateOf(false) }
    var isPublic by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    var strokeWidth by remember { mutableStateOf(2f) }
    val lines = remember { mutableStateListOf<Line>() }
    var currentColor by remember { mutableStateOf(Color.Black) }
    val canvasWidth by remember { mutableStateOf(0) }
    val canvasHeight by remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

    val state = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offset += panChange
    }

    val pdfBitmap = remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(pdfPath) {
        val file = File(pdfPath)
        if (file.exists()) {
            val pdfRenderer =
                PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
            val page = pdfRenderer.openPage(0)

            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            pdfBitmap.value = bitmap.asImageBitmap()

            page.close()
            pdfRenderer.close()
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .drawWithContent {
            drawContent() // Disegna il contenuto esistente (es. immagine)

            // Disegna tutte le linee sopra l'immagine
            lines.forEach { line ->
                drawLine(
                    color = line.color,
                    start = line.start * scale + offset,
                    end = line.end * scale + offset,
                    strokeWidth = line.strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        }
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                val currentPosition = (change.position - offset) / scale
                if (isEraser) {
                    lines.removeAll { line -> isPointNearLine(currentPosition, line) }
                } else {
                    lines.add(
                        Line(
                            start = currentPosition - dragAmount / scale,
                            end = currentPosition,
                            color = if (isEraser or isNavigationMode) Color.Transparent else currentColor,
                            strokeWidth = strokeWidth
                        )
                    )
                }
            }
        }
    ) {

        // 🔹 Toolbar
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .background(Color.Gray.copy(alpha = 0.7f), shape = RoundedCornerShape(16.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(Color.Black, Color.Blue, Color.Green, Color.Red).forEach { color ->
                ColorButton(onClick = {
                    currentColor = color; isEraser = false; isNavigationMode = false
                }, color = color)
            }

            VerticalDivider(modifier = Modifier.height(15.dp))

            IconButton(onClick = { expanded = !expanded }) {
                Icon(painterResource(R.drawable.thickness), "Stroke width")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf(1f, 2f, 5f).forEach {
                    DropdownMenuItem(
                        text = {
                            it.toInt()
                            Text("$it mm")
                        },
                        onClick = { strokeWidth = it; expanded = false }
                    )
                }
            }

            VerticalDivider(modifier = Modifier.height(15.dp))

            IconButton(onClick = { isNavigationMode = !isNavigationMode; isEraser = false }) {
                Icon(
                    painter = painterResource(R.drawable.drag_pan_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                    contentDescription = "Drag and pan",
                    tint = if (isNavigationMode) Color.Gray else Color.Black
                )
            }
            IconButton(onClick = { isEraser = !isEraser; isNavigationMode = false }) {
                Icon(
                    painter = painterResource(R.drawable.ink_eraser),
                    contentDescription = "Eraser",
                    tint = if (isEraser) Color.Gray else Color.Black
                )
            }
            IconButton(onClick = { lines.clear() }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_delete_24),
                    contentDescription = "Clear all"
                )
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
            onClick = { showDialog = true }
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_save_24),
                contentDescription = "Save"
            )
        }

        // 🔹 Dialog per il salvataggio
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Salvataggio PDF") },
                text = {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Salva il file per tutti")
                            Checkbox(checked = isPublic, onCheckedChange = { isPublic = it })
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        coroutineScope.launch {
                            savePdfToCacheAndUpload(
                                context,
                                pdfPath,
                                lines,
                                isPublic,
                                numeroPdf,
                                nomePdf,
                                canvasWidth,
                                canvasHeight
                            )
                        }
                    }) {
                        Text("Salva")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Annulla")
                    }
                }
            )
        }

        pdfBitmap.value?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
                    .transformable(state)
            )
        }
    }
}

@Composable
fun PdfEditor(
    pdfPath: String
) {
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(pdfPath) {
        // Assicurati che il link sia un URL valido
        val uri = Uri.parse(pdfPath)

        val config = ViewerConfig.Builder()
            .openUrlCachePath(context.cacheDir.absolutePath)
            .build()

        activity?.let {
            DocumentActivity.openDocument(it, uri, config)
        }
    }
}

suspend fun savePdfToCacheAndUpload(
    context: Context,
    pdfPath: String,
    lines: List<Line>,
    isPublic: Boolean,
    numeroPdf: String,
    nomePdf: String,
    canvasWidth: Int,
    canvasHeight: Int,
) {
    val tempFile = downloadPdfFromFirebase(pdfPath, context) ?: return

    val cacheFile = withContext(Dispatchers.IO) {
        File.createTempFile("Cantico_${numeroPdf}_temp", ".pdf", context.cacheDir)
    }

    withContext(Dispatchers.IO) {
        var document: PDDocument? = null
        var contentStream: PDPageContentStream? = null
        try {
            document = PDDocument.load(tempFile)
            val page = document.getPage(0)

            contentStream = PDPageContentStream(
                document,
                page,
                PDPageContentStream.AppendMode.APPEND,
                true,
                true
            )

            val pdfWidth = page.cropBox.width
            val pdfHeight = page.cropBox.height

            lines.forEach { line ->
                val startX = (line.start.x / canvasWidth) * pdfWidth
                val startY =
                    pdfHeight - ((line.start.y / canvasHeight) * pdfHeight)  // Coordinate invertite
                val endX = (line.end.x / canvasWidth) * pdfWidth
                val endY = pdfHeight - ((line.end.y / canvasHeight) * pdfHeight)

                contentStream.setStrokingColor(
                    (line.color.red * 255).toInt(),
                    (line.color.green * 255).toInt(),
                    (line.color.blue * 255).toInt()
                )
                contentStream.setLineWidth(line.strokeWidth)

                contentStream.moveTo(startX, startY)
                contentStream.lineTo(endX, endY)
                contentStream.stroke()
            }

            contentStream.close()
            document.save(cacheFile)

            val uri = Uri.fromFile(cacheFile)
            uploadPdfToFirebase(isPublic, numeroPdf, context, uri, nomePdf)

        } catch (e: IOException) {
            Log.e("PDF_Error", "Errore nel salvataggio del PDF: ${e.message}", e)
        } finally {
            contentStream?.close()
            document?.close()
        }
    }
}

suspend fun downloadPdfFromFirebase(url: String, context: Context): File? {
    return withContext(Dispatchers.IO) {
        var urlConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            urlConnection = URL(url).openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.doOutput = false
            urlConnection.connectTimeout = 10000  // Timeout di connessione
            urlConnection.readTimeout = 10000

            if (urlConnection.responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("DownloadPDF", "Errore nel download: ${urlConnection.responseMessage}")
                return@withContext null
            }

            val cacheFile = File.createTempFile("temp_pdf", ".pdf", context.cacheDir)
            inputStream = urlConnection.inputStream
            outputStream = FileOutputStream(cacheFile)

            inputStream.copyTo(outputStream)

            Log.d("DownloadPDF", "PDF scaricato con successo: ${cacheFile.absolutePath}")
            return@withContext cacheFile

        } catch (e: Exception) {
            Log.e("DownloadPDF", "Errore durante il download", e)
            return@withContext null

        } finally {
            try {
                inputStream?.close()
                outputStream?.close()
                urlConnection?.disconnect()
            } catch (e: IOException) {
                Log.e("DownloadPDF", "Errore nella chiusura dello stream", e)
            }
        }
    }
}

fun isPointNearLine(point: Offset, line: Line, threshold: Float = 20f): Boolean {
    val dist = distanceFromPointToSegment(point, line.start, line.end)
    return dist < threshold
}

// Calcola la distanza minima tra un punto e un segmento di linea
fun distanceFromPointToSegment(p: Offset, a: Offset, b: Offset): Float {
    val ab = b - a
    val ap = p - a
    val bp = p - b

    val ab2 = ab.x * ab.x + ab.y * ab.y
    val ap_ab = ap.x * ab.x + ap.y * ab.y
    val t = (ap_ab / ab2).coerceIn(0f, 1f)

    val closest = Offset(a.x + t * ab.x, a.y + t * ab.y)
    return (p - closest).getDistance()
}

// Funzione per ottenere il path della cartella in Firebase
fun getPdfStoragePath(isPublic: Boolean, nomeCurrentUser: String, numeroPdf: String): String {
    return if (isPublic) {
        if (numeroPdf.toInt() <= 700) "SpartitiInni"
        else "Spartiti"
    } else {
        "$nomeCurrentUser/Cantico $numeroPdf/"
    }
}

suspend fun uploadPdfToFirebase(
    isPublic: Boolean,
    numeroPdf: String,
    context: Context,
    uri: Uri,
    nomePdf: String,
) {

    val nomeCurrentUser = FirebaseAuth.getInstance().currentUser?.displayName ?: "Anonimo"
    val storagePath = getPdfStoragePath(isPublic, nomeCurrentUser, numeroPdf)
    val storageRef = FirebaseStorage.getInstance().reference.child(storagePath)

    try {
        // Carica il file su Firebase Storage
        val uploadTask = storageRef.putFile(uri).await()
        val downloadUri = storageRef.downloadUrl.await()

        // Aggiungi l'URL al Firestore
        addPdfUrlToFirestore(nomePdf, numeroPdf, downloadUri.toString(), isPublic, nomeCurrentUser)
        showToast(
            context,
            "PDF Caricato con successo!"
        ) // Mostra lo Snackbar

        Log.d("UploadPDF", "PDF caricato con successo: $downloadUri")
    } catch (e: Exception) {
        Log.e("UploadPDF", "Errore durante il caricamento", e)
    }
}

suspend fun addPdfUrlToFirestore(
    nomePdf: String,
    numeroPdf: String,
    pdfUrl: String,
    isPublic: Boolean,
    nomeCurrentUser: String,
) {
    val user = FirebaseAuth.getInstance().currentUser
    Log.d("ProvaNomePDF", "Nome pdf: $nomePdf")
    val db = FirebaseFirestore.getInstance()

    val path = if (numeroPdf.toInt() <= 700) "SpartitiInni" else "Spartiti"
    val collectionPath = if (isPublic) {
        path // "SpartitiInni" o "Spartiti"
    } else {
        "Users/$nomeCurrentUser/$path" // Percorso per gli utenti privati
    }

    try {
        if (!isPublic) {
            // 🔹 Assicuriamoci che il documento utente esista
            val userDocRef = db.collection("Users").document(nomeCurrentUser)
            val userDocSnapshot = userDocRef.get().await()

            if (!userDocSnapshot.exists()) {
                // Creiamo il documento con un campo "nome"
                userDocRef.set(
                    mapOf(
                        "nome" to nomeCurrentUser,
                        "uid" to (user?.uid ?: "Anonimo"),
                        "email" to (user?.email ?: "Anonimo")
                    )
                ).await()
                Log.d("Firestore", "Documento utente creato: $nomeCurrentUser")
            }

            // 🔹 Ora possiamo salvare il PDF nella sottocollezione
            val pdfData = mapOf(
                "url" to pdfUrl,
                "nome" to nomePdf,
                "numero" to numeroPdf
            )

            val documentRef = db.collection(collectionPath).document("Cantico $numeroPdf")
            val documentSnapshot = documentRef.get().await()

            if (documentSnapshot.exists()) {
                Log.d("Firestore", "Il documento esiste già: Cantico $numeroPdf")
            } else {
                documentRef.set(pdfData).await()
                Log.d("Firestore", "PDF URL aggiunto con successo")
            }
        } else {
            // 🔹 Se il PDF è pubblico, aggiorniamo semplicemente il documento
            val documentRef = db.collection(collectionPath).document("Cantico $numeroPdf")

            val data = mapOf(
                "url" to pdfUrl // URL del nuovo PDF caricato su Firebase Storage
            )

            documentRef.set(data, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("Firestore", "PDF aggiornato correttamente nel database")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Errore durante l'aggiornamento del PDF", e)
                }
        }
    } catch (e: Exception) {
        Log.e("Firestore", "Errore durante l'aggiunta del PDF", e)
    }
}

@Composable
fun ColorButton(color: Color, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(color, CircleShape)
        )
    }
}

@Composable
fun MenuPage() {
    Column(
        modifier = Modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Bottom
    ) {
        listOf(
            Pair(R.drawable.g8, "Inni di Lode / Aggiunta"),
            Pair(R.drawable.g9, "Evangelizzazioni"),
            Pair(R.drawable.g10, "Culto dei giovani")
        ).forEach { (iconRes, title) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(48.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun AdaptiveLayoutSpartiti(
    navController: NavHostController,
    navView: NavigationViewModel,
    url: String,
    visible: Boolean,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    if (screenWidth > 1200.dp) {
        // Schermi grandi (tablet, desktop)
        NavigationRailLayoutSpartiti(navController, navView, url, content)
    } else {
        // Schermi piccoli (smartphone)
        NavigationBarLayoutSpartiti(navController, navView, url, visible, content)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationRailLayoutSpartiti(
    navController: NavHostController,
    navView: NavigationViewModel,
    url: String,
    content: @Composable () -> Unit,
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = "MusicADI Colle",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Left,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("mainscreen")
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Menu"
                        )
                    }
                },
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.disegno),
                        contentDescription = "Icona principale",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    IconButton(onClick = { navController.navigate("choosescreen") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_launch_24),
                            contentDescription = "Passa al choosescreen",
                            modifier = Modifier
                                .size(32.dp)
                                .fillMaxSize()
                        )
                    }
                },
            )
        },
        content = { innerPadding ->
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    modifier = Modifier.zIndex(1f),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    val currentBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = currentBackStackEntry?.destination

                    NavigationRailItem(
                        selected = currentDestination?.route == "",
                        onClick = { navController.navigate("") },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "") },
                        label = { Text("") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationRailItem(
                        selected = currentDestination?.route == "mainscreen",
                        onClick = { navController.navigate("mainscreen") },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    NavigationRailItem(
                        selected = currentDestination?.route == "elenco",
                        onClick = { navController.navigate("elenco") },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.ic_elenco),
                                contentDescription = "Prove"
                            )
                        },
                        label = { Text("Elenco") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    NavigationRailItem(
                        selected = currentDestination?.route == "spartiti",
                        onClick = { navController.navigate("spartiti") },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.ic_music_note),
                                contentDescription = "Prove"
                            )
                        },
                        label = { Text("Spartiti") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    NavigationRailItem(
                        selected = currentDestination?.route == "testo",
                        onClick = { navController.navigate("testo") },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.ic_testo),
                                contentDescription = "Prove"
                            )
                        },
                        label = { Text("Testi") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    NavigationRailItem(
                        selected = currentDestination?.route == "servizi",
                        onClick = { navController.navigate("servizi") },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.baseline_star_24),
                                contentDescription = "Prove"
                            )
                        },
                        label = { Text("Servizi") }
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(innerPadding)
                ) {
                    content()
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBarLayoutSpartiti(
    navController: NavHostController,
    navView: NavigationViewModel,
    url: String,
    visible: Boolean,
    content: @Composable () -> Unit,
) {
    val items = listOf("Elenco", "Spartiti", "Home", "Testo", "Servizi")

    var selectedItem by remember { mutableIntStateOf(5) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = "MusicADI Colle",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Left,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("mainscreen") }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Home"
                        )
                    }
                },
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.disegno),
                        contentDescription = "Icona principale",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    IconButton(onClick = { navController.navigate("choosescreen") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_launch_24),
                            contentDescription = "Passa al choosescreen",
                            modifier = Modifier
                                .size(32.dp)
                                .fillMaxSize()
                        )
                    }
                },
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(),
                exit = slideOutVertically(),
                modifier = Modifier
                    .zIndex(3f)
            ) {
                NavigationBar(
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp, 32.dp, 0.dp, 0.dp))
                        .animateContentSize()
                        .background(Color.Transparent)
                        .wrapContentSize(),
                ) {
                    items.forEachIndexed { index, item ->
                        val icon: Painter = when (index) {
                            0 -> painterResource(id = R.drawable.ic_elenco)
                            1 -> painterResource(id = R.drawable.ic_music_note)
                            2 -> painterResource(id = R.drawable.baseline_home_24)
                            3 -> painterResource(id = R.drawable.ic_testo)
                            4 -> painterResource(id = R.drawable.baseline_star_24)
                            else -> painterResource(id = R.drawable.baseline_settings_24)
                        }
                        NavigationBarItem(
                            icon = { Icon(painter = icon, contentDescription = null) },
                            label = { Text(item) },
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                                // Esegui l'azione di navigazione corrispondente qui
                                // Ad esempio:
                                when (index) {
                                    0 -> navController.navigate("elenco")
                                    1 -> navController.navigate("spartiti")
                                    2 -> navController.navigate("mainscreen")
                                    3 -> navController.navigate("testo")
                                    4 -> navController.navigate("servizi")
                                }
                            }
                        )
                    }
                }
            }
        },
        content =
        { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                content()
            }
        }
    )
}

fun showToast(context: Context, message: String) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}