package com.example.mynotes.routing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


sealed class Screen {
    object Phones: Screen()
    object SaveNote: Screen()
    object Trash: Screen()
}

object MyPhonesRouter {
    var currentScreen: Screen by mutableStateOf(Screen.Phones)

    fun navigateTo(destination: Screen) {
        currentScreen = destination
    }
}