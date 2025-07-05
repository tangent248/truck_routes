package com.group_7.truck_routes.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.group_7.truck_routes.ApiService
import com.group_7.truck_routes.RetrofitInstance
import com.group_7.truck_routes.Routs
import com.group_7.truck_routes.model.Destination
import com.group_7.truck_routes.model.LatLng
import com.group_7.truck_routes.model.Location
import com.group_7.truck_routes.model.Origin
import com.group_7.truck_routes.model.PostRequest
import com.group_7.truck_routes.model.Route
import com.group_7.truck_routes.model.RouteModifiers
import kotlinx.coroutines.tasks.await


@Composable
fun RouteSelectionScreen(navController: NavController,
                         startPoint: String,
                         destination: String,
                         loadTons: Double) {

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val apiService = remember { RetrofitInstance.getApiService() }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var routes by remember { mutableStateOf<Map<String, Route?>>(emptyMap()) }

    var startAddress by remember { mutableStateOf<String?>(null) }
    var destinationAddress by remember { mutableStateOf<String?>(null) }

     fun reverseGeocode(context: Context, latLng: LatLng): String? {
        return try {
            val geocoder = android.location.Geocoder(context)
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            val address = addresses?.firstOrNull()

            // Use locality (e.g., "Pune") or subAdminArea fallback
            address?.locality ?: address?.subAdminArea ?: address?.adminArea
        } catch (e: Exception) {
            null
        }
    }

    // Convert to LatLng
    fun stringToLatLng(input: String): LatLng? {
        return try {
            val parts = input.split(",")
            LatLng(parts[0].trim().toDouble(), parts[1].trim().toDouble())
        } catch (e: Exception) {
            null
        }
    }

    fun calculateCost(distanceMeters: Int): String {
        val km = distanceMeters / 1000.0
        val costPerKgPerKm = 8.0 / 1000.0
        val loadKg = loadTons * 1000
        val cost = costPerKgPerKm * loadKg * km
        return "₹${String.format("%.2f", cost)}"
    }


    // Fetch user location and routes
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val start = stringToLatLng(startPoint)
            val end = stringToLatLng(destination)

            if (start != null && end != null) {
                try {
                    startAddress = reverseGeocode(context, start)
                    destinationAddress = reverseGeocode(context, end)

                    val loc = fusedLocationClient.lastLocation.await()
                    val userLatLng = LatLng(loc.latitude, loc.longitude)
                    userLocation = userLatLng

                    val (speed, mileage, toll) = fetchRouteOptions(
                        apiService,
                        userLatLng,
                        start,
                        end
                    )

                    routes = mapOf(
                        "speed" to speed.firstOrNull(),
                        "mileage" to mileage.firstOrNull(),
                        "toll" to toll.firstOrNull()
                    )
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Error fetching routes: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Text(
                text = "${startAddress ?: "Start"} 🚛 ➡️ ${destinationAddress ?: "Destination"}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        item {
            Text(
                text = "Choose your preferred route option",
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            RouteCard(
                title = "Highway Express",
                time = routes["speed"]?.let { formatDuration(it.duration) } ?: "Loading...",
                distance = routes["speed"]?.let { "${it.distanceMeters / 1000.0} km" }
                    ?: "Loading...",
                cost = routes["speed"]?.let { calculateCost(it.distanceMeters) } ?: "Loading...",
                description = "Fastest route for delivery trucks using major highways with current traffic conditions",
                tag = "Fastest",
                tagColor = Color(0xFF0DCAF0),
                onClick = {
                    navController.navigate(
                        Routs.Maps(
                            startPoint = startPoint,
                            destination = destination,
                            route = "speed"
                        )
                    )
                }
            )
        }

        item {
            RouteCard(
                title = "Local Delivery Route",
                time = routes["mileage"]?.let { formatDuration(it.duration) } ?: "Loading...",
                distance = routes["mileage"]?.let { "${it.distanceMeters / 1000.0} km" }
                    ?: "Loading...",
                cost = routes["mileage"]?.let { calculateCost(it.distanceMeters) } ?: "Loading...",
                description = "Shorter distance through local roads, perfect for smaller delivery vehicles",
                tag = "Shortest",
                tagColor = Color(0xFF0DCAF0),
                onClick = {
                    navController.navigate(
                        Routs.Maps(
                            startPoint = startPoint,
                            destination = destination,
                            route = "mileage"
                        )
                    )
                }
            )
        }

        item {
            RouteCard(
                title = "Commercial Route",
                time = routes["toll"]?.let { formatDuration(it.duration) } ?: "Loading...",
                distance = routes["toll"]?.let { "${it.distanceMeters / 1000.0} km" }
                    ?: "Loading...",
                cost = routes["toll"]?.let { calculateCost(it.distanceMeters) } ?: "Loading...",
                description = "Truck-friendly route avoiding toll roads and weight restrictions",
                tag = "Commercial",
                tagColor = Color(0xFF0DCAF0),
                onClick = {
                    navController.navigate(
                        Routs.Maps(
                            startPoint = startPoint,
                            destination = destination,
                            route = "toll"
                        )
                    )
                }
            )
        }
    }
}

fun formatDuration(duration: String): String {
    val seconds = duration.replace("s", "").toIntOrNull() ?: return "?"
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}

suspend fun fetchRouteOptions(
    apiService: ApiService,
    userLoc: LatLng,
    startLoc: LatLng,
    destLoc: LatLng
): Triple<List<Route>, List<Route>, List<Route>> {
    Location(userLoc)
    val startPoint = Location(startLoc)
    val destination = Location(destLoc)

    val requestSpeed = PostRequest(
        origin = Origin(startPoint),
        destination = Destination(destination),
        travelMode = "DRIVE",
        routingPreference = "TRAFFIC_AWARE_OPTIMAL",
        computeAlternativeRoutes = true,
        routeModifiers = RouteModifiers(false, false, false),
        languageCode = "en-US",
        units = "METRIC"
    )
    val requestMileage = PostRequest(
        origin = Origin(startPoint),
        destination = Destination(destination),
        travelMode = "DRIVE",
        routingPreference = "TRAFFIC_AWARE",
        computeAlternativeRoutes = false,
        routeModifiers = RouteModifiers(false, true, false),
        languageCode = "en-US",
        units = "METRIC"
    )
    val requestTollFree = PostRequest(
        origin = Origin(startPoint),
        destination = Destination(destination),
        travelMode = "DRIVE",
        routingPreference = "TRAFFIC_AWARE",
        computeAlternativeRoutes = false,
        routeModifiers = RouteModifiers(false, false, true),
        languageCode = "en-US",
        units = "METRIC"
    )

    val speedRoute = apiService.getRoutes(requestSpeed).routes
    val mileageRoute = apiService.getRoutes(requestMileage).routes
    val tollRoute = apiService.getRoutes(requestTollFree).routes

    return Triple(speedRoute, mileageRoute, tollRoute)
}


@Composable
fun RouteCard(
    title: String,
    time: String,
    distance: String,
    cost: String,
    description: String,
    tag: String? = null,
    tagColor: Color = Color.Blue,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Title Row with Optional Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

                tag?.let {
                    Box(
                        modifier = Modifier
                            .background(tagColor, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = it, color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time and Distance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = time,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF007BFF)
                    )
                    Text(text = "Duration", fontSize = 12.sp)
                }
                Column {
                    Text(
                        text = distance,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF007BFF)
                    )
                    Text(text = "Distance", fontSize = 12.sp)
                }
                Column {
                    Text(
                        text = cost,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF007BFF)
                    )
                    Text(text = "Est. Fuel Cost", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(thickness = 2.dp, color = Color(0xFF00CFFF))

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(text = description, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
            ) {
                Text("Select Route", color = Color.White)
            }
        }
    }
}