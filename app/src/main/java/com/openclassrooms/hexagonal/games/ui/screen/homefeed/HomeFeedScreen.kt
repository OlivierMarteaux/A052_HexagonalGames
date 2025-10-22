package com.openclassrooms.hexagonal.games.ui.screen.homefeed

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.oliviermarteaux.shared.composables.CenteredCircularProgressIndicator
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.ui.UiState
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
            onMenuItem2Click = { checkUserState(
                onUserLogged = navigateToAccount,
                onNoUserLogged = navigateToLogin
            )},
            menuItem2Title = stringResource(id = R.string.my_account),
            onFabClick = { checkUserState(
                onUserLogged = navigateToAddPost,
                onNoUserLogged = ::showAuthErrorToast
            )}
        ) { contentPadding ->
            LaunchedEffect(homeFeedUiState) {
              Log.i("OM_TAG", "HomeFeedViewModel: LaunchedEffect: homeFeedUiState = $homeFeedUiState")
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
                      modifier = modifier.padding(contentPadding),
                      posts = (homeFeedUiState as UiState.Success<Post>).data,
                      onPostClick = onPostClick
                    )
                  }
                }
                if(authError) SharedToast(
                    text = stringResource(R.string.user_disconnected),
                    bottomPadding = 120
                )
                if(networkError) SharedToast(
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
    modifier = modifier.padding(8.dp),
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
    modifier = Modifier.fillMaxWidth(),
    onClick = {
      onPostClick(post)
    }) {
    Column(
      modifier = Modifier.padding(8.dp),
    ) {
      Text(
        text = stringResource(
          id = R.string.by,
          post.author?.firstname ?: "",
          post.author?.lastname ?: ""
        ),
        style = MaterialTheme.typography.titleSmall
      )
      Text(
        text = post.title,
        style = MaterialTheme.typography.titleLarge
      )
      if (!post.photoUrl.isNullOrEmpty()) {
        AsyncImage(
          modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .heightIn(max = 200.dp)
            .aspectRatio(ratio = 16 / 9f),
          model = post.photoUrl,
          placeholder = ColorPainter(Color.DarkGray),
          contentDescription = "image",
          contentScale = ContentScale.Crop,
        )
      }
      if (!post.description.isNullOrEmpty()) {
        Text(
          text = post.description,
          style = MaterialTheme.typography.bodyMedium
        )
      }
    }
  }
}