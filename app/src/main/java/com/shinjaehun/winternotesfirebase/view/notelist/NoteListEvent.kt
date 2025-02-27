package com.shinjaehun.winternotesfirebase.view.notelist

sealed class NoteListEvent {
    object OnStart: NoteListEvent()
    data class OnNoteItemClick(val position: Int): NoteListEvent()
    data class OnSearchTextChange(val searchKeyword: String): NoteListEvent()
}