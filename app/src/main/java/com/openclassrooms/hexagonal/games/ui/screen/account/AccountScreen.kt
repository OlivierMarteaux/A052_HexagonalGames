package com.openclassrooms.hexagonal.games.ui.screen.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.localShared.ui.theme.SharedPadding
import com.oliviermarteaux.shared.composables.IconScaffold
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.openclassrooms.hexagonal.games.R

/**
 * A screen for managing the user's account.
 *
 * @param modifier The modifier to apply to this screen.
 * @param navigateBack A function to call to navigate back to the previous screen.
 * @param accountViewModel The view model for this screen.
 */
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
                    modifier = modifier
                        .padding(contentPadding)
                        .padding(horizontal = SharedPadding.xl)
                        .fillMaxSize(),
                    signOut = ::signOut,
                    deleteAccount = ::deleteAccount,
                    navigateBack = navigateBack
                )
                if (unknownError) SharedToast(stringResource(R.string.application_error_unknown))
            }
        }
    }
}

/**
 * A composable for the body of the account screen.
 *
 * @param modifier The modifier to apply to this composable.
 * @param signOut A function to call to sign out the user.
 * @param deleteAccount A function to call to delete the user's account.
 * @param navigateBack A function to call to navigate back to the previous screen.
 */
@Composable
private fun AccountBody(
    modifier: Modifier = Modifier,
    signOut: (() -> Unit) -> Unit,
    deleteAccount: (() -> Unit) -> Unit,
    navigateBack: () -> Unit,
) {
    IconScaffold(modifier = modifier) {
        SharedButton(text = stringResource(R.string.sign_out)) { signOut(navigateBack) }
        SharedButton(text = stringResource(R.string.delete_account)) { deleteAccount(navigateBack) }
    }
}