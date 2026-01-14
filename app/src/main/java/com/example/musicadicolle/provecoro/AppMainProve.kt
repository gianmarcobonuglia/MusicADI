package com.example.musicadicolle.provecoro

import android.content.Intent
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.musicadicolle.Account
import com.example.musicadicolle.NavigationViewModel
import com.example.musicadicolle.News
import com.example.musicadicolle.R
import com.example.musicadicolle.generale.TypewriterTextEffect1
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun AppMainProveScreen(navController: NavHostController, navigationViewModel: NavigationViewModel) {

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

    val testo =
        listOf("il pianoforte...", "la batteria...", "le voci...", "la chitarra...", "il basso...")

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    val selectPhotoContract =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { photoUri = uri }
        }

    val context = LocalContext.current
    val intent =
        remember { Intent(Intent.ACTION_VIEW, Uri.parse("https://musicadicolle.altervista.org/")) }

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
    val screenWidth = configuration.screenWidthDp.dp

    if (user != null) {
        AdaptiveLayoutProve(
            navController = navController,
            navView = navigationViewModel,
            url = user.photoUrl.toString()
        ) {
            Column(
                modifier = Modifier
                    .padding()
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF9BBFCA),
                            Color(0xFF1893EC)
                        )
                    )),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                Box( //Intro
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(0f)
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Altezza personalizzabile
                            .graphicsLayer {
                                alpha = 0.7f // Trasparenza per il vetro
                                shadowElevation = 8.dp.toPx() // Aggiungi profondità
                            }
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.15f),
                                        Color.White.copy(alpha = 0.05f)
                                    ),
                                    start = Offset.Zero,
                                    end = Offset.Infinite
                                )
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .blur(5.dp) // Sfocatura per il vetro
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.disegno_prove),
                            contentDescription = "Prove",
                            modifier = Modifier
                                .size(180.dp),
                            alignment = Alignment.Center
                        )
                    }

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (account != null) {
                            Text(
                                text = "Benvenuto, \n${account.name}!",
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                                style = MaterialTheme.typography.displaySmall
                            )
                        } else {
                            Text(
                                text = "Benvenuto!",
                                textAlign = TextAlign.Left,
                                color = Color.Black,
                                style = MaterialTheme.typography.displaySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        TypewriterTextEffect1(
                            words = testo,
                        )
                        { displayedText ->

                            Text(
                                text = "Pensata per $displayedText",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.Black,
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
                        .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(18.dp, 18.dp, 0.dp, 0.dp)),
                ){
                    Column( //Column main
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                            .wrapContentSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        Row {
                            Text(
                                text = "Cosa canteremo",
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Icon(
                                painter = painterResource(R.drawable.baseline_star_24),
                                contentDescription = "Account"
                            )
                        }
                        Spacer(modifier = Modifier.padding(32.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.padding(32.dp))
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
                        }
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
                        Spacer(modifier = Modifier.padding(32.dp))
                        HorizontalDivider(
                            thickness = 2.dp,
                        )
                        Spacer(modifier = Modifier.height(32.dp))
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
                        value = email,
                        onValueChange = {
                            if (it.isNotEmpty()) {
                                email = it
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
                Button(
                    onClick = {
                        // Esegui azioni di conferma
                        user?.updateProfile(
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(email)
                                .setPhotoUri(photoUri)
                                .build()
                        )
                        navController.navigate("account")
                        showDialog = false
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

}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Carousel(
    items: List<Painter>, // Lista delle immagini da visualizzare nel carousel
) {
    val pagerState = rememberPagerState()

    Box {
        HorizontalPager(
            state = pagerState,
            count = items.size,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Image(
                painter = items[page],
                contentDescription = "Carousel Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(16.dp)
            )
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = Color.Gray,
            indicatorHeight = 8.dp,
            indicatorWidth = 8.dp
        )
    }
}

@Composable
fun TypewriterTextEffect2(
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
