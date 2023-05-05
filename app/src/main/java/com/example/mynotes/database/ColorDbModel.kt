package com.example.mynotes.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ColorDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "hex") val hex: String,
    @ColumnInfo(name = "name") val name: String
) {
    companion object {
        val DEFAULT_COLORS = listOf(
            ColorDbModel(1, "#FFFFFF",  "Home"),
            ColorDbModel(2, "#E57373", "Work"),
            ColorDbModel(3, "#F06292",  "Mobile"),
            ColorDbModel(4, "#CE93D8",  "Friend"),
            ColorDbModel(5, "#2196F3", "Family"),
            ColorDbModel(6, "#00ACC1", "Other"),
        )
        val DEFAULT_COLOR = DEFAULT_COLORS[0]
    }
}
