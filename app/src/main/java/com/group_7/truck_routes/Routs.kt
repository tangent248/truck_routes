package com.group_7.truck_routes

import kotlinx.serialization.Serializable

sealed class Routs {
    @Serializable
    object Home

    @Serializable
    object Loginscreen

    @Serializable
    object Registerscreen

    @Serializable
    data class RouteSelectionScreen(
        val startPoint: String,
        val destination: String,
        val loadTons: Double
    )

    @Serializable
    data class Maps(
        val startPoint: String,
        val destination: String,
        val route: String
    )
}