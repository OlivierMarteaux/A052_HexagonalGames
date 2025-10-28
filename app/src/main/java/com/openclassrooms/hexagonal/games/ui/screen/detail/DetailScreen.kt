package com.openclassrooms.hexagonal.games.ui.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.shared.composables.texts.TextBodySmall
import com.oliviermarteaux.shared.ui.theme.SharedPadding
import com.oliviermarteaux.shared.composables.SharedCardAsyncImage
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.composables.texts.TextBodyLarge
import com.oliviermarteaux.shared.composables.texts.TextTitleMedium
import com.oliviermarteaux.shared.composables.texts.TextTitleSmall
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post

/**
 * A composable function that displays the detail screen for a specific post.
 *
 * This screen shows the post's title, author, description, photo, and a list of comments.
 * It provides a FAB to add a new comment, which is only enabled for logged-in users.
 *
 * @param modifier The modifier to be applied to the screen's root layout.
 * @param onBackClick A lambda function to be invoked when the back button is pressed.
 * @param navigateToCommentScreen A lambda function that navigates to the comment screen, passing the current post.
 * @param detailViewModel The ViewModel responsible for the business logic of this screen.
 */
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
                    modifier = modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(SharedPadding.medium)
                        .verticalScroll(rememberScrollState()),
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

/**
 * A private composable function that displays the main content of the post.
 *
 * It includes the author's name, post title, description, photo, and the list of comments.
 *
 * @param post The [Post] object containing the details to be displayed.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
private fun DetailBody(
    post: Post,
    modifier: Modifier = Modifier,
) {
    with (post) {
        Column (modifier = modifier){
            TextTitleSmall(
                stringResource(
                    R.string.detail_screen_text_author,
                    author?.firstname?:"",
                    author?.lastname?:""
                ))
            Spacer(Modifier.padding(SharedPadding.xs))
            TextTitleMedium(title)
            TextBodyLarge(description?:"")
            photoUrl?.let{ SharedCardAsyncImage(
                photoUri = photoUrl,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.heightIn(max = 400.dp),
                imageModifier = Modifier.fillMaxWidth()
            ) }
            // Comments list
            Spacer(Modifier.padding(SharedPadding.medium))
            TextBodyLarge(stringResource(R.string.detail_screen_text_comments))
            Spacer(Modifier.padding(SharedPadding.small))
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)){
                items(comments.size){ index ->
                    Comment(comments[index])
                }
            }
        }
    }
}

/**
 * A composable function that displays a single comment item.
 *
 * The comment is displayed in an elevated card, showing the author's full name and the comment content.
 *
 * @param comment The [Comment] object to be displayed.
 */
@Composable
fun Comment(comment: Comment) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SharedPadding.xs),
    ) {
        Column(modifier = Modifier.padding(SharedPadding.medium)) {
            TextTitleSmall(text = "${comment.author.firstname} ${comment.author.lastname}")
            Spacer(Modifier.padding(SharedPadding.xs))
            TextBodySmall(text = comment.content)
        }
    }
}
