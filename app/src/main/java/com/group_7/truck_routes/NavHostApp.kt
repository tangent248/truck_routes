package com.group_7.truck_routes

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.group_7.truck_routes.screens.Home
import com.group_7.truck_routes.screens.Loginscreen
import com.group_7.truck_routes.screens.Maps
import com.group_7.truck_routes.screens.Registerscreen
import com.group_7.truck_routes.screens.RouteSelectionScreen
import com.group_7.truck_routes.viewModel.MapViewModel


@Composable
fun NavHostApp() {
    val navController = rememberNavController()
    val mapViewModel = viewModel<MapViewModel>()

    NavHost(navController = navController, startDestination = Routs.Loginscreen) {

        composable<Routs.Home>
        { Home(
            mapViewModel = mapViewModel,
            navController = navController
        ) }

        composable<Routs.Loginscreen>
        { Loginscreen(navController) }

        composable<Routs.Registerscreen>
        { Registerscreen(navController) }

        composable<Routs.RouteSelectionScreen> {
            val data = it.toRoute<Routs.RouteSelectionScreen>()
            RouteSelectionScreen(
                navController,
                startPoint = data.startPoint,
                destination = data.destination,
                loadTons = data.loadTons
            )
        }

        composable<Routs.Maps> {
            val data = it.toRoute<Routs.Maps>()

            Maps(
                mapViewModel = mapViewModel,
                startPoint = data.startPoint,
                destination = data.destination,
                route = data.route
            )
        }

    }
}
