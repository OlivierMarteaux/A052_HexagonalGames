package com.openclassrooms.hexagonal.games.screen.comment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedOutlinedTextField
import com.oliviermarteaux.shared.composables.TriggeredToast
import com.oliviermarteaux.shared.utils.checkInternetConnection
import com.oliviermarteaux.shared.utils.isOnline
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.HexagonalGamesScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    commentViewModel: CommentViewModel = hiltViewModel()
){
//    val context = LocalContext.current
//    val isOnline: Boolean by checkInternetConnection(context).collectAsState(true)
//    val noInternetToast: Boolean = commentViewModel.noInternetToast
    val isOnline: Boolean = commentViewModel.isOnline
    val networkError: Boolean = commentViewModel.networkError
    val unknownError: Boolean = commentViewModel.unknownError

    HexagonalGamesScaffold(
        modifier = modifier,
        title = "Add a comment",
    ){ contentPadding ->
        Box {
            CommentBody(
                commentContent = commentViewModel.commentContent,
                onCommentChange = commentViewModel::onCommentChange,
                modifier = modifier.padding(contentPadding),
                onBackClick = onBackClick,
                addComment = commentViewModel::addComment,
                isOnline = isOnline,
                showNetworkErrorToast = commentViewModel::showNetworkErrorToast,
            )
            TriggeredToast(
                trigger = unknownError,
                text = stringResource(R.string.application_error_unknown)
            )
//            TriggeredToast(
//                trigger = !isOnline,
//                text = stringResource(R.string.application_error_network),
//                bottomPadding = 120
//            )
            TriggeredToast(
                trigger = networkError,
                text = stringResource(R.string.application_error_network),
                bottomPadding = 120
            )
        }
    }
}

@Composable
private fun CommentBody(
    commentContent: String,
    onCommentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    addComment: (() -> Unit) -> Unit = {},
    isOnline: Boolean = true,
    showNetworkErrorToast: () -> Unit
) {
    Column (modifier = modifier){
        SharedOutlinedTextField(
            value = commentContent,
            onValueChange = { onCommentChange(it) },
            label = stringResource(R.string.comment_screen_label_comment),
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
            isError = commentContent.isEmpty(),
            errorText = stringResource(R.string.comment_screen_error_empty)
        )
        SharedButton(
            text = stringResource(R.string.save),
            enabled = commentContent.isNotEmpty()
        ) { if (isOnline) { addComment(onBackClick) } else { showNetworkErrorToast() } }
    }
}