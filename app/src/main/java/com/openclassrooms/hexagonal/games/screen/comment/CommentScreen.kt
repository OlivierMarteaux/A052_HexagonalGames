package com.openclassrooms.hexagonal.games.screen.comment

import android.R.attr.text
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
import com.oliviermarteaux.shared.composables.TriggeredToast
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.HexagonalGamesScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    commentViewModel: CommentViewModel = hiltViewModel()
){
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
                addComment = commentViewModel::addComment
            )
            TriggeredToast(
                trigger = unknownError,
                text = stringResource(R.string.application_error_unknown)
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
    addComment: (() -> Unit) -> Unit = {}
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
            enabled = commentContent.isNotEmpty(),
        ){ addComment(onBackClick) }
    }
}