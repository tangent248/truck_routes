package com.group_7.truck_routes

import kotlinx.serialization.Serializable

sealed class Routs {
    @Serializable
    object Home

    @Serializable
    data class Maps(
        val startPoint: String,
        val destination: String,
        val route: String
    )
}