package com.group_7.truck_routes

import com.group_7.truck_routes.model.PostRequest
import com.group_7.truck_routes.model.PostResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers(
        "X-Goog-Api-Key: AIzaSyAGX8CifFJ8j8GChyKgNd3TJcXYDq_Ix4A",
        "X-Goog-FieldMask: routes.duration,routes.distanceMeters,routes.polyline"
    )
    @POST("directions/v2:computeRoutes")
    suspend fun getRoutes(@Body request: PostRequest): PostResponse
}