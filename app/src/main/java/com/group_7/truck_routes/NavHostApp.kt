package com.group_7.truck_routes

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.group_7.truck_routes.Routs.Home



@Composable
fun NavHostApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Home) {
        composable<Routs.Home>
        { Home(navController) }
        composable<Routs.Maps> {
            val data = it.toRoute<Routs.Maps>()
            Maps(navController,startPoint = data.startpoint,destination = data.destination,route = data.route) }

    }
}
