package com.stechoq.tugaskelompok3

import NoteAdapter
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.stechoq.tugaskelompok3.R
import com.stechoq.tugaskelompok3.database.Note
import com.stechoq.tugaskelompok3.database.NoteDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : AppCompatActivity() {
    lateinit var db: NoteDatabase
    lateinit var noteAdapter: NoteAdapter
    lateinit var rvNotes: RecyclerView
    lateinit var tambahNoteButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tambahNoteButton = findViewById(R.id.tambahNoteButton)
        rvNotes = findViewById(R.id.rvNotes)

        db = Room.databaseBuilder(applicationContext, NoteDatabase::class.java, "note-db").build()

        noteAdapter = NoteAdapter(
            emptyList(),
            this@MainActivity,
            db
        )

        tambahNoteButton.setOnClickListener {
            val dialogView =
                LayoutInflater.from(this).inflate(R.layout.tambah_note, null)
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
            val alertDialog = dialogBuilder.create()
            alertDialog.show()

            val confirmButton: Button = dialogView.findViewById(R.id.confirmTambahNote)

            confirmButton.setOnClickListener {
                val inputTitle = dialogView.findViewById<TextInputEditText>(R.id.inputTitleTextInput).text.toString()
                val inputDesc = dialogView.findViewById<TextInputEditText>(R.id.inputDescTextInput).text.toString()

                if (inputTitle.isNotEmpty() && inputDesc.isNotEmpty()) {
                    GlobalScope.launch {
                        db.daoNote().insert(Note(inputTitle, inputDesc))
                        val notesFromDB = db.daoNote().getAllNotes()
                        runOnUiThread {
                            noteAdapter.updateData(notesFromDB)
                            Toast.makeText(this@MainActivity, "Note berhasil dibuat!", Toast.LENGTH_SHORT).show()
                            alertDialog.dismiss()
                        }
                    }
                    Toast.makeText(this, "Note berhasil dibuat!", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                } else {
                    Toast.makeText(this, "Judul dan Deskripsi tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        rvNotes.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        GlobalScope.launch {
            getData()
        }
    }

    private fun getData() {
        // Fetch all notes from the database
        val notesFromDB = db.daoNote().getAllNotes()

        // Update the RecyclerView adapter with the fetched notes
        runOnUiThread {
            noteAdapter.updateData(notesFromDB)
        }
    }

}