package com.androiddevs.ktornoteapp.ui.addeditnote

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.data.local.entities.Note
import com.androiddevs.ktornoteapp.other.Constants.DEFAULT_COLOR_NOTES
import com.androiddevs.ktornoteapp.other.Constants.KEY_LOGGED_IN_EMAIL
import com.androiddevs.ktornoteapp.other.Constants.KEY_PASSWORD
import com.androiddevs.ktornoteapp.other.Constants.NO_EMAIL
import com.androiddevs.ktornoteapp.other.Status
import com.androiddevs.ktornoteapp.ui.BaseFragment
import com.androiddevs.ktornoteapp.ui.dialogs.ColorPickerDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_edit_note.*
import kotlinx.android.synthetic.main.item_note.view.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddEditNoteFragment:BaseFragment(R.layout.fragment_add_edit_note) {

    private val viewModel:AddEditNoteViewModel by viewModels()
    private val args :AddEditNoteFragmentArgs by navArgs()
    private var curNote: Note?= null
    private var currNoteColor  = DEFAULT_COLOR_NOTES

    @Inject
    lateinit var sharedPref:SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(args.id.isNotEmpty()){
            viewModel.getNoteById(args.id)
        }
        viewNoteColor.setOnClickListener {
            ColorPickerDialogFragment().apply {

            }
        }
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.note.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.getContentIfnotHandled()?.let {result->
                when(result.status){
                    Status.SUCCESS->{
                        val note = result.data!!
                        curNote = note
                        etNoteTitle.setText(note.title)
                        etNoteContent.setText(note.content)
                        changeViewNoteColor(note.color)
                    }
                    Status.ERROR->{
                        showSnackbar(result.message ?: "Note not found")
                    }
                    Status.LOADING->{

                    }
                }
            }
        })
    }

    private fun changeViewNoteColor(colorstr:String){
        val drawable  = ResourcesCompat.getDrawable(resources,R.drawable.circle_shape,null)
        drawable?.let{
            val wrappedDrawable = DrawableCompat.wrap(it)
            val color = Color.parseColor("#${colorstr}")
            DrawableCompat.setTint(wrappedDrawable,color)
            viewNoteColor.background = wrappedDrawable
            currNoteColor = colorstr
        }

    }

    override fun onPause() {
        super.onPause()
        saveNotes()

    }
    private fun saveNotes(){
        val authEmail = sharedPref.getString(KEY_LOGGED_IN_EMAIL, NO_EMAIL)?: NO_EMAIL
        val title = etNoteTitle.text.toString()
        val content = etNoteContent.text.toString()
        if(title.isEmpty() || content.isEmpty()){
            return
        }
        val date = System.currentTimeMillis()
        val color = currNoteColor
        val id = curNote?.id?: UUID.randomUUID().toString()
        val owners = curNote?.owner ?: listOf(authEmail)
        val note = Note(title,content,date,owners,color,id = id)
        viewModel.insertNote(note)

    }
}