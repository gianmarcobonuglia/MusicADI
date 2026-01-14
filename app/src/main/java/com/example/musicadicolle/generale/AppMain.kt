package com.example.musicadicolle.generale

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.musicadicolle.Account
import com.example.musicadicolle.IconFromUrl
import com.example.musicadicolle.NavigationViewModel
import com.example.musicadicolle.News
import com.example.musicadicolle.R
import com.example.musicadicolle.uploadPhotoUrl
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.random.Random

@Composable
fun AppMainScreen(
    navController: NavHostController,
    navigationViewModel: NavigationViewModel,
) {

    val newsList = remember { mutableStateListOf<News>() }

    // Recupera le news da Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("News")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val newsItem = document.toObject(News::class.java)
                    newsList.add(newsItem)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("News", "Error getting documents: ", exception)
            }
    }

    var greetings by remember { mutableStateOf("") }
    val currentDateTime = LocalDateTime.now()
    val time = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    when (time){
        in "07:00" .. "12:00" -> {
            greetings = "Buongiorno"
        }
        in "12:01" .. "18:00" -> {
            greetings = "Buon pomeriggio"
        }
        in "18:01" .. "23:59" -> {
            greetings = "Buona sera"
        }
        in "00:00" .. "06:59" -> {
            greetings = "Buona notte"
        }
    }

    val testo = listOf("il pianoforte...", "la batteria...", "le voci...", "la chitarra...", "il basso...")

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var ename by remember { mutableStateOf("") }
    val selectPhotoContract =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { photoUri = uri }
        }

    val context = LocalContext.current
    val intent =
        remember { Intent(Intent.ACTION_VIEW, "https://musicadicolle.altervista.org/".toUri()) }

    val auth = Firebase.auth

    val user = auth.currentUser
    val account = Account.fromFirebaseUser(user)
    var photoUrl by remember { mutableStateOf("") }

    if (user != null) {
        account?.name = user.displayName.toString()
        account?.email = user.email.toString()
        account?.photoUrl = user.photoUrl.toString()
        account?.idToken = user.uid
    }

    Log.d("AppMainScreen", "account: ${user?.displayName}")

    if (account == null) {
        Log.d("AppMainScreen", "account nullo")
        navController.navigate("login")
    }

    if (account != null) {
        photoUrl = account.photoUrl
    }


    val configuration = LocalConfiguration.current

    if (user != null) {
        AdaptiveLayout(
            navController = navController,
            navView = navigationViewModel,
            url = user.photoUrl.toString()
        ) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "${user.displayName}",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.titleLarge
                            )
                            HorizontalDivider()

                            Text(
                                "Impostazioni Account",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.titleMedium
                            )
                            NavigationDrawerItem(
                                label = { Text("Il Tuo Account") },
                                selected = false,
                                icon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) },
                                onClick = { navController.navigate("account") }
                            )
                            NavigationDrawerItem(
                                label = { Text("Modifica il Account") },
                                selected = false,
                                icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                                onClick = { showDialog = true }
                            )
                            NavigationDrawerItem(
                                label = { Text("Cantici Modificati") },
                                selected = false,
                                icon = { Icon(painterResource(R.drawable.baseline_draw_24), contentDescription = null) },
                                onClick = { navController.navigate("canticiModificati") }
                            )
                            NavigationDrawerItem(
                                label = { Text("Logout") },
                                selected = false,
                                icon = { Icon(painterResource(R.drawable.baseline_logout_24), contentDescription = null) },
                                onClick = {
                                    auth.signOut()
                                    navController.navigate("login")
                                }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            Text(
                                "Impostazioni Generali",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.titleMedium
                            )
                            NavigationDrawerItem(
                                label = { Text("Impostazioni dell'App") },
                                selected = false,
                                icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                                onClick = { navController.navigate("settings") }
                            )
                            NavigationDrawerItem(
                                label = { Text("Aiuto e feedback") },
                                selected = false,
                                icon = { Icon(Icons.Default.Build, contentDescription = null) },
                                onClick = { /* Handle click */ },
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                },
            ) {
                Column(
                    modifier = Modifier
                        .padding()
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {

                    Box( //Intro
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(0f)
                            .align(Alignment.CenterHorizontally)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$greetings, \n${account?.name}!",
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                style = MaterialTheme.typography.displaySmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TypewriterTextEffect1(
                                words = testo,
                            )
                            { displayedText ->

                                Text(
                                    text = "Pensata per $displayedText",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize()
                            .zIndex(1f)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(18.dp, 18.dp, 0.dp, 0.dp)
                            ),
                    ) {
                        Column(
                            //Column main
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth()
                                .wrapContentSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Row { //sezione account
                                Text(
                                    text = "Account",
                                    color = MaterialTheme.colorScheme.secondary,
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.width(15.dp))
                                Icon(
                                    painter = painterResource(R.drawable.baseline_manage_accounts_24),
                                    contentDescription = "Account"
                                )
                            } //sezione account

                            Spacer(modifier = Modifier.height(16.dp))

                            IconFromUrl(url = photoUrl)

                            Spacer(modifier = Modifier.height(16.dp))

                            Row {

                                IconButton(onClick = {
                                    scope.launch {
                                        if (drawerState.isClosed) {
                                            drawerState.open()
                                        } else {
                                            drawerState.close()
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
                            }

                            Spacer(modifier = Modifier.padding(16.dp))

                            Row {
                                Text(
                                    text = "News",
                                    color = MaterialTheme.colorScheme.secondary,
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Icon(
                                    painter = painterResource(R.drawable.baseline_newspaper_24),
                                    contentDescription = "Account"
                                )
                            } //sezione news

                            Spacer(modifier = Modifier.padding(16.dp))

                            NewsScreen(newsList = newsList)

                            Spacer(modifier = Modifier.padding(32.dp))

                            HorizontalDivider()

                            Spacer(modifier = Modifier.padding(16.dp))

                            Row {
                                Text(
                                    text = "Sito Web",
                                    color = MaterialTheme.colorScheme.secondary,
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.width(15.dp))
                                Icon(
                                    painter = painterResource(R.drawable.baseline_web),
                                    contentDescription = "Sito Web"
                                )
                            } //sezione sito web

                            Spacer(modifier = Modifier.padding(16.dp))

                            Image(
                                painter = painterResource(id = R.drawable.disegno_colle),
                                contentDescription = "Immagine sito web",
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(onClick = { context.startActivity(intent) }) {
                                Icon(
                                    painterResource(id = R.drawable.baseline_open_in_browser_24),
                                    contentDescription = "Apri nel Browser"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sito Web qui!")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "Inserisci i dati")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = ename,
                        onValueChange = {
                            if (it.isNotEmpty()) {
                                ename = it
                            }
                        },
                        label = { Text("Cambia nome!") })

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        selectPhotoContract.launch("image/*")
                    }) {
                        Text("Cambia foto profilo")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Se l'utente ha selezionato un'immagine, caricala
                    photoUri?.let { uri ->
                        uploadPhotoUrl(uri = uri, showSnackbar = showSnackbar)
                    }
                }) {
                    Text("Conferma")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Chiudi il dialogo senza confermare
                        showDialog = false
                    }) {
                    Text("Annulla")
                }
            }
        )
    }

    // Mostra Snackbar se richiesto
    if (showSnackbar) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                Button(onClick = { showSnackbar = false }) {
                    Text("OK")
                }
            }
        ) {
            Text(text = "Foto del profilo aggiornata con successo!")
        }
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun NewsScreen(newsList: List<News>) {
    val pagerState = rememberPagerState()

    Column {
        HorizontalPager(
            state = pagerState,
            count = newsList.size,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) { page ->
            NewsCard(news = newsList[page])
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun NewsCard(news: News) {

// Formattatore per il timestamp
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    // Converti il Timestamp di Firebase in Date e poi in una stringa formattata
    val formattedDate = formatter.format(news.timestamp!!.toDate())

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        Row(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(news.imageUrl),
                contentDescription = "News image",
                modifier = Modifier
                    .aspectRatio(4f / 3f)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(15.dp)
            ) {
                Text(text = news.title, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = news.description, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun TypewriterTextEffect1(
    words: List<String>,
    minDelayInMillis: Long = 10,
    maxDelayInMillis: Long = 150,
    minCharacterChunk: Int = 1,
    maxCharacterChunk: Int = 1,
    displayTextComposable: @Composable (displayedText: String) -> Unit,
) {
    // Ensure minDelayInMillis is less than or equal to maxDelayInMillis
    require(minDelayInMillis <= maxDelayInMillis) {
        "TypewriterTextEffect: Invalid delay range. minDelayInMillis ($minDelayInMillis) must be less than or equal to maxDelayInMillis ($maxDelayInMillis)."
    }

    // Ensure minCharacterChunk is less than or equal to maxCharacterChunk
    require(minCharacterChunk <= maxCharacterChunk) {
        "TypewriterTextEffect: Invalid character chunk range. minCharacterChunk ($minCharacterChunk) must be less than or equal to maxCharacterChunk ($maxCharacterChunk)."
    }

    // Initialize and remember the displayedText and the current word index
    var displayedText by remember { mutableStateOf("") }
    var wordIndex by remember { mutableIntStateOf(0) }

    // Launch the effect to update the displayedText value over time
    LaunchedEffect(Unit) {
        while (true) {
            val currentWord = words[wordIndex]
            val textLength = currentWord.length
            var endIndex = 0

            // Forward animation (typing)
            while (endIndex < textLength) {
                endIndex = minOf(
                    endIndex + Random.nextInt(minCharacterChunk, maxCharacterChunk + 1),
                    textLength
                )
                displayedText = currentWord.substring(startIndex = 0, endIndex = endIndex)
                delay(Random.nextLong(minDelayInMillis, maxDelayInMillis))
            }

            // Delay before starting backward animation
            delay(1500)

            // Backward animation (deleting)
            while (endIndex > 0) {
                endIndex -= Random.nextInt(minCharacterChunk, maxCharacterChunk + 1)
                displayedText =
                    currentWord.substring(startIndex = 0, endIndex = endIndex.coerceAtLeast(0))
                delay(Random.nextLong(minDelayInMillis, maxDelayInMillis))
            }

            // Move to the next word
            wordIndex = (wordIndex + 1) % words.size
        }
    }

    // Call the displayTextComposable with the current displayedText value
    displayTextComposable(displayedText)
}
