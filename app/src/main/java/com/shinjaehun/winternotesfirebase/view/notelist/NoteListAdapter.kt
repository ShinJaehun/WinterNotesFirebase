package com.shinjaehun.winternotesfirebase.view.notelist

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.size.Scale
import com.shinjaehun.winternotesfirebase.common.ColorBLACK
import com.shinjaehun.winternotesfirebase.common.simpleDate
import com.shinjaehun.winternotesfirebase.databinding.ItemContainerNoteBinding
import com.shinjaehun.winternotesfirebase.model.Note

class NoteListAdapter(
    val event: MutableLiveData<NoteListEvent> = MutableLiveData()
): ListAdapter<Note, NoteListAdapter.NoteViewHolder>(NoteDiffUtilCallback()) {

    inner class NoteViewHolder(val binding: ItemContainerNoteBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NoteViewHolder(
            ItemContainerNoteBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        getItem(position).let { note ->
            with(holder) {
                binding.tvTitle.text = note.title
                binding.tvDateTime.text = simpleDate(note.dateTime)

                if (note.imagePath.isNullOrEmpty()) {
                    binding.rivImagePreview.visibility = View.GONE
                } else {
                    binding.rivImagePreview.visibility = View.VISIBLE
                    binding.rivImagePreview.load(note.imagePath) {
                        scale(Scale.FIT)
                    }
                }

                val gradientDrawable = binding.layoutNote.background as GradientDrawable
                if (note.color.isNullOrEmpty()) {
                    gradientDrawable.setColor(Color.parseColor(ColorBLACK))
                } else {
                    gradientDrawable.setColor(Color.parseColor(note.color))
                }

                binding.layoutNote.setOnClickListener {
                    event.value = NoteListEvent.OnNoteItemClick(position)
                }
            }
        }
    }


}