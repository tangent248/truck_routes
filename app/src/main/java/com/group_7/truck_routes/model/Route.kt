package com.group_7.truck_routes.model

data class Route(
    val distanceMeters: Int,
    val duration: String,
    val polyline: Polyline
)