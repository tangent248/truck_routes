package com.group_7.truck_routes.model

data class PostRequest(
    val computeAlternativeRoutes: Boolean,
    val destination: Destination,
    val languageCode: String,
    val origin: Origin,
    val routeModifiers: RouteModifiers,
    val routingPreference: String,
    val travelMode: String,
    val units: String
)

