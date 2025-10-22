package com.openclassrooms.hexagonal.games.ui.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.shared.composables.SharedAsyncImage
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.composables.TriggeredToast
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    navigateToCommentScreen: (Post) -> Unit = {},
    detailViewModel: DetailViewModel = hiltViewModel()
){
    with (detailViewModel) {
        SharedScaffold(
            modifier = modifier,
            title = post.title,
            onBackClick = onBackClick,
            onFabClick = {
                checkUserState(
                    onUserLogged = { navigateToCommentScreen(post) },
                    onNoUserLogged = ::showAuthErrorToast
                )
            }
        ) { contentPadding ->
            Box {
                DetailBody(
                    post = post,
                    modifier = modifier.padding(contentPadding),
                )
                if(authError) SharedToast(text = stringResource(R.string.user_disconnected))
                if(networkError) SharedToast(
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