package com.example.musicadicolle

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class SpotifyAuthActivity(private val activity: ComponentActivity) {

    private val clientId = "06e71869a24148899d7193ae4ec480d4"
    private val redirectUri = "http://musicadicolle.altervista.org/"
    private val scopes = arrayOf("streaming")

    private var accessToken: String? = null

    private val sharedPreferences: SharedPreferences =
        activity.getSharedPreferences("SpotifyPrefs", Context.MODE_PRIVATE)

    private lateinit var loginResultLauncher: ActivityResultLauncher<Intent>

    init {
        initializeLoginResultLauncher()
        accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)
    }

    private fun initializeLoginResultLauncher() {
        loginResultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val response = AuthorizationClient.getResponse(result.resultCode, result.data)
            if (response.type == AuthorizationResponse.Type.TOKEN) {
                accessToken = response.accessToken
                saveAccessToken(accessToken)
                Log.d("SpotifyAuthActivity", "Access token: $accessToken")
                // Esegui qui la logica con l'access token ottenuto
            } else if (response.type == AuthorizationResponse.Type.ERROR) {
                Log.e("SpotifyAuthActivity", "Authorization error: ${response.error}")
                // Gestisci l'errore di autenticazione
            }
        }
    }

    private fun saveAccessToken(token: String?) {
        sharedPreferences.edit().putString("ACCESS_TOKEN", token).apply()
    }

    fun getAccessToken(): String? {
        return accessToken
    }

    fun startSpotifyLogin() {
        val request = AuthorizationRequest.Builder(
            clientId,
            AuthorizationResponse.Type.TOKEN,
            redirectUri
        ).setScopes(scopes).build()

        val intent = AuthorizationClient.createLoginActivityIntent(activity, request)
        loginResultLauncher.launch(intent)
    }
}
