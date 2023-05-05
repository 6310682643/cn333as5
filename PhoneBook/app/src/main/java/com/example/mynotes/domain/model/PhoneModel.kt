package com.example.mynotes.domain.model

const val NEW_PHONE_ID = -1L

data class PhoneModel(
    val id: Long = NEW_PHONE_ID, // This value is used for new notes
    val title: String = "",
    val content: String = "",
    val isCheckedOff: Boolean? = null, // null represents that the note can't be checked off
    val color: ColorModel = ColorModel.DEFAULT
)