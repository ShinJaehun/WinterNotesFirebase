package com.shinjaehun.winternotesfirebase.model

data class FirebaseNote (
    val title: String? = "",
    val contents: String? = "",
    val dateTime: String? = "",
    val imageUrl: String? = "",
    val color: String? = "",
    val webLink: String? = "",
    val creator: String? = ""
)