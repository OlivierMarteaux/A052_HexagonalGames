package com.openclassrooms.hexagonal.games.ui.screen.homefeed

import android.R.attr.bottom
import android.R.attr.contentDescription
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.oliviermarteaux.localShared.composables.SharedAsyncImage
import com.oliviermarteaux.shared.composables.CenteredCircularProgressIndicator
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.composables.texts.TextTitleMedium
import com.oliviermarteaux.shared.composables.texts.TextTitleSmall
import com.oliviermarteaux.shared.ui.UiState
import com.oliviermarteaux.localShared.ui.theme.SharedPadding
import com.oliviermarteaux.shared.ui.theme.SharedShapes
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFeedScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeFeedViewModel = hiltViewModel(),
    onPostClick: (Post) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    navigateToLogin: () -> Unit = {},
    navigateToAccount: () -> Unit = {},
    navigateToAddPost: () -> Unit = {}
) {
    with(viewModel) {
        SharedScaffold(
            title = stringResource(id = R.string.homefeed_fragment_label),
            onMenuItem1Click = onSettingsClick,
            menuItem1Title = stringResource(id = R.string.action_settings),
            onMenuItem2Click = {
                checkUserState(
                    onUserLogged = navigateToAccount,
                    onNoUserLogged = navigateToLogin
                )
            },
            menuItem2Title = stringResource(id = R.string.my_account),
            onFabClick = {
                checkUserState(
                    onUserLogged = navigateToAddPost,
                    onNoUserLogged = ::showAuthErrorToast
                )
            }
        ) { contentPadding ->
            LaunchedEffect(homeFeedUiState) {
                Log.i(
                    "OM_TAG",
                    "HomeFeedViewModel: LaunchedEffect: homeFeedUiState = $homeFeedUiState"
                )
            }
            Box {
                //_ UiState management: Empty, Error, Loading, Success
                when (homeFeedUiState) {
                    is UiState.Loading -> CenteredCircularProgressIndicator()
                    is UiState.Empty -> SharedToast(stringResource(R.string.homefeed_empty_state))
                    is UiState.Error -> {
                        SharedToast(
                            text = stringResource(R.string.application_error_unknown),
                            bottomPadding = 200
                        )
                    }

                    is UiState.Success -> {
                        HomeFeedList(
                            modifier = modifier.padding(contentPadding).padding(horizontal = SharedPadding.medium),
                            posts = (homeFeedUiState as UiState.Success<Post>).data,
                            onPostClick = onPostClick
                        )
                    }
                }
                if (authError) SharedToast(
                    text = stringResource(R.string.homefeed_error_no_user_logged),
                    bottomPadding = 120
                )
                if (networkError) SharedToast(
                    text = stringResource(R.string.application_error_network),
                    bottomPadding = 160
                )
            }
        }
    }
}

@Composable
private fun HomeFeedList(
    modifier: Modifier = Modifier,
    posts: List<Post>,
    onPostClick: (Post) -> Unit,
) {
    LazyColumn(
        modifier = modifier/*.padding(8.dp)*/,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(posts) { post ->
            HomeFeedCell(
                post = post,
                onPostClick = onPostClick
            )
        }
    }
}

@Composable
private fun HomeFeedCell(
    post: Post,
    onPostClick: (Post) -> Unit,
) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            onPostClick(post)
        }) {
        Column(
            modifier = Modifier.padding(SharedPadding.medium),
        ) {
            TextTitleSmall(
                text = stringResource(
                    id = R.string.by,
                    post.author?.firstname ?: "",
                    post.author?.lastname ?: ""
                ),
                modifier = Modifier.padding(bottom = SharedPadding.xs)
            )
            TextTitleMedium(text = post.title)
            Spacer(Modifier.padding(SharedPadding.small))
            if (!post.photoUrl.isNullOrEmpty()) {
                SharedAsyncImage(
                    photoUri = post.photoUrl,
                    modifier = Modifier
                        .padding(bottom = SharedPadding.xs)
                        .heightIn(max = 200.dp),
                    imageModifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ratio = 16 / 9f),
                )
            }
            if (!post.description.isNullOrEmpty()) {
                Spacer(Modifier.padding(SharedPadding.small))
                Text(
                    text = post.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}