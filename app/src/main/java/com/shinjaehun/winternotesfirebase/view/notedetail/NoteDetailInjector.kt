package com.shinjaehun.winternotesfirebase.view.notedetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.FirebaseApp
import com.shinjaehun.winternotesfirebase.model.FirebaseNoteRepoImpl
import com.shinjaehun.winternotesfirebase.model.INoteRepository

class NoteDetailInjector(application: Application): AndroidViewModel(application) {
    private fun getNoteRepository(): INoteRepository {
        FirebaseApp.initializeApp(getApplication())
        return FirebaseNoteRepoImpl()
    }

    fun provideNoteDetailViewModelFactory(): NoteDetailViewModelFactory =
        NoteDetailViewModelFactory(getNoteRepository())
}