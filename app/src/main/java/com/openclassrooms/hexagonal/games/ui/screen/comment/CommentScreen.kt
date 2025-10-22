package com.openclassrooms.hexagonal.games.ui.screen.comment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedOutlinedTextField
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.composables.TriggeredToast
import com.openclassrooms.hexagonal.games.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    commentViewModel: CommentViewModel = hiltViewModel()
){
    with(commentViewModel) {
        SharedScaffold(
            modifier = modifier,
            title = "Add a comment",
            onBackClick = onBackClick
        ) { contentPadding ->
            Box {
                CommentBody(
                    commentContent = commentContent,
                    onCommentChange = ::onCommentChange,
                    modifier = modifier.padding(contentPadding),
                    onBackClick = onBackClick,
                    addComment = ::addComment,
                    isOnline = isOnline,
                    showNetworkErrorToast = ::showNetworkErrorToast,
                )
                if(unknownError) SharedToast(text = stringResource(R.string.application_error_unknown))
                if(networkError) SharedToast(
                    text = stringResource(R.string.application_error_network),
                    bottomPadding = 120
                )
            }
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