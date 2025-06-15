package com.group_7.truck_routes

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.group_7.truck_routes.Screens.Home
import com.group_7.truck_routes.routs.Home
import com.group_7.truck_routes.Screens.Maps


@Composable
fun NavHostApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = routs.Home) {
        composable<routs.Home>
        {Home(navController)}

        composable<routs.Maps> {
            val data = it.toRoute<routs.Maps>()
            Maps(
                navController,
                startPoint = data.startPoint,
                destination = data.destination,
                route = data.route
            )
        }

    }
}
