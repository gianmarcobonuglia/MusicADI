@file:OptIn(ExperimentalPagerApi::class)

package com.example.musicadicolle.provecoro

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.painter.Painter
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

val path = Path()

@Composable
fun SpartitiScreenProve(
    navController: NavHostController,
    navigationViewModel: NavigationViewModel,
) {

    val pdfUrl = remember { mutableStateOf("") }

    val errorMessage = remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState()

    var searchText by remember { mutableStateOf("") }
    var pdfList by remember { mutableStateOf<List<Pdf>>(emptyList()) }
    val tipoPdf by remember { mutableStateOf("") }

    var rowModifier by remember { mutableFloatStateOf(1f) }
    var notRowModifier by remember { mutableFloatStateOf(3f) }

    var numeroPdf by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isPdfLoaded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var isDrawingMode by remember { mutableStateOf(false) }
    var iconExpanded by remember { mutableStateOf(false) }

    var visible by remember { mutableStateOf(true) }

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
        AdaptiveLayoutSpartitiProve(
            navController = navController,
            navView = navigationViewModel,
            url = user.photoUrl.toString(),
            visible = visible,
        ) {

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
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Search,
                                keyboardType = KeyboardType.Number
                            ), //KeyboardOptions(keyboardType = KeyboardType.Number)
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

                                        val db = Firebase.firestore

                                        val path = "Coro"

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
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(start = 8.dp)
                        ) {
                            Text("Cerca cantico")
                        }
                    }
                }
            }

            Column( //horizontal pages
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
                            .height(550.dp)
                    ) {
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
                                        onValueChange = { searchText = it },
                                        label = { Text("Filtra i cantici") },
                                        shape = MaterialTheme.shapes.large
                                    )
                                }

                                // Recupera i PDF dal database Firebase
                                getPdfListFromFirebase(4) { pdf ->
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
                                            onFavoriteChange = { updatedPdf, isFavorite, tipoPdf -> },
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

                                                        Log.d(
                                                            "Firestore database",
                                                            "Raccolta: $path"
                                                        )

                                                        val querySnapshot =
                                                            db.collection("Coro")
                                                                .whereEqualTo(
                                                                    "numero",
                                                                    numeroPdf
                                                                )
                                                                .get()
                                                                .await()

                                                        for (document in querySnapshot.documents) {
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
                                            onPdfPress = { },
                                        )
                                    }
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
                            if (isPdfLoaded) {
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (iconExpanded) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FloatingActionButton(
                                onClick = { /*TODO*/ },
                                modifier = Modifier.size(40.dp),
                                containerColor = MaterialTheme.colorScheme.secondary
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_save_24),
                                    contentDescription = "Save"
                                )
                            }
                            FloatingActionButton(
                                onClick = { /*TODO*/ },
                                modifier = Modifier.size(40.dp),
                                containerColor = MaterialTheme.colorScheme.secondary
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_delete_24),
                                    contentDescription = "Discard everything"
                                )
                            }
                        }
                    }
                    FloatingActionButton(
                        modifier = Modifier
                            .zIndex(3f)
                            .align(Alignment.BottomEnd)
                            .padding(20.dp)
                            .animateContentSize(),
                        onClick = {
                            isDrawingMode = !isDrawingMode
                            iconExpanded = !iconExpanded
                        }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_draw_24),
                            contentDescription = "Modifica"
                        )
                    }
                }
            } else if (errorMessage.value.isNotEmpty()) {
                Text(text = errorMessage.value, color = MaterialTheme.colorScheme.error)
            }

            if (isDrawingMode) {
            }
        }
    }
}

@Composable
fun AdaptiveLayoutSpartitiProve(
    navController: NavHostController,
    navView: NavigationViewModel,
    url: String,
    visible: Boolean,
    content: @Composable () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    if (screenWidth > 1200.dp) {
        // Schermi grandi (tablet, desktop)
        NavigationRailLayoutSpartitiProve(navController, navView, url, content)
    } else {
        // Schermi piccoli (smartphone)
        NavigationBarLayoutSpartitiProve(navController, navView, url, visible, content)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationRailLayoutSpartitiProve(
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
                        navController.navigate("prove")
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
                        selected = currentDestination?.route == "prove",
                        onClick = { navController.navigate("prove") },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    NavigationRailItem(
                        selected = currentDestination?.route == "elencoProve",
                        onClick = { navController.navigate("elencoProve") },
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
                        selected = currentDestination?.route == "spartitiProve",
                        onClick = { navController.navigate("spartitiProve") },
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
                        selected = currentDestination?.route == "testoProve",
                        onClick = { navController.navigate("testoProve") },
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
                        selected = currentDestination?.route == "serviziProve",
                        onClick = { navController.navigate("serviziProve") },
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
fun NavigationBarLayoutSpartitiProve(
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
                    IconButton(onClick = { navController.navigate("prove") }) {
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
                                    0 -> navController.navigate("elencoProve")
                                    1 -> navController.navigate("spartitiProve")
                                    2 -> navController.navigate("prove")
                                    3 -> navController.navigate("testoProve")
                                    4 -> navController.navigate("serviziProve")
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