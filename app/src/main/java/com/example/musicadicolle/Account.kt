package com.example.musicadicolle

import com.google.firebase.auth.FirebaseUser

data class Account(
    var name: String = "",
    var email: String = "",
    var idToken: String = "",
    var photoUrl: String = "",
    var account: FirebaseUser? = null
) {
    companion object {
        fun fromFirebaseUser(user: FirebaseUser?): Account? {
            return if (user != null) {
                Account(
                    name = user.displayName ?: "",
                    email = user.email ?: "",
                    idToken = user.uid, // Puoi usare l'UID come idToken se necessario
                    photoUrl = user.photoUrl?.toString() ?: ""
                )
            } else {
                null
            }
        }
    }
}

