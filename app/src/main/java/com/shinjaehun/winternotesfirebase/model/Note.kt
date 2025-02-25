package com.shinjaehun.winternotesfirebase.model

data class Note(
    val title: String,
    val contents: String?,
    val dateTime: String,
    val imagePath: String?,
//    val imageUri: Uri?,
    val color: String?,
    val webLink: String?,
    val creator: User?
)
