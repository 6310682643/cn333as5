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
import com.example.mynotes.routing.MyPhonesRouter
import com.example.mynotes.routing.Screen
import com.example.mynotes.screens.PhonesScreen
import com.example.mynotes.screens.SaveNoteScreen
import com.example.mynotes.screens.TrashScreen
import com.example.mynotes.ui.theme.PhoneBookTheme
import com.example.mynotes.ui.theme.MyPhonesThemeSettings
import com.example.mynotes.viewmodel.MainViewModel
import com.example.mynotes.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhoneBookTheme(darkTheme = MyPhonesThemeSettings.isDarkThemeEnabled) {
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
        when (MyPhonesRouter.currentScreen) {
            is Screen.Phones -> PhonesScreen(viewModel)
            is Screen.SaveNote -> SaveNoteScreen(viewModel)
            is Screen.Trash -> TrashScreen(viewModel)
        }
    }
}
