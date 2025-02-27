package com.shinjaehun.winternotesfirebase.view.notelist

import androidx.recyclerview.widget.DiffUtil
import com.shinjaehun.winternotesfirebase.model.Note

class NoteDiffUtilCallback: DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.dateTime == newItem.dateTime
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.dateTime == newItem.dateTime
    }
}