package com.openclassrooms.hexagonal.games.ui.screen.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.composables.TriggeredToast
import com.openclassrooms.hexagonal.games.R

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    accountViewModel: AccountViewModel = hiltViewModel(),
){
    SharedScaffold(
        modifier = modifier,
        title = stringResource(R.string.my_account),
        onBackClick = navigateBack
    ){ contentPadding ->
        with (accountViewModel) {
            Box {
                AccountBody(
                    modifier = modifier.padding(contentPadding),
                    signOut = ::signOut,
                    deleteAccount = ::deleteAccount,
                    navigateBack = navigateBack
                )
                if (unknownError) SharedToast(stringResource(R.string.application_error_unknown))
            }
        }
    }
}

@Composable
private fun AccountBody(
    modifier: Modifier = Modifier,
    signOut: (() -> Unit) -> Unit,
    deleteAccount: (() -> Unit) -> Unit,
    navigateBack: () -> Unit,
) {
    Column(modifier = modifier) {
        SharedButton(text = stringResource(R.string.sign_out)) { signOut(navigateBack) }
        SharedButton(text = stringResource(R.string.delete_account)) { deleteAccount(navigateBack) }
    }
}