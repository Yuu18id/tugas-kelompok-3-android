package com.stechoq.tugaskelompok3.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase: RoomDatabase() {
    abstract fun daoNote(): DaoNote
}