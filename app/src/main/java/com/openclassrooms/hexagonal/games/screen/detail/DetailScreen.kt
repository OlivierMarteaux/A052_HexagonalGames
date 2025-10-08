package com.openclassrooms.hexagonal.games.screen.detail

import android.R.attr.password
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oliviermarteaux.shared.composables.SharedAsyncImage
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedOutlinedTextField
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.screen.password.PasswordViewModel
import com.openclassrooms.hexagonal.games.ui.HexagonalGamesScaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    detailViewModel: DetailViewModel = hiltViewModel()
){
//    val post = detailViewModel.post
    val post by detailViewModel.post.collectAsStateWithLifecycle()
    HexagonalGamesScaffold(
        modifier = modifier,
        title = post.title,
    ){ contentPadding ->
        DetailBody(
            post = post,
            modifier = modifier.padding(contentPadding),
        )
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
        }
    }
}