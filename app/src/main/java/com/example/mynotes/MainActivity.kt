package com.example.mynotes

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mynotes.routing.MyNotesRouter
import com.example.mynotes.routing.Screen
import com.example.mynotes.screens.PhonesScreen
import com.example.mynotes.screens.SavePhoneScreen
import com.example.mynotes.screens.TrashScreen
import com.example.mynotes.ui.theme.PhoneBooksTheme
import com.example.mynotes.ui.theme.PhoneBooksThemeSettings
import com.example.mynotes.viewmodel.MainViewModel
import com.example.mynotes.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhoneBooksTheme(darkTheme = PhoneBooksThemeSettings.isDarkThemeEnabled) {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(LocalContext.current.applicationContext as Application)
                )
                MainActivityScreen(viewModel)
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun MainActivityScreen(viewModel: MainViewModel) {
    Surface {
        when (MyNotesRouter.currentScreen) {
            is Screen.Notes -> PhonesScreen(viewModel)
            is Screen.SaveNote -> SavePhoneScreen(viewModel)
            is Screen.Trash -> TrashScreen(viewModel)
        }
    }
}
