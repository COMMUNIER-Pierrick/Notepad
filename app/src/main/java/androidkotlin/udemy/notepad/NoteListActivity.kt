package androidkotlin.udemy.notepad

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidkotlin.udemy.notepad.utils.deleteNote
import androidkotlin.udemy.notepad.utils.loadNotes
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class NoteListActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var notes: MutableList<Note>
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        findViewById<FloatingActionButton>(R.id.create_note_fab).setOnClickListener(this)
        coordinatorLayout = findViewById<CoordinatorLayout>(R.id.coordinator_layout)

        notes = loadNotes(this)

        adapter = NoteAdapter(notes, this)

        val recyclerView = findViewById<RecyclerView>(R.id.notes_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        when (requestCode) {
            NoteDetailActivity.REQUEST_EDIT_NOTE -> processEditNoteResult(data)
        }
    }

    private fun processEditNoteResult(data: Intent) {
        val noteIndex = data.getIntExtra(NoteDetailActivity.EXTRA_NOTE_INDEX, -1)

        when (data.action) {
            NoteDetailActivity.ACTION_SAVE_NOTE -> {
                val note = data.getParcelableExtra<Note>(NoteDetailActivity.EXTRA_NOTE)
                if (note != null) {
                    saveNote(note, noteIndex)
                }
            }
            NoteDetailActivity.ACTION_DELETE_NOTE -> deleteNote(noteIndex)
        }

    }

    override fun onClick(view: View) {
        if(view.tag != null) {
            showNoteDetail(view.tag as Int)
        }else {
            when (view.id) {
                R.id.create_note_fab -> createNote()
            }
        }
    }

    private fun createNote() {
        showNoteDetail(-1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun saveNote(note: Note, noteIndex: Int) {
        if(noteIndex < 0) {
            notes.add(0, note)
        }else {
            notes[noteIndex] = note
        }
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteNote(noteIndex: Int) {
        if(noteIndex < 0) {
            return
        }
        val note = notes.removeAt(noteIndex)
        deleteNote(this, note)
        adapter.notifyDataSetChanged()

        Snackbar.make(coordinatorLayout, "${note.title} supprimé", Snackbar.LENGTH_SHORT)
            .show()
    }

    fun showNoteDetail(noteIndex: Int) {

        val note = if(noteIndex < 0) Note() else notes[noteIndex]

        val intent = Intent(this, NoteDetailActivity::class.java)
        intent.putExtra(NoteDetailActivity.EXTRA_NOTE, note as Parcelable)
        intent.putExtra(NoteDetailActivity.EXTRA_NOTE_INDEX, noteIndex)
        startActivityForResult(intent, NoteDetailActivity.REQUEST_EDIT_NOTE)
    }
}