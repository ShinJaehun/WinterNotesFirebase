package com.shinjaehun.winternotesfirebase.view.notelist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.FirebaseApp
import com.shinjaehun.winternotesfirebase.model.FirebaseNoteRepoImpl
import com.shinjaehun.winternotesfirebase.model.INoteRepository

class NoteListInjector(application: Application): AndroidViewModel(application) {
    private fun getNoteRepository(): INoteRepository {
        FirebaseApp.initializeApp(getApplication())
        return FirebaseNoteRepoImpl()
    }

    fun provideNoteListViewModelFactory(): NoteListViewModelFactory =
        NoteListViewModelFactory(getNoteRepository())
}