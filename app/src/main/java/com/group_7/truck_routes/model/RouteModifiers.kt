package com.group_7.truck_routes.model

data class RouteModifiers(
    val avoidFerries: Boolean,
    val avoidHighways: Boolean,
    val avoidTolls: Boolean
)