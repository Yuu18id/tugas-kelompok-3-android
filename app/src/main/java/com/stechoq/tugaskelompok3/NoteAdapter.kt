import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.stechoq.tugaskelompok3.MainActivity
import com.stechoq.tugaskelompok3.R
import com.stechoq.tugaskelompok3.database.Note
import com.stechoq.tugaskelompok3.database.NoteDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NoteAdapter(
    private var notes: List<Note>,
    private val context: Context,
    private val db: NoteDatabase
) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notes[position]
        holder.titleTextView.text = currentNote.title
        holder.descTextView.text = currentNote.desc
        holder.itemView.setOnClickListener {
            showNoteDialog(currentNote)
        }
    }

    override fun getItemCount() = notes.size

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descTextView: TextView = itemView.findViewById(R.id.descTextView)
    }

    fun updateData(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    private fun showNoteDialog(note: Note) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.detail_note, null)
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        dialogView.findViewById<TextView>(R.id.titleDetailTextView).text = note.title
        dialogView.findViewById<TextView>(R.id.descdetailTextView).text = note.desc

        dialogView.findViewById<Button>(R.id.deleteNoteButton).setOnClickListener {
            alertDialog.dismiss()
            showDeleteNoteDialog(note)
        }

        dialogView.findViewById<Button>(R.id.editNoteButton).setOnClickListener {
            alertDialog.dismiss()
            showEditNoteDialog(note)
        }
    }


    private fun showDeleteNoteDialog(note: Note) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.hapus_note, null)
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        dialogView.findViewById<Button>(R.id.cancelDeleteButton).setOnClickListener {
            alertDialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.confirmDeleteButton).setOnClickListener {
            GlobalScope.launch {
                db.daoNote().deleteNote(note)
                val notesFromDB = db.daoNote().getAllNotes()
                (context as MainActivity).runOnUiThread {
                    updateData(notesFromDB)
                    Toast.makeText(context, "Catatan berhasil dihapus!", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                }
            }
        }
    }
    private fun showEditNoteDialog(note: Note) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.edit_note, null)
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        val editTitleTextInput = dialogView.findViewById<TextInputEditText>(R.id.editTitleTextInput)
        editTitleTextInput.setText(note.title)
        val editDescTextInput = dialogView.findViewById<TextInputEditText>(R.id.editDescTextInput)
        editDescTextInput.setText(note.desc)

        dialogView.findViewById<Button>(R.id.confirmEditNote).setOnClickListener {
            val updatedTitle = editTitleTextInput.text.toString().trim()
            val updatedDesc = editDescTextInput.text.toString().trim()

            if (updatedTitle.isNotEmpty() && updatedDesc.isNotEmpty()) {
                // Update note object
                note.title = updatedTitle
                note.desc = updatedDesc

                // Update note in database using Dao
                GlobalScope.launch {
                    db.daoNote().updateNote(note)
                    val notesFromDB = db.daoNote().getAllNotes()
                    (context as MainActivity).runOnUiThread {
                        updateData(notesFromDB)
                        Toast.makeText(context, "Catatan berhasil diubah!", Toast.LENGTH_SHORT).show()
                        alertDialog.dismiss()
                    }
                }
            } else {
                Toast.makeText(context, "Judul dan Deskripsi tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
