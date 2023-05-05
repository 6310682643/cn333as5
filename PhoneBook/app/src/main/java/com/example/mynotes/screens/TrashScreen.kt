package com.example.mynotes.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import com.example.mynotes.R
import com.example.mynotes.domain.model.PhoneModel
import com.example.mynotes.routing.Screen
import com.example.mynotes.ui.components.AppDrawer
import com.example.mynotes.ui.components.Phone
import com.example.mynotes.viewmodel.MainViewModel
import kotlinx.coroutines.launch

private const val NO_DIALOG = 1
private const val RESTORE_PHONES_DIALOG = 2
private const val PERMANENTLY_DELETE_DIALOG = 3

@Composable
@ExperimentalMaterialApi
fun TrashScreen(viewModel: MainViewModel) {

    val notesInThrash: List<PhoneModel> by viewModel.phonesInTrash
        .observeAsState(listOf())

    val selectedPhones: List<PhoneModel> by viewModel.selectedPhones
        .observeAsState(listOf())

    val dialogState = rememberSaveable { mutableStateOf(NO_DIALOG) }

    val scaffoldState = rememberScaffoldState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            val areActionsVisible = selectedPhones.isNotEmpty()
            TrashTopAppBar(
                onNavigationIconClick = {
                    coroutineScope.launch { scaffoldState.drawerState.open() }
                },
                onRestorePhonesClick = { dialogState.value = RESTORE_PHONES_DIALOG },
                onDeletePhonesClick = { dialogState.value = PERMANENTLY_DELETE_DIALOG },
                areActionsVisible = areActionsVisible
            )
        },
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Trash,
                closeDrawerAction = {
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }
            )
        },
        content = {
            Content(
                phones = notesInThrash,
                onPhoneClick = { viewModel.onPhoneSelected(it) },
                selectedPhones = selectedPhones
            )

            val dialog = dialogState.value
            if (dialog != NO_DIALOG) {
                val confirmAction: () -> Unit = when (dialog) {
                    RESTORE_PHONES_DIALOG -> {
                        {
                            viewModel.restorePhones(selectedPhones)
                            dialogState.value = NO_DIALOG
                        }
                    }
                    PERMANENTLY_DELETE_DIALOG -> {
                        {
                            viewModel.permanentlyDeletePhones(selectedPhones)
                            dialogState.value = NO_DIALOG
                        }
                    }
                    else -> {
                        {
                            dialogState.value = NO_DIALOG
                        }
                    }
                }

                AlertDialog(
                    onDismissRequest = { dialogState.value = NO_DIALOG },
                    title = { Text(mapDialogTitle(dialog)) },
                    text = { Text(mapDialogText(dialog)) },
                    confirmButton = {
                        TextButton(onClick = confirmAction) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { dialogState.value = NO_DIALOG }) {
                            Text("Dismiss")
                        }
                    }
                )
            }
        }
    )
}

@Composable
private fun TrashTopAppBar(
    onNavigationIconClick: () -> Unit,
    onRestorePhonesClick: () -> Unit,
    onDeletePhonesClick: () -> Unit,
    areActionsVisible: Boolean
) {
    TopAppBar(
        title = { Text(text = "Trash", color = MaterialTheme.colors.onPrimary) },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = "Drawer Button"
                )
            }
        },
        actions = {
            if (areActionsVisible) {
                IconButton(onClick = onRestorePhonesClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_restore_from_trash_24),
                        contentDescription = "Restore Contacts Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
                IconButton(onClick = onDeletePhonesClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_forever_24),
                        contentDescription = "Delete Contacts Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}

@Composable
@ExperimentalMaterialApi
private fun Content(
    phones: List<PhoneModel>,
    onPhoneClick: (PhoneModel) -> Unit,
    selectedPhones: List<PhoneModel>,
) {
    val tabs = listOf("CONTACT", "FAV CONTACT")

    // Init state for selected tab
    var selectedTab by remember { mutableStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        val filteredNotes = when (selectedTab) {
            0 -> {
                phones.filter { it.isCheckedOff == null }
            }
            1 -> {
                phones.filter { it.isCheckedOff != null }
            }
            else -> throw IllegalStateException("Tab not supported - index: $selectedTab")
        }

        LazyColumn {
            items(count = filteredNotes.size) { noteIndex ->
                val note = filteredNotes[noteIndex]
                val isNoteSelected = selectedPhones.contains(note)
                Phone(
                    phone = note,
                    onPhoneClick = onPhoneClick,
                    isSelected = isNoteSelected
                )
            }
        }
    }
}

private fun mapDialogTitle(dialog: Int): String = when (dialog) {
    RESTORE_PHONES_DIALOG -> "Restore contacts"
    PERMANENTLY_DELETE_DIALOG -> "Delete contacts forever"
    else -> throw RuntimeException("Dialog not supported: $dialog")
}

private fun mapDialogText(dialog: Int): String = when (dialog) {
    RESTORE_PHONES_DIALOG -> "Are you sure you want to restore selected contacts?"
    PERMANENTLY_DELETE_DIALOG -> "Are you sure you want to delete selected contacts permanently?"
    else -> throw RuntimeException("Dialog not supported: $dialog")
}