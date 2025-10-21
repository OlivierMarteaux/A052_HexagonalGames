package com.openclassrooms.hexagonal.games.ui.screen.detail

import android.R.attr.text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oliviermarteaux.shared.composables.SharedAsyncImage
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.composables.TriggeredToast
import com.oliviermarteaux.shared.utils.checkInternetConnection
import com.oliviermarteaux.shared.utils.isOnline
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.ui.HexagonalGamesScaffold
import androidx.compose.runtime.getValue
import com.openclassrooms.hexagonal.games.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    navigateToCommentScreen: (Post) -> Unit = {},
    detailViewModel: DetailViewModel = hiltViewModel()
){

    val post = detailViewModel.post
    val currentUser: User? = detailViewModel.currentUser
    val authError = detailViewModel.authError
//    val context = LocalContext.current
//    val isOnline by checkInternetConnection(context).collectAsState(true)
//    val isOnline: Boolean = detailViewModel.isOnline
    val networkError: Boolean = detailViewModel.networkError

    HexagonalGamesScaffold(
        modifier = modifier,
        title = post.title,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    detailViewModel.onAuthUserClick(
                        onUserLogged = { navigateToCommentScreen(post) },
                        onNoUserLogged = { detailViewModel.showAuthErrorToast() }
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.description_button_add)
                )
            }
        }
    ){ contentPadding ->
        Box {
            DetailBody(
                post = post,
                modifier = modifier.padding(contentPadding),
            )
            Column {
                TriggeredToast(
                    trigger = authError,
                    text = stringResource(R.string.user_disconnected)
                )
                TriggeredToast(
                    trigger = networkError,
                    text = stringResource(R.string.application_error_network),
                    bottomPadding = 120
                )
            }
        }
    }
}

@Composable
private fun DetailBody(
    post: Post,
    modifier: Modifier = Modifier,
) {
    with (post) {
        Column (modifier = modifier){
            Text(text = "By ${author?.lastname} ${author?.firstname}")
            Text(text = title)
            Text(text = description?:"")
            photoUrl?.let{ SharedAsyncImage(photoUri = photoUrl) }
            // Comments list
            LazyColumn{
                items(comments.size){ index ->
                    Comment(comments[index])
                }
            }
        }
    }
}

@Composable
fun Comment(comment: Comment) {
    Column{
        Text(text = "${comment.author.firstname} ${comment.author.lastname}")
        Text(text = comment.content)
    }
}