package com.shinjaehun.winternotesfirebase.view.notedetail

import android.net.Uri
import com.shinjaehun.winternotesfirebase.model.Note

sealed class NoteDetailEvent {
    data class OnStart(val noteId: String): NoteDetailEvent()
    data class OnDoneClick(val note: Note, val imageUri: Uri?): NoteDetailEvent()
    data class OnNoteImageChange(val imageUri: Uri): NoteDetailEvent()
    object OnNoteImageDeleteClick: NoteDetailEvent()
    data class OnNoteColorChange(val color: String?): NoteDetailEvent()
    data class OnWebLinkChange(val webLink: String?): NoteDetailEvent()
    object OnWebLinkDeleteClick: NoteDetailEvent()
    object OnDeleteClick: NoteDetailEvent()
}