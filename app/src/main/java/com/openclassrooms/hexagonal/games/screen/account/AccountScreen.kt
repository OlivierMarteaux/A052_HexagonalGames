package com.openclassrooms.hexagonal.games.screen.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.shared.composables.SharedButton
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.HexagonalGamesScaffold

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    accountViewModel: AccountViewModel = hiltViewModel(),
    navigateToSplashScreen: () -> Unit,
){

    HexagonalGamesScaffold(
        modifier = modifier,
        title = stringResource(R.string.my_account),
    ){ contentPadding ->
        AccountBody(
            modifier = modifier.padding(contentPadding),
            signOut = accountViewModel::signOut,
            deleteAccount = accountViewModel::deleteAccount,
            navigateToSplashScreen = navigateToSplashScreen
        )
    }
}

@Composable
private fun AccountBody(
    modifier: Modifier = Modifier,
    signOut: (() -> Unit) -> Unit,
    deleteAccount: (() -> Unit) -> Unit,
    navigateToSplashScreen: () -> Unit,
) {
    Column(modifier = modifier) {
        SharedButton(text = stringResource(R.string.sign_out)) { signOut(navigateToSplashScreen) }
        SharedButton(text = stringResource(R.string.delete_account)) { deleteAccount(navigateToSplashScreen) }
    }
}