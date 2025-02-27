package com.shinjaehun.winternotesfirebase.model

import android.net.Uri
import com.shinjaehun.winternotesfirebase.common.Result
import com.shinjaehun.winternotesfirebase.common.UiState

interface INoteRepository {
    suspend fun getNotes(): Result<Exception, List<Note>>
    suspend fun getNoteById(noteId: String): Result<Exception, Note>
    suspend fun deleteNote(note: Note): Result<Exception, Unit>
    suspend fun insertOrUpdateNote(note: Note, imageUri: Uri?): Result<Exception, Unit>
//    suspend fun searchNote(keyword: String): Result<Exception, List<Note>>
//    suspend fun uploadImageFile(fileUri: Uri, onResult: (UiState<Uri>) -> Unit)
}