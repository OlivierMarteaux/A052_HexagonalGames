package com.openclassrooms.hexagonal.games.ui.screen.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.localShared.composables.SharedIcon
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.ui.theme.SharedPadding
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.screen.settings.IconScaffold

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
                        .padding(horizontal = SharedPadding.xxl)
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

@Composable
private fun AccountBody(
    modifier: Modifier = Modifier,
    signOut: (() -> Unit) -> Unit,
    deleteAccount: (() -> Unit) -> Unit,
    navigateBack: () -> Unit,
) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.SpaceEvenly,
//        modifier = modifier
//    ) {
//        SharedIcon(
//            modifier = Modifier.size(200.dp),
//            painter = painterResource(R.drawable.hexagonal_games_logo),
//        )
    IconScaffold(modifier = modifier) {
        SharedButton(text = stringResource(R.string.sign_out)) { signOut(navigateBack) }
        SharedButton(text = stringResource(R.string.delete_account)) { deleteAccount(navigateBack) }
    }
}