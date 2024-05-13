package com.stechoq.tugaskelompok3.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DaoNote {

    @Query("SELECT * FROM note")
    fun getAllNotes(): List<Note>

    @Insert
    fun insert(vararg note: Note)

    @Delete
    fun deleteNote(note: Note)

    @Update
    fun updateNote(note: Note)
}