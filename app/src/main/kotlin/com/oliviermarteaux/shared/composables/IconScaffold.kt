package com.oliviermarteaux.shared.composables
//
//import android.content.res.Configuration
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import com.openclassrooms.hexagonal.games.R
//
///**
// * A scaffold that displays an icon and some content.
// *
// * @param modifier The modifier to apply to this scaffold.
// * @param verticalArrangement The vertical arrangement of the content.
// * @param content The content to display.
// */
//@Composable
//fun IconScaffold(
//    modifier: Modifier = Modifier,
//    verticalArrangement: Arrangement.Vertical = Arrangement.SpaceEvenly,
//    content: @Composable () -> Unit
//) {
//    val configuration = LocalConfiguration.current
//    val orientation = configuration.orientation
//
//    Column(
//        modifier = modifier,
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//            SharedIcon(
//                modifier = Modifier
//                    .padding(vertical = 50.dp)
//                    .size(200.dp),
//                icon = IconSource.PainterIcon(painterResource(R.drawable.hexagonal_games_logo)),
//            )
//        }
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = verticalArrangement,
//            modifier = Modifier.fillMaxSize()
//        ) { content() }
//    }
//}