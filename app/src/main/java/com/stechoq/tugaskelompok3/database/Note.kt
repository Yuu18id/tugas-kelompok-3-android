package com.stechoq.tugaskelompok3.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Note (
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "desc") var desc: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}