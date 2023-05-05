package com.example.mynotes.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhoneDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "can_be_checked_off") val canBeCheckedOff: Boolean,
    @ColumnInfo(name = "is_checked_off") val isCheckedOff: Boolean,
    @ColumnInfo(name = "color_id") val colorId: Long,
    @ColumnInfo(name = "in_trash") val isInTrash: Boolean
) {
    companion object {
        val DEFAULT_NOTES = listOf(
            PhoneDbModel(1, "Abrahum", "0832934887", false, false, 1, false, ),
            PhoneDbModel(2, "Billy third", "0842628948", false, false, 2, false, ),
            PhoneDbModel(3, "GOJO", "0877774444", false, false, 3, false, ),
            PhoneDbModel(4, "Jaemin", "0255034140", false, false, 4, false, ),
            PhoneDbModel(5, "Marklee", "0919872145", false, false, 5, false, ),
            PhoneDbModel(6, "NCT", "0818448183", false, false, 4, false, ),
            PhoneDbModel(7, "Oreo", "0934887555", false, false, 2, false, ),
            PhoneDbModel(8, "Sabuhe", "0855555555", false, false, 4, false, ),
        )
    }
}
