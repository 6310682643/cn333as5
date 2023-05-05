package com.example.mynotes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mynotes.domain.model.PhoneModel
import com.example.mynotes.util.fromHex

@ExperimentalMaterialApi
@Composable
fun Phone(
    modifier: Modifier = Modifier,
    phone: PhoneModel,
    onPhoneClick: (PhoneModel) -> Unit = {},
    onPhoneCheckedChange: (PhoneModel) -> Unit = {},
    isSelected: Boolean
) {
    val background = if (isSelected)
        Color.LightGray
    else
        MaterialTheme.colors.surface

    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        backgroundColor = background
    ) {
        ListItem(
            text = { Text(text = phone.title, maxLines = 1) },
            secondaryText = {
                Text(text = phone.color.name+"  "+phone.content , maxLines = 2)
            },

            icon = {
               PhoneColor(
                    color = Color.fromHex(phone.color.hex),
                    size = 40.dp,
                    border = 1.dp
                )
            },
            trailing = {
                if (phone.isCheckedOff != null) {
                    Checkbox(
                        checked = phone.isCheckedOff,
                        onCheckedChange = { isChecked ->
                            val newPhone = phone.copy(isCheckedOff = isChecked)
                            onPhoneCheckedChange.invoke(newPhone)
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            },
            modifier = Modifier.clickable {
                onPhoneClick.invoke(phone)
            }
        )
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
private fun NotePreview() {
    Phone(phone = PhoneModel(1, "Note 1", "Content 1", false), isSelected = true)
}