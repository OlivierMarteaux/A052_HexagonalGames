package com.openclassrooms.hexagonal.games.screen.homefeed

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestoreException
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.composables.TriggeredToast
import com.oliviermarteaux.shared.ui.UiState
import com.oliviermarteaux.shared.utils.isOnline
import com.oliviermarteaux.utils.TOAST_DURATION
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme
import java.io.IOException

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
  var showMenu by rememberSaveable { mutableStateOf(false) }

  Scaffold(
    modifier = modifier,
    topBar = {
      TopAppBar(
        title = {
          Text(stringResource(id = R.string.homefeed_fragment_label))
        },
        actions = {
          IconButton(onClick = { showMenu = !showMenu }) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = stringResource(id = R.string.contentDescription_more)
            )
          }
          DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
          ) {
            DropdownMenuItem(
              onClick = {
                onSettingsClick()
                showMenu = false
              },
              text = {
                Text(
                  text = stringResource(id = R.string.action_settings)
                )
              }
            )
            DropdownMenuItem(
              onClick = {
                viewModel.onAccountClick(
                  onUserLogged = navigateToAccount,
                  onNoUserLogged = navigateToLogin
                )
                showMenu = false
              },
              text = {
                Text(
                  text = stringResource(id = R.string.my_account)
                )
              }
            )
          }
        }
      )
    },
    floatingActionButtonPosition = FabPosition.End,
    floatingActionButton = {
      FloatingActionButton(
        onClick = {
          if (viewModel.currentUser != null) {
            Log.d("OM_TAG", "HomeFeedScreen: onClick: Navigate to add post")
            navigateToAddPost()
          } else {
            Log.d("OM_TAG", "HomeFeedScreen: onClick: Show log toast")
            viewModel.showLogToast()
          }
        }
      ) {
        Icon(
          imageVector = Icons.Filled.Add,
          contentDescription = stringResource(id = R.string.description_button_add)
        )
      }
    }
  ) { contentPadding ->
    val homeFeedUiState: UiState<Post> = viewModel.homeFeedUiState
    LaunchedEffect(homeFeedUiState){
      Log.i("OM_TAG", "HomeFeedViewModel: LaunchedEffect: homeFeedUiState = $homeFeedUiState")
    }

      Box(){
        //_ UiState management: Empty, Error, Loading, Success
        when (homeFeedUiState) {
          is UiState.Empty -> SharedToast(
            text = stringResource(R.string.homefeed_empty_state),
            durationMillis = TOAST_DURATION
          )
          is UiState.Error -> {
            val error = homeFeedUiState.throwable
            val errorMessage = when (error) {
              is FirebaseFirestoreException -> stringResource(
                R.string.homefeed_error_database,
                error.message ?: ""
              )

              is IOException -> stringResource(R.string.homefeed_error_network)
              else -> stringResource(
                R.string.homefeed_error_unknown,
                error?.localizedMessage ?: "Unknown error"
              )
            }
            SharedToast(
              text = errorMessage,
              bottomPadding = 160,
              durationMillis = TOAST_DURATION
            )
          }

          is UiState.Loading ->
            Column(
              modifier = modifier
                .fillMaxSize()
                .padding(contentPadding),
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              CircularProgressIndicator()
            }
          is UiState.Success -> {
            val posts = homeFeedUiState.data
            HomeFeedList(
              modifier = modifier.padding(contentPadding),
              posts = posts,
              onPostClick = onPostClick
            )
          }
        }
        //_ No user logged error toast
        TriggeredToast(
          trigger = viewModel.showLogToast,
          text = stringResource(R.string.homefeed_error_no_user_logged),
          bottomPadding = 120
        )
        //_ No network error toast
        val context = LocalContext.current
        TriggeredToast(
          trigger = !isOnline(context),
          text = stringResource(R.string.homefeed_error_network),
          bottomPadding = 160
        )
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

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun HomeFeedCellPreview() {
  HexagonalGamesTheme {
    HomeFeedCell(
      post = Post(
        id = "1",
        title = "title",
        description = "description",
        photoUrl = null,
        timestamp = 1,
        author = User(
          id = "1",
          firstname = "firstname",
          lastname = "lastname",
          email = "email"
        )
      ),
      onPostClick = {}
    )
  }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun HomeFeedCellImagePreview() {
  HexagonalGamesTheme {
    HomeFeedCell(
      post = Post(
        id = "1",
        title = "title",
        description = null,
        photoUrl = "https://picsum.photos/id/85/1080/",
        timestamp = 1,
        author = User(
          id = "1",
          firstname = "firstname",
          lastname = "lastname",
          email = "email"
        )
      ),
      onPostClick = {}
    )
  }
}