package com.example.mynotes.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import com.example.mynotes.domain.model.PhoneModel
import com.example.mynotes.routing.Screen
import com.example.mynotes.ui.components.AppDrawer
import com.example.mynotes.ui.components.Phone
import com.example.mynotes.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun PhonesScreen(viewModel: MainViewModel) {
    val phones by viewModel.phonesNotInTrash.observeAsState(listOf())
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Contacts",
                        color = MaterialTheme.colors.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch { scaffoldState.drawerState.open() }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "Drawer Button"
                        )
                    }
                }
            )
        },
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Phones,
                closeDrawerAction = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onCreateNewPhoneClick() },
                contentColor = MaterialTheme.colors.background,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Note Button"
                    )
                }
            )
        }
    ) {
        if (phones.isNotEmpty()) {
            PhonesList(
                phones = phones.sortedBy { it.title },
                onPhoneCheckedChange = {
                    viewModel.onPhoneCheckedChange(it)
                },
                onPhoneClick = { viewModel.onPhoneClick(it) }
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun PhonesList(
    phones: List<PhoneModel>,
    onPhoneCheckedChange: (PhoneModel) -> Unit,
    onPhoneClick: (PhoneModel) -> Unit
) {
    LazyColumn {
        items(count = phones.size) { phoneIndex ->
            val phone = phones[phoneIndex]
            Phone(
                phone = phone,
                onPhoneClick = onPhoneClick,
                onPhoneCheckedChange = onPhoneCheckedChange,
                isSelected = false
            )
        }
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
private fun PhonesListPreview() {
    PhonesList(
        phones = listOf(
            PhoneModel(1, "A", "Content 1", null),
            PhoneModel(2, "OI", "Content 2", false),
            PhoneModel(3, "E", "Content 3", true)
        ).sortedBy { it.title },
        onPhoneCheckedChange = {},
        onPhoneClick = {}
    )
}