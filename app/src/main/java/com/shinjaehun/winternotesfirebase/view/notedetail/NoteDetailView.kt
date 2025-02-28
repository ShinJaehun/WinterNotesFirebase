package com.shinjaehun.winternotesfirebase.view.notedetail

import android.content.Context
import android.net.Uri
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil3.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.storage.FirebaseStorage
import com.shinjaehun.winternotesfirebase.R
import com.shinjaehun.winternotesfirebase.common.ColorBLACK
import com.shinjaehun.winternotesfirebase.common.ColorDARKBLUE
import com.shinjaehun.winternotesfirebase.common.ColorLIGHTBLUE
import com.shinjaehun.winternotesfirebase.common.ColorPINK
import com.shinjaehun.winternotesfirebase.common.ColorYELLOW
import com.shinjaehun.winternotesfirebase.common.UiState
import com.shinjaehun.winternotesfirebase.common.currentTime
import com.shinjaehun.winternotesfirebase.common.makeToast
import com.shinjaehun.winternotesfirebase.common.toEditable
import com.shinjaehun.winternotesfirebase.databinding.FragmentNoteDetailBinding
import com.shinjaehun.winternotesfirebase.model.Note
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "NoteDetailView"

class NoteDetailView: Fragment() {

    private lateinit var binding: FragmentNoteDetailBinding
    private lateinit var viewModel: NoteDetailViewModel
    private lateinit var callback: OnBackPressedCallback

//    private var note: Note? = null
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private var uploadImageUri: Uri? = null
    private var uploadImageUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNoteDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.noteListView)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun onStart() {
        super.onStart()

        viewModel = ViewModelProvider(
            this,
            NoteDetailInjector(requireActivity().application).provideNoteDetailViewModelFactory()
        ).get(NoteDetailViewModel::class.java)

        observeViewModel()
        initMisc()

        viewModel.handleEvent(
            NoteDetailEvent.OnStart(arguments?.getString("noteId").toString())
        )

        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.noteListView)
        }

        binding.ivSave.setOnClickListener {
            val title = binding.etNoteTitle.text.toString()
            val contents = binding.etNoteContent.text.toString()
            val gradientDrawable = binding.viewSubtitleIndicator.background as GradientDrawable
            val colorCode = String.format("#%06X", (0xFFFFFF and gradientDrawable.color!!.defaultColor))
            val webUrl = binding.tvWebUrl.text.toString()
            Log.i(TAG, "webUrl: $webUrl")

            if (binding.ivNote.visibility == View.GONE) {
                uploadImageUrl = null
            }

            viewModel.handleEvent(
                NoteDetailEvent.OnDoneClick(
                    Note(
                        title = title,
                        contents = contents,
                        dateTime = currentTime(),
                        imagePath = uploadImageUrl,
                        color = colorCode,
                        webLink = webUrl,
                        creator = null
                    ),
                    imageUri = uploadImageUri
                )
            )
        }
    }

    private fun observeViewModel() {
        viewModel.error.observe(
            viewLifecycleOwner,
            Observer { errorMessage ->
                showErrorState(errorMessage)
            }
        )

        viewModel.note.observe(
            viewLifecycleOwner,
            Observer {
                binding.etNoteTitle.text = it.title.toEditable()
                binding.tvDateTime.text = it.dateTime

                if(!it.contents.isNullOrEmpty()) {
                    binding.etNoteContent.text = it.contents.toEditable()
                } else {
                    binding.etNoteContent.text = "".toEditable()
                }

                if (!it.color.isNullOrEmpty()){
                    when(it.color){
                        ColorPINK -> binding.misc.layoutMisc.findViewById<ImageView>(R.id.iv_color2).performClick()
                        ColorDARKBLUE -> binding.misc.layoutMisc.findViewById<ImageView>(R.id.iv_color3).performClick()
                        ColorYELLOW -> binding.misc.layoutMisc.findViewById<ImageView>(R.id.iv_color4).performClick()
                        ColorLIGHTBLUE -> binding.misc.layoutMisc.findViewById<ImageView>(R.id.iv_color5).performClick()
                        else -> binding.misc.layoutMisc.findViewById<ImageView>(R.id.iv_color1).performClick()
                    }
                    setTitleIndicatorColor(it.color)
                } else {
                    setTitleIndicatorColor(ColorBLACK)
                }

                if (!it.imagePath.isNullOrEmpty()) {
                    showImageFromUrl(it.imagePath)
                }

                if(!it.webLink.isNullOrEmpty()) {
                    showWebLink(it.webLink)
                }

                binding.misc.layoutDeleteNote.visibility = View.VISIBLE
            }
        )

        viewModel.noteColor.observe(
            viewLifecycleOwner,
            Observer { noteColor ->
                if(!noteColor.isNullOrEmpty()) {
                    setTitleIndicatorColor(noteColor)
                } else {
                    setTitleIndicatorColor(ColorBLACK)
                }
            }
        )

        viewModel.noteImage.observe(
            viewLifecycleOwner,
            Observer {  imageUri ->
                if (imageUri != null) {
                    showImageFromUri(imageUri)
                }
            }
        )

        viewModel.noteImageDeleted.observe(
            viewLifecycleOwner,
            Observer {
                uploadImageUri = null

                Log.i(TAG, "왜 안 지워져1")
                binding.ivNote.setImageDrawable(null) // 이게 맞는지 모르겠음!!
                binding.ivNote.visibility = View.GONE
                Log.i(TAG, "왜 안 지워져2")
                binding.ivDeleteImage.visibility = View.GONE
            }
        )

        viewModel.webLink.observe(
            viewLifecycleOwner,
            Observer { webLink ->
                if(!webLink.isNullOrEmpty()) {
                    showWebLink(webLink)
                }
            }
        )

        viewModel.webLinkDeleted.observe(
            viewLifecycleOwner,
            Observer {
                binding.tvWebUrl.text = ""
                binding.tvWebUrl.visibility = View.GONE
                binding.layoutWebUrl.visibility = View.GONE
            }
        )

        viewModel.updated.observe(
            viewLifecycleOwner,
            Observer {
                if(it) {
                    findNavController().navigate(R.id.noteListView)
                }
            }
        )

        viewModel.deleted.observe(
            viewLifecycleOwner,
            Observer {
                if(it) {
                    findNavController().navigate(R.id.noteListView)
                }
            }
        )
    }

    private fun showImageFromUrl(url: String?) {
        uploadImageUrl = url

        binding.ivNote.visibility = View.VISIBLE
        binding.ivNote.load(url)
        binding.ivDeleteImage.visibility = View.VISIBLE
        binding.ivDeleteImage.setOnClickListener {
            viewModel.handleEvent(
                NoteDetailEvent.OnNoteImageDeleteClick
            )
        }
    }

    private fun showImageFromUri(uri: Uri) {
        uploadImageUri = uri

        binding.ivNote.visibility = View.VISIBLE
        binding.ivNote.setImageURI(uri)
        binding.ivDeleteImage.visibility = View.VISIBLE
        binding.ivDeleteImage.setOnClickListener {
            viewModel.handleEvent(
                NoteDetailEvent.OnNoteImageDeleteClick
            )
        }
    }

    private fun showWebLink(url: String) {
        binding.layoutWebUrl.visibility = View.VISIBLE
        binding.tvWebUrl.text = url
        binding.ivDeleteWebUrl.visibility = View.VISIBLE
        binding.ivDeleteWebUrl.setOnClickListener {
            viewModel.handleEvent(
                NoteDetailEvent.OnWebLinkDeleteClick
            )
        }
    }

    private fun setTitleIndicatorColor(selectedColor: String) {
        val gradientDrawable = binding.viewSubtitleIndicator.background as GradientDrawable
        gradientDrawable.setColor(Color.parseColor(selectedColor))
    }

    private fun initMisc() {
        val layoutMisc : LinearLayout = binding.misc.layoutMisc
        val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> = BottomSheetBehavior.from(layoutMisc)

        binding.misc.tvMiscellaneous.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        val imageColor1 = binding.misc.ivColor1
        val imageColor2 = binding.misc.ivColor2
        val imageColor3 = binding.misc.ivColor3
        val imageColor4 = binding.misc.ivColor4
        val imageColor5 = binding.misc.ivColor5

        binding.misc.ivColor1.setOnClickListener {
            imageColor1.setImageResource(R.drawable.ic_done)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            viewModel.handleEvent(
                NoteDetailEvent.OnNoteColorChange(ColorBLACK)
            )
        }

        binding.misc.ivColor2.setOnClickListener {
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(R.drawable.ic_done)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            viewModel.handleEvent(
                NoteDetailEvent.OnNoteColorChange(ColorPINK)
            )
        }

        binding.misc.ivColor3.setOnClickListener {
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(R.drawable.ic_done)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            viewModel.handleEvent(
                NoteDetailEvent.OnNoteColorChange(ColorDARKBLUE)
            )
        }

        binding.misc.ivColor4.setOnClickListener {
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(R.drawable.ic_done)
            imageColor5.setImageResource(0)
            viewModel.handleEvent(
                NoteDetailEvent.OnNoteColorChange(ColorYELLOW)
            )
        }

        binding.misc.ivColor5.setOnClickListener {
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(R.drawable.ic_done)
            viewModel.handleEvent(
                NoteDetailEvent.OnNoteColorChange(ColorLIGHTBLUE)
            )
        }

        binding.misc.layoutAddImage.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.misc.layoutAddUrl.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            showAddURLDialog()
        }

        binding.misc.layoutDeleteNote.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            showDeleteNoteDialog()
        }
    }

    private fun showAddURLDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val v: View = LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_add_url, view?.findViewById(R.id.layout_addUrlContainer)
        )
        builder.setView(v)
        val dialogAddURL: AlertDialog = builder.create()
        if (dialogAddURL.window != null) {
            dialogAddURL.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        val inputURL = v.findViewById<EditText>(R.id.et_url)
        inputURL.requestFocus()

        v.findViewById<TextView>(R.id.tv_AddUrl).setOnClickListener {
            if(inputURL.text.toString().trim().isEmpty()){
                showErrorState("Enter URL")
            } else if (!Patterns.WEB_URL.matcher(inputURL.text.toString()).matches()) {
                showErrorState("Enter valid URL")
            } else {
                viewModel.handleEvent(
                    NoteDetailEvent.OnWebLinkChange(inputURL.text.toString().trim())
                )
                dialogAddURL.dismiss()
            }
        }

        v.findViewById<TextView>(R.id.tv_AddUrl_Cancel).setOnClickListener {
            dialogAddURL.dismiss()
        }

        dialogAddURL.show()
    }

    private fun showDeleteNoteDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val v: View = LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_delete_note, view?.findViewById(R.id.layout_DeleteNoteContainer)
        )
        builder.setView(v)
        val dialogDeleteNote: AlertDialog = builder.create()
        if (dialogDeleteNote.window != null) {
            dialogDeleteNote.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        v.findViewById<TextView>(R.id.tv_DeleteNote).setOnClickListener {
            viewModel.handleEvent(
                NoteDetailEvent.OnDeleteClick
            )
            dialogDeleteNote.dismiss()
        }

        v.findViewById<TextView>(R.id.tv_DeleteNote_Cancel).setOnClickListener {
            dialogDeleteNote.dismiss()
        }

        dialogDeleteNote.show()
    }

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {

            Log.i(TAG, "pick uri: $uri")
//            uploadImageUri = uri
            viewModel.handleEvent(
                NoteDetailEvent.OnNoteImageChange(uri)
            )

        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    private fun showErrorState(errorMessage: String) = makeToast(errorMessage)

}