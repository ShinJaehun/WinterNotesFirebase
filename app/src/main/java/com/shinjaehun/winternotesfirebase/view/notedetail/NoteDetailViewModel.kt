package com.shinjaehun.winternotesfirebase.view.notedetail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shinjaehun.winternotesfirebase.common.BaseViewModel
import com.shinjaehun.winternotesfirebase.common.GET_NOTE_ERROR
import com.shinjaehun.winternotesfirebase.common.Result
import com.shinjaehun.winternotesfirebase.common.UiState
import com.shinjaehun.winternotesfirebase.common.currentTime
import com.shinjaehun.winternotesfirebase.model.INoteRepository
import com.shinjaehun.winternotesfirebase.model.Note
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val TAG = "NoteDetailViewModel"

class NoteDetailViewModel(
    val noteRepo: INoteRepository,
    uiContext: CoroutineContext
): BaseViewModel<NoteDetailEvent>(uiContext) {

    private val noteState = MutableLiveData<Note>()
    val note: LiveData<Note> get() = noteState

    private val noteImageState = MutableLiveData<Uri?>()
    val noteImage: LiveData<Uri?> get() = noteImageState

    private val noteImageDeletedState = MutableLiveData<Boolean>()
    val noteImageDeleted: LiveData<Boolean> get() = noteImageDeletedState

    private val noteColorState = MutableLiveData<String?>()
    val noteColor: LiveData<String?> get() = noteColorState

    private val webLinkState = MutableLiveData<String?>()
    val webLink: LiveData<String?> get() = webLinkState

    private val webLinkDeletedState = MutableLiveData<Boolean>()
    val webLinkDeleted: LiveData<Boolean> get() = webLinkDeletedState

    private val deletedState = MutableLiveData<Boolean>()
    val deleted: LiveData<Boolean> get() = deletedState

    private val updatedState = MutableLiveData<Boolean>()
    val updated: LiveData<Boolean> get() = updatedState

    override fun handleEvent(event: NoteDetailEvent) {
        when(event) {
            is NoteDetailEvent.OnStart -> getNote(event.noteId)
            is NoteDetailEvent.OnDoneClick -> updateNote(event.note, event.imageUri)
            is NoteDetailEvent.OnNoteImageChange -> changeNoteImage(event.imageUri)
            NoteDetailEvent.OnNoteImageDeleteClick -> deleteNoteImage()
            is NoteDetailEvent.OnNoteColorChange -> changeNoteColor(event.color)
            is NoteDetailEvent.OnWebLinkChange -> changeWebLink(event.webLink)
            NoteDetailEvent.OnWebLinkDeleteClick -> deleteWebLink()
            NoteDetailEvent.OnDeleteClick -> deleteNote()
        }
    }

    private fun getNote(noteId: String) = launch {
        if (noteId == "") {
            newNote()
        } else {
            val noteResult = noteRepo.getNoteById(noteId)
            when(noteResult) {
                is Result.Value -> noteState.value = noteResult.value
                is Result.Error -> errorState.value = GET_NOTE_ERROR
            }
        }
    }

    private fun newNote() {
        noteState.value =
            Note("", "", currentTime(), null, null, null, null)
    }

    private fun updateNote(upNote: Note, imageUri: Uri?) = launch {
        Log.i(TAG, "update note~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
         val updateResult = noteRepo.insertOrUpdateNote(
            note.value!!.copy(
                title = upNote.title,
                contents = upNote.contents,
//                dateTime = currentTime(), // 이게 FB의 PK가 dateTime이어서...
                imagePath = upNote.imagePath,
                color = upNote.color,
                webLink = upNote.webLink,
            ),
            imageUri
        )
        when(updateResult) {
            is Result.Value -> updatedState.value = true
            is Result.Error -> updatedState.value = false
        }
    }

    private fun changeNoteImage(imageUri: Uri) {
        noteImageState.value = imageUri
    }

    private fun deleteNoteImage() {
        noteImageDeletedState.value = true
    }

    private fun changeNoteColor(color: String?) {
        noteColorState.value = color
    }

    private fun changeWebLink(webLink: String?) {
        webLinkState.value = webLink
    }

    private fun deleteWebLink() {
        webLinkDeletedState.value = true
    }

    private fun deleteNote() = launch {
        val deletedResult = noteRepo.deleteNote(note.value!!)
        when (deletedResult) {
            is Result.Value -> deletedState.value = true
            is Result.Error -> deletedState.value = false
        }
    }

//    fun onUploadImageFile(fileUris: Uri, onResult: (UiState<Uri>) -> Unit) {
//        onResult.invoke(UiState.Loading)
//        viewModelScope.launch {
//            noteRepo.uploadImageFile(fileUris, onResult)
//        }
//    }
}