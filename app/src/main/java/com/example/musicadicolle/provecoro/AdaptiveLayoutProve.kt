package com.example.musicadicolle.provecoro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.musicadicolle.NavigationViewModel
import com.example.musicadicolle.R

@Composable
fun AdaptiveLayoutProve(
    navController: NavHostController,
    navView: NavigationViewModel,
    url: String,
    content: @Composable () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    if (screenWidth > 1200.dp) {
        // Schermi grandi (tablet, desktop)
        NavigationRailLayoutProve(navController, content, navView)
    } else {
        // Schermi piccoli (smartphone)
        NavigationBarLayoutProve(navController, content, navView)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationRailLayoutProve(
    navController: NavHostController,
    content: @Composable () -> Unit,
    navView: NavigationViewModel,
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
                    Image(painter = painterResource(id = R.drawable.disegno_prove), contentDescription = "Icona principale", contentScale = ContentScale.Fit, modifier = Modifier.size(48.dp))
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
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationRailItem(
                        selected = currentDestination?.route == "elencoProve",
                        onClick = { navController.navigate("elencoProve") },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.ic_elenco),
                                contentDescription = "Elenco"
                            )
                        },
                        label = { Text("Elenco") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationRailItem(
                        selected = currentDestination?.route == "spartitiProve",
                        onClick = { navController.navigate("spartitiProve") },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.ic_music_note),
                                contentDescription = "Spartiti"
                            )
                        },
                        label = { Text("Spartiti") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationRailItem(
                        selected = currentDestination?.route == "testoProve",
                        onClick = { navController.navigate("testoProve") },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.ic_testo),
                                contentDescription = "Testi"
                            )
                        },
                        label = { Text("Testi") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationRailItem(
                        selected = currentDestination?.route == "serviziProve",
                        onClick = { navController.navigate("serviziProve") },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.baseline_star_24),
                                contentDescription = "Servizi"
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
fun NavigationBarLayoutProve(
    navController: NavHostController,
    content: @Composable () -> Unit,
    navView: NavigationViewModel,
) {
    val items = listOf("Elenco", "Spartiti", "Home", "Testo", "Servizi")

    var selectedItem by remember { mutableIntStateOf(5) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
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
                    Image(painter = painterResource(id = R.drawable.disegno_prove), contentDescription = "Icona principale", contentScale = ContentScale.Fit, modifier = Modifier.size(48.dp))
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
            NavigationBar (
                modifier = Modifier.clip(RoundedCornerShape(50.dp, 50.dp, 0.dp, 0.dp)),
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

