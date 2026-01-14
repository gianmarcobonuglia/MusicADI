package com.example.musicadicolle.provecoro

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.musicadicolle.Account
import com.example.musicadicolle.IconFromUrl
import com.example.musicadicolle.NavigationViewModel
import com.example.musicadicolle.R
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AccountScreenProve(navController: NavHostController, navigationViewModel: NavigationViewModel) {

    var photoUrl by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }

    val selectPhotoContract =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { photoUri = uri }
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

    Log.d("AccountScreen", "Account: $account")
    Log.d("AccountScreen", "photoUrl: ${account?.photoUrl}")
    Log.d("AccountScreen", "Email: ${account?.email}")

    if (account == null) {
        navController.navigate("login")
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (user != null) {
            AdaptiveLayoutProve(navController = navController, navView = navigationViewModel, url = user.photoUrl.toString()) {

                if (account != null) {
                    photoUrl = account.photoUrl
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
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
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    IconFromUrl(url = photoUrl)

                    Spacer(modifier = Modifier.height(8.dp))

                    if (account != null) {
                        Text(
                            text = account.name,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        shadowElevation = 5.dp,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                    ) {
                        if (account != null) {
                            Text(
                                text = "Email: ${account.email}",
                                modifier = Modifier.padding(all = 8.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = Int.MAX_VALUE
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row() {
                        IconButton(onClick = {
                            auth.signOut()
                            navController.navigate("login")
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_logout_24),
                                contentDescription = "Logout"
                            )
                        }
                        IconButton(onClick = {
                            showDialog = true
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_edit_24),
                                contentDescription = "Modifica",
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(32.dp))

                    Row() {
                        Text(
                            text = "Impostazioni",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Icon(
                            painter = painterResource(R.drawable.baseline_settings_24),
                            contentDescription = "Account"
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Preferenze",
                                modifier = Modifier.padding(all = 8.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = Int.MAX_VALUE
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Absolute.SpaceBetween
                        ) {
                            Text(
                                text = "Culto",
                                modifier = Modifier.padding(all = 8.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = Int.MAX_VALUE
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            IconButton(
                                onClick = {
                                    navController.navigate("mainscreen")
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_launch_24),
                                    contentDescription = "Modifica",
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    HorizontalDivider(
                        thickness = 2.dp,
                    )

                    Spacer(modifier = Modifier.height(32.dp))


                    HorizontalDivider(
                        thickness = 2.dp,
                    )

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
                        navController.navigate("accountProve")
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
