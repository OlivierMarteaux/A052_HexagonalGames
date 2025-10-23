package com.oliviermarteaux.shared.composables

import android.R.attr.bottom
import android.R.attr.onClick
import android.view.WindowInsets.Type.statusBars
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oliviermarteaux.localShared.composables.SharedIconButton
import com.oliviermarteaux.shared.composables.texts.TextTitleLarge
import com.oliviermarteaux.shared.composables.texts.TextTitleSmall
import com.openclassrooms.hexagonal.games.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedScaffold(
    modifier: Modifier = Modifier,
    title: String = "",
    onFabClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    onMenuItem1Click: (() -> Unit)? = null,
    onMenuItem2Click: (() -> Unit)? = null,
    menuItem1Title: String = "",
    menuItem2Title: String = "",
    content: @Composable (contentPadding: PaddingValues) -> Unit = {},
){
    var showMenu by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { TextTitleLarge(title) },
                navigationIcon = {
                    onBackClick?.let {
                        SharedIconButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.contentDescription_go_back)
                        ) { onBackClick() }
                    }
                },
                actions = {
                    onMenuItem1Click?.let{
                        SharedIconButton(
                            icon = Icons.Default.MoreVert,
                            contentDescription = stringResource(id = R.string.contentDescription_more)
                        ) { showMenu = !showMenu }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    onMenuItem1Click()
                                    showMenu = false
                                },
                                text = {
                                    TextTitleSmall(text = menuItem1Title)
                                }
                            )
                            onMenuItem2Click?.let {
                                DropdownMenuItem(
                                    onClick = {
                                        onMenuItem2Click()
                                        showMenu = false
                                    },
                                    text = {
                                        TextTitleSmall(text = menuItem2Title)
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            onFabClick?.let {
                FloatingActionButton(
                    modifier = Modifier.padding(bottom = 20.dp, end = 20.dp),
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(10.dp),
                    onClick = onFabClick
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.description_button_add)
                    )
                }
            }
        },
    ) { contentPadding -> content(contentPadding) }
}