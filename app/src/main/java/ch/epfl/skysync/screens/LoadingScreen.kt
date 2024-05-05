package ch.epfl.skysync.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.viewmodel.FlightsViewModel

@Composable
fun LoadingScreen(navController: NavHostController, viewModel: FlightsViewModel) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    if(user == null) {
        LoadingComponent(isLoading = true, onRefresh = { /*TODO*/ }) {}
    }
    else{
        if(user is Admin){
            navController.navigate(Route.ADMIN)
        }
        else{
            navController.navigate(Route.CREW_PILOT)
        }
    }


}