package com.example.mynotes.database

import com.example.mynotes.domain.model.ColorModel
import com.example.mynotes.domain.model.NEW_PHONE_ID
import com.example.mynotes.domain.model.PhoneModel

class DbMapper {
    // Create list of NoteModels by pairing each note with a color
    fun mapPhones(
        phoneDbModels: List<PhoneDbModel>,
        colorDbModels: Map<Long, ColorDbModel>
    ): List<PhoneModel> = phoneDbModels.map {
        val colorDbModel = colorDbModels[it.colorId]
            ?: throw RuntimeException("Color for colorId: ${it.colorId} was not found. Make sure that all colors are passed to this method")

        mapPhone(it, colorDbModel)
    }

    // convert NoteDbModel to NoteModel
    fun mapPhone(phoneDbModel: PhoneDbModel, colorDbModel: ColorDbModel): PhoneModel {
        val color = mapColor(colorDbModel)
        val isCheckedOff = with(phoneDbModel) { if (canBeCheckedOff) isCheckedOff else null }
        return with(phoneDbModel) { PhoneModel(id, title, content, isCheckedOff, color) }
    }

    // convert list of ColorDdModels to list of ColorModels
    fun mapColors(colorDbModels: List<ColorDbModel>): List<ColorModel> =
        colorDbModels.map { mapColor(it) }

    // convert ColorDbModel to ColorModel
    fun mapColor(colorDbModel: ColorDbModel): ColorModel =
        with(colorDbModel) { ColorModel(id, name, hex) }

    // convert NoteModel back to NoteDbModel
    fun mapDbPhone(phone: PhoneModel): PhoneDbModel =
        with(phone) {
            val canBeCheckedOff = isCheckedOff != null
            val isCheckedOff = isCheckedOff ?: false
            if (id == NEW_PHONE_ID)
                PhoneDbModel(
                    title = title,
                    content = content,
                    canBeCheckedOff = canBeCheckedOff,
                    isCheckedOff = isCheckedOff,
                    colorId = color.id,
                    isInTrash = false
                )
            else
                PhoneDbModel(id, title, content, canBeCheckedOff, isCheckedOff, color.id, false)
        }
}