package com.openclassrooms.hexagonal.games.ui.screen.comment

import android.R.attr.text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.localShared.ui.theme.SharedPadding
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedOutlinedTextField
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.ui.theme.SharedSize
import com.openclassrooms.hexagonal.games.R

/**
 * A screen for adding a comment to a post.
 *
 * @param modifier The modifier to apply to this screen.
 * @param onBackClick A function to call when the back button is clicked.
 * @param commentViewModel The view model for this screen.
 */
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
            title = stringResource(R.string.comment_screen_title),
            onBackClick = onBackClick
        ) { contentPadding ->
            Box {
                CommentBody(
                    commentContent = commentContent,
                    onCommentChange = ::onCommentChange,
                    modifier = modifier
                        .padding(contentPadding)
                        .padding(SharedPadding.xl)
                        .fillMaxSize(),
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

/**
 * A composable for the body of the comment screen.
 *
 * @param commentContent The content of the comment.
 * @param onCommentChange A function to call when the comment content changes.
 * @param modifier The modifier to apply to this composable.
 * @param onBackClick A function to call when the back button is clicked.
 * @param addComment A function to call to add the comment.
 * @param isOnline A boolean indicating if the device is online.
 * @param showNetworkErrorToast A function to call to show a network error toast.
 */
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
    val commentFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { commentFocusRequester.requestFocus() }
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ){
        SharedOutlinedTextField(
            value = commentContent,
            onValueChange = { onCommentChange(it) },
            label = stringResource(R.string.comment_screen_label_comment),
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
            isError = commentContent.isEmpty(),
            errorText = stringResource(R.string.comment_screen_error_empty),
            maxLines = Int.MAX_VALUE,
            singleLine = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(SharedSize.xxl)
                .focusRequester(commentFocusRequester)
        )
        SharedButton(
            text = stringResource(R.string.save),
            enabled = commentContent.isNotEmpty()
        ) { if (isOnline) { addComment(onBackClick) } else { showNetworkErrorToast() } }
        Spacer(Modifier.size(SharedSize.xxl))
    }
}