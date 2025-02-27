package com.shinjaehun.winternotesfirebase.view.notelist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.shinjaehun.winternotesfirebase.R
import com.shinjaehun.winternotesfirebase.common.makeToast
import com.shinjaehun.winternotesfirebase.common.toUser
import com.shinjaehun.winternotesfirebase.databinding.FragmentNoteListBinding
import com.shinjaehun.winternotesfirebase.model.User

private const val TAG = "NoteListView"

class NoteListView: Fragment() {

    private lateinit var binding: FragmentNoteListBinding
    private lateinit var viewModel: NoteListViewModel
    private lateinit var adapter: NoteListAdapter
    private lateinit var callback: OnBackPressedCallback

//    private var user: User? = null
//    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNoteListBinding.inflate(inflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.notesRecyclerView.adapter = null
    }

    override fun onStart() {
        super.onStart()

//        user = firebaseAuth.currentUser?.toUser

        viewModel = ViewModelProvider(
            this,
            NoteListInjector(requireActivity().application).provideNoteListViewModelFactory()
        ).get(NoteListViewModel::class.java)

        setUpAdapter()
        observeViewModel()

        binding.fabAddNote.setOnClickListener {
            startNoteDetailWithArgs("")
        }

        viewModel.handleEvent(NoteListEvent.OnStart)
    }

    private fun setUpAdapter() {
        adapter = NoteListAdapter()
        adapter.event.observe(
            viewLifecycleOwner,
            Observer {
                viewModel.handleEvent(it)
            }
        )
        binding.notesRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.notesRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.error.observe(
            viewLifecycleOwner,
            Observer { errorMessage ->
                showErrorState(errorMessage)
            }
        )

        viewModel.noteList.observe(
            viewLifecycleOwner,
            Observer { noteList ->
                Log.i(TAG, "count of noteList: ${noteList.size}")
                adapter.submitList(noteList)
            }
        )

        viewModel.editNote.observe(
            viewLifecycleOwner,
            Observer { noteId ->
                startNoteDetailWithArgs(noteId)
            }
        )

        viewModel.searchNoteList.observe(
            viewLifecycleOwner,
            Observer { noteList ->
                adapter.submitList(noteList)
            }
        )

    }

    private fun startNoteDetailWithArgs(noteId: String) {
        val bundle = Bundle()
        bundle.putString("noteId", noteId)
        findNavController().navigate(R.id.noteDetailView, bundle)
    }

    private fun showErrorState(errorMessage: String?) = makeToast(errorMessage!!)
}