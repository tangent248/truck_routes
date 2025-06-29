package com.group_7.truck_routes.screens

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.group_7.truck_routes.ApiService
import com.group_7.truck_routes.RetrofitInstance
import com.group_7.truck_routes.model.Destination
import com.group_7.truck_routes.model.LatLng
import com.group_7.truck_routes.model.Location
import com.group_7.truck_routes.model.Origin
import com.group_7.truck_routes.model.PostRequest
import com.group_7.truck_routes.model.Route
import com.group_7.truck_routes.model.RouteModifiers
import com.group_7.truck_routes.viewModel.MapViewModel

@Composable
fun Maps(mapViewModel: MapViewModel, startPoint: String, destination: String, route: String) {
    // Initialize the camera position state, which controls the camera's position on the map
    val cameraPositionState = rememberCameraPositionState()
    // Obtain the current context
    val context = LocalContext.current
    // Observe the user's location from the ViewModel
    val userLocation by mapViewModel.userLocation
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val apiService: ApiService by lazy { RetrofitInstance.getApiService() }

    var postsSpeed by remember { mutableStateOf<List<Route>>(emptyList()) }
    var postsMileage by remember { mutableStateOf<List<Route>>(emptyList()) }
    var postsAvoidToll by remember { mutableStateOf<List<Route>>(emptyList()) }

    var polylinePoints by remember { mutableStateOf<List<com.google.android.gms.maps.model.LatLng>>(emptyList()) }


    //converts string to lat long
    fun stringToLocation(input: String): Location? {
        return try {
            val parts = input.split(",")
            val lat = parts[0].trim().toDouble()
            val lng = parts[1].trim().toDouble()
            Location(LatLng(lat, lng))
        } catch (e: Exception) {
            null
        }
    }


    LaunchedEffect(Unit) {

        val originLocation = stringToLocation(startPoint)
        val destinationLocation = stringToLocation(destination)


        if (originLocation == null || destinationLocation == null) {
            Toast.makeText(context, "Invalid coordinates", Toast.LENGTH_LONG).show()
            return@LaunchedEffect
        }

        val routeRequestSpeed = PostRequest(
            origin = Origin(location = originLocation),
            destination = Destination(location = destinationLocation),
            travelMode = "DRIVE",
            routingPreference = "TRAFFIC_AWARE_OPTIMAL",
            computeAlternativeRoutes = true,
            routeModifiers = RouteModifiers(
                avoidTolls = false,
                avoidHighways = false,
                avoidFerries = true
            ),
            languageCode = "en-US",
            units = "METRIC"
        )
        val routeRequestMileage = PostRequest(
            origin = Origin(location = originLocation),
            destination = Destination(location = destinationLocation),
            travelMode = "DRIVE",
            routingPreference = "TRAFFIC_AWARE",
            computeAlternativeRoutes = true,
            routeModifiers = RouteModifiers(
                avoidTolls = false,
                avoidHighways = false,
                avoidFerries = true
            ),
            languageCode = "en-US",
            units = "METRIC"
        )
        val routeRequestAvoidToll = PostRequest(
            origin = Origin(location = originLocation),
            destination = Destination(location = destinationLocation),
            travelMode = "DRIVE",
            routingPreference = "TRAFFIC_AWARE",
            computeAlternativeRoutes = true,
            routeModifiers = RouteModifiers(
                avoidTolls = true,
                avoidHighways = false,
                avoidFerries = false
            ),
            languageCode = "en-US",
            units = "METRIC"
        )

        try {
            val responseSpeed = apiService.getRoutes(routeRequestSpeed)

            val responseMileage = apiService.getRoutes(routeRequestMileage)

            val responseAvoidToll = apiService.getRoutes(routeRequestAvoidToll)

            postsSpeed = responseSpeed.routes
            postsMileage = responseMileage.routes
            postsAvoidToll = responseAvoidToll.routes

            val routePreference = when (route) {
                "speed" -> responseSpeed
                "mileage" -> responseMileage
                "avoidToll" -> responseAvoidToll
                else -> responseSpeed
            }

            val encodedPolyline = routePreference.routes.firstOrNull()?.polyline?.encodedPolyline
            if (encodedPolyline != null) {
                polylinePoints = PolyUtil.decode(encodedPolyline)
            }

        } catch (e: Exception) {
            Toast.makeText(context, "API Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }




    // Handle permission requests for accessing fine location
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Fetch the user's location and update the camera if permission is granted
            mapViewModel.fetchUserLocation(context, fusedLocationClient)
        } else {
            // Handle the case when permission is denied
            Toast.makeText(
                context,
                "Location permission was denied by the user",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

// Request the location permission when the composable is launched
    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            // Check if the location permission is already granted
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                // Fetch the user's location and update the camera
                mapViewModel.fetchUserLocation(context, fusedLocationClient)
            }

            else -> {
                // Request the location permission if it has not been granted
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
// Display the Google Map
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // If the user's location is available, place a marker on the map
        userLocation?.let {
            Marker(
                state = MarkerState(position = it), // Place the marker at the user's location
                title = "Your Location", // Set the title for the marker
                snippet = "This is where you are currently located." // Set the snippet for the marker
            )
            // Move the camera to the user's location with a zoom level of 10f
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 10f)
        }
        if (polylinePoints.isNotEmpty()) {
            Polyline(
                points = polylinePoints,
                color = androidx.compose.ui.graphics.Color.Blue,
                width = 5f
            )
        }
    }
}