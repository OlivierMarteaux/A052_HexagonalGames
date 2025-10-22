package com.openclassrooms.hexagonal.games.ui.screen.detail

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.common.math.LinearTransformation.vertical
import com.oliviermarteaux.shared.composables.SharedAsyncImage
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.composables.texts.TextBodyLarge
import com.oliviermarteaux.shared.composables.texts.TextBodySmall
import com.oliviermarteaux.shared.composables.texts.TextHeadLineLarge
import com.oliviermarteaux.shared.composables.texts.TextLabelLarge
import com.oliviermarteaux.shared.composables.texts.TextTitleMedium
import com.oliviermarteaux.shared.composables.texts.TextTitleSmall
import com.oliviermarteaux.shared.ui.theme.SharedPadding
import com.oliviermarteaux.shared.ui.theme.SharedShapes
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
                    modifier = modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(SharedPadding.large),
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
            TextTitleSmall("By ${author?.lastname} ${author?.firstname}")
            Spacer(Modifier.padding(SharedPadding.small))
            TextTitleMedium(title)
            TextBodyLarge(description?:"")
            photoUrl?.let{ SharedAsyncImage(
                photoUri = photoUrl,
                alignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SharedShapes.small)
                    .heightIn(max = 400.dp),
                contentScale = ContentScale.FillWidth
            ) }
            // Comments list
            Spacer(Modifier.padding(SharedPadding.large))
            TextLabelLarge("Comments")
            Spacer(Modifier.padding(SharedPadding.medium))
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
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = SharedPadding.small),
    ) {
        Column(modifier = Modifier.padding(SharedPadding.large)) {
            TextTitleSmall(text = "${comment.author.firstname} ${comment.author.lastname}")
            Spacer(Modifier.padding(SharedPadding.small))
            TextBodySmall(text = comment.content)
        }
    }
}