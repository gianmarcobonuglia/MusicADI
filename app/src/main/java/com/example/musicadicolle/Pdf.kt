package com.example.musicadicolle

import java.sql.Timestamp

data class Pdf(
    val id: String = "",
    val nome: String = "",
    val numero: String = "",
    val pdfurl: String = "",
    val tipoPdf: String = "",
    val userId: String = "",
    var isFavorite: Boolean = false
) {
    // Costruttore vuoto richiesto da Firebase per la deserializzazione degli oggetti
    constructor() : this("", "", "","", "", "")
}

data class CalendarEvent(
    val titolo: String = "",
    val data: String = "",
    val tipo: String = "",
    val allDay: Boolean = false,
    val email: String = ""
)

data class News(
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val timestamp: com.google.firebase.Timestamp? = null
)