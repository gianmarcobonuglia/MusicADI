@file:Suppress("DEPRECATION")

package com.example.musicadicolle

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicadicolle.generale.AccountScreen
import com.example.musicadicolle.generale.AppMainScreen
import com.example.musicadicolle.generale.ElencoScreen
import com.example.musicadicolle.generale.ServiziScreen
import com.example.musicadicolle.generale.SpartitiScreen
import com.example.musicadicolle.generale.TestoScreen
import com.example.musicadicolle.provecoro.AccountScreenProve
import com.example.musicadicolle.provecoro.AppMainProveScreen
import com.example.musicadicolle.provecoro.ElencoScreenProve
import com.example.musicadicolle.provecoro.ServiziScreenProve
import com.example.musicadicolle.provecoro.SpartitiScreenProve
import com.example.musicadicolle.provecoro.TestoScreenProve
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.musicadicolle.ui1.ui1.ui.theme.AppTheme as MusicADIColleTheme

private const val TAG = "GoogleActivity"
const val RC_SIGN_IN = 9001
var errorMessage = ""

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavHostController
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dateFormat: SimpleDateFormat

    // Costanti per richieste
    private val requestBluetoothPermissions = 2
    private val requestEnableBT = 1

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        val auth = Firebase.auth

        // Funzione per attivare il Bluetooth e richiedere permessi
        fun checkAndEnableBluetooth() {
            val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

            // Controlla se il dispositivo supporta il Bluetooth
            if (bluetoothAdapter == null) {
                Log.e("Bluetooth", "Il dispositivo non supporta il Bluetooth")
                return
            }

            // Controlla se il Bluetooth è abilitato
            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, requestEnableBT)
            }

            // Richiedi i permessi necessari per il Bluetooth (da Android 12)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                        requestBluetoothPermissions
                    )
                    return
                }
            }

            // Bluetooth è abilitato e permessi concessi
            Log.d("Bluetooth", "Bluetooth attivato e permessi concessi")
        }

        navController = NavHostController(this) // Inizializza navController qui

        sharedPreferences = getSharedPreferences("song_preferences", Context.MODE_PRIVATE)
        dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        setContent {


            val context = LocalContext.current
            var dynamicColor by remember { mutableStateOf(context.getDynamicColor()) }

            MusicADIColleTheme(dynamicColor = dynamicColor) {

                checkAndEnableBluetooth()

                navController = rememberNavController() // Inizializza navController qui

                val content: @Composable () -> Unit = { }
                val context = LocalContext.current
                val navigationViewModel: NavigationViewModel = viewModel(factory = ViewModelFactory(context)) // Passa il context al ViewModel

                NavHost(navController, startDestination = "splashScreen") {

                    composable("ForgottenPassword") { ForgottenPasswordScreen(navController, navigationViewModel = navigationViewModel) }
                    composable("SignIn") { SignInScreen(navController, navigationViewModel = navigationViewModel) }
                    composable("successScreen") { SuccessScreen(navController, navigationViewModel = navigationViewModel) }

                    composable("login") { LoginScreen(navController, navigationViewModel = navigationViewModel) }
                    composable("splashScreen") { SplashScreen(navController) }

                    composable("account") { AccountScreen(navController, navigationViewModel = navigationViewModel) }
                    composable("accountProve") { AccountScreenProve(navController, navigationViewModel = navigationViewModel) }

                    composable("mainscreen") { AppMainScreen(navController, navigationViewModel = navigationViewModel)}

                    composable("elenco") { ElencoScreen(navController, navigationViewModel = navigationViewModel) }
                    composable("elencoProve") { ElencoScreenProve(navController, navigationViewModel = navigationViewModel) }

                    composable("spartiti") { SpartitiScreen(navController, navigationViewModel = navigationViewModel) }
                    composable("spartitiProve") { SpartitiScreenProve(navController, navigationViewModel = navigationViewModel) }

                    composable("testo") { TestoScreen(navController, navigationViewModel = navigationViewModel) }
                    composable("testoProve") { TestoScreenProve(navController, navigationViewModel = navigationViewModel) }

                    composable("servizi") { ServiziScreen(navController, navigationViewModel = navigationViewModel) }
                    composable("serviziProve") { ServiziScreenProve(navController, navigationViewModel = navigationViewModel) }
                    composable("settings") { SettingsScreen(navController, navigationViewModel = navigationViewModel)}

                    composable("prove") { AppMainProveScreen(navController, navigationViewModel = navigationViewModel) }
                    composable("choosescreen") { ChooseScreen(navController, navigationViewModel = navigationViewModel, content = content) }

                    composable("canticiModificati"){ CanticiModificatiScreen(navController, navigationViewModel)}
                }
            }
        }
    }

    // Questo metodo viene chiamato automaticamente quando un'intento restituisce un risultato
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

        //Bluetooth
        if (requestCode == requestEnableBT) {
            if (resultCode == RESULT_OK) {
                Log.d("Bluetooth", "Bluetooth abilitato")
            } else {
                Log.e("Bluetooth", "Bluetooth non abilitato")
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        completedTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                try {
                    val account = completedTask.getResult(ApiException::class.java)

                    // User is now signed in
                    // You can now use the account object to access the user's Google account information
                    val user = auth.currentUser
                    val Account = Account.fromFirebaseUser(user)

                    if (user != null) {
                        Account?.name = user.displayName.toString()
                        Account?.email = user.email.toString()
                        Account?.photoUrl = user.photoUrl.toString()
                        Account?.idToken = user.uid
                    }
                    Log.d(TAG, "handleSignInResult: ${user!!.uid}")
                    Log.d(TAG, "handleSignInResult: ${user.displayName}")
                    Log.d(TAG, "handleSignInResult: ${user.email}")
                    Log.d(TAG, "handleSignInResult: ${user.photoUrl}")

                    navController.navigate("mainscreen")

                } catch (e: ApiException) {
                    // Handle sign-in failure
                    Log.w(TAG, "signInResult:failed code=" + e.statusCode)
                    errorMessage = e.message.toString()
                    Log.d(TAG, "handleSignInResult: $errorMessage")
                    navController.navigate("login")
                }
            } else {
                val exception = task.exception
                Log.w("HandleSignIn", "handleSignInResult:failed", exception)
                errorMessage = exception?.message.toString()
                navController.navigate("login")
            }
        }
    }

    //Bluetooth
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestBluetoothPermissions) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Bluetooth", "Permessi Bluetooth concessi")
            } else {
                Log.e("Bluetooth", "Permessi Bluetooth negati")
            }
        }
    }

    //Dynamic Color
    fun Context.setDynamicColor(enabled: Boolean){
        getSharedPreferences("settings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("dynamic_color", enabled)
            .apply()
    }
    fun Context.getDynamicColor(): Boolean{
        return getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getBoolean("dynamic_color", true)
    }
}