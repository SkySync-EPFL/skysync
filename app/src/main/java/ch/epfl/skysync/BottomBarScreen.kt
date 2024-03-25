package ch.epfl.skysync

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Email
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    @DrawableRes val icon: Int
) {
    object Home : BottomBarScreen(
        route = "home",
        title = "Home",
        icon = R.drawable.baseline_home_24
    )

    object Flight : BottomBarScreen(
        route = "flight",
        title = "Flight",
        icon = R.drawable.baseline_flight_24
    )

    object Chat : BottomBarScreen(
        route = "chat",
        title = "Chat",
        icon = R.drawable.baseline_chat_24
    )

    object Calendar : BottomBarScreen(
        route = "calendar",
        title = "Calendar",
        icon = R.drawable.baseline_calendar_month_24
    )
}