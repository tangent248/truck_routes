package com.group_7.truck_routes.screens

import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button

import androidx.compose.material.icons.filled.Close

import androidx.compose.material3.Icon

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.*
import com.group_7.truck_routes.ApiService
import com.group_7.truck_routes.R
import com.group_7.truck_routes.RetrofitInstance
import com.group_7.truck_routes.model.*
import com.group_7.truck_routes.viewModel.MapViewModel
import com.google.android.gms.maps.model.LatLng as GmsLatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory

@Composable
fun Maps(mapViewModel: MapViewModel, startPoint: String, destination: String, route: String) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val apiService: ApiService by lazy { RetrofitInstance.getApiService() }

    val userLocation by mapViewModel.userLocation
    val bearing by mapViewModel.bearing

    var postsSpeed by remember { mutableStateOf<List<Route>>(emptyList()) }
    var postsSpeedToStartPoint by remember { mutableStateOf<List<Route>>(emptyList()) }
    var postsMileage by remember { mutableStateOf<List<Route>>(emptyList()) }
    var postsMileageToStartPoint by remember { mutableStateOf<List<Route>>(emptyList()) }
    var postsAvoidToll by remember { mutableStateOf<List<Route>>(emptyList()) }
    var postsAvoidTollToStartPoint by remember { mutableStateOf<List<Route>>(emptyList()) }

    var polylinePoints by remember { mutableStateOf<List<GmsLatLng>>(emptyList()) }
    var polylinePointsToStartPoint by remember { mutableStateOf<List<GmsLatLng>>(emptyList()) }

    var isRotationEnabled by remember { mutableStateOf(false) }


    // Helper function to convert a "lat,lng" string to Location object
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
        mapViewModel.startCompass(context)
    }

    // Request user location permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            mapViewModel.fetchUserLocation(context, fusedLocationClient)
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            mapViewModel.startUpdatingLocationPeriodically(context, fusedLocationClient)
        } else {
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(userLocation) {
        val originLocation = stringToLocation(startPoint)
        val destinationLocation = stringToLocation(destination)

        if (originLocation == null || destinationLocation == null || userLocation == null) {
            Log.e("MapsScreen", "Invalid or missing coordinates")
            return@LaunchedEffect
        }

        val userLocWrapped = Location(LatLng(userLocation!!.latitude, userLocation!!.longitude))

        val routeRequestSpeed = PostRequest(
            origin = Origin(originLocation),
            destination = Destination(destinationLocation),
            travelMode = "DRIVE",
            routingPreference = "TRAFFIC_AWARE_OPTIMAL",
            computeAlternativeRoutes = true,
            routeModifiers = RouteModifiers(false, false, true),
            languageCode = "en-US",
            units = "METRIC"
        )

        val routeRequestSpeedToStartPoint = PostRequest(
            origin = Origin(userLocWrapped),
            destination = Destination(originLocation),
            travelMode = "DRIVE",
            routingPreference = "TRAFFIC_AWARE_OPTIMAL",
            computeAlternativeRoutes = true,
            routeModifiers = RouteModifiers(false, false, true),
            languageCode = "en-US",
            units = "METRIC"
        )

        val routeRequestMileage = routeRequestSpeed.copy(routingPreference = "TRAFFIC_AWARE")
        val routeRequestMileageToStartPoint = routeRequestSpeedToStartPoint.copy(routingPreference = "TRAFFIC_AWARE")

        val routeRequestAvoidToll = routeRequestSpeed.copy(
            routeModifiers = RouteModifiers(true, false, false)
        )
        val routeRequestAvoidTollToStartPoint = routeRequestSpeedToStartPoint.copy(
            routeModifiers = RouteModifiers(true, false, false)
        )

        try {
            val responseSpeed = apiService.getRoutes(routeRequestSpeed)
            val responseSpeedToStart = apiService.getRoutes(routeRequestSpeedToStartPoint)
            val responseMileage = apiService.getRoutes(routeRequestMileage)
            val responseMileageToStart = apiService.getRoutes(routeRequestMileageToStartPoint)
            val responseAvoidToll = apiService.getRoutes(routeRequestAvoidToll)
            val responseAvoidTollToStart = apiService.getRoutes(routeRequestAvoidTollToStartPoint)

            postsSpeed = responseSpeed.routes
            postsSpeedToStartPoint = responseSpeedToStart.routes
            postsMileage = responseMileage.routes
            postsMileageToStartPoint = responseMileageToStart.routes
            postsAvoidToll = responseAvoidToll.routes
            postsAvoidTollToStartPoint = responseAvoidTollToStart.routes

            val routeSelected = when (route) {
                "speed" -> responseSpeed
                "mileage" -> responseMileage
                "avoidToll" -> responseAvoidToll
                else -> responseSpeed
            }

            val routeToStartSelected = when (route) {
                "speed" -> responseSpeedToStart
                "mileage" -> responseMileageToStart
                "avoidToll" -> responseAvoidTollToStart
                else -> responseSpeedToStart
            }

            routeSelected.routes.firstOrNull()?.polyline?.encodedPolyline?.let {
                polylinePoints = PolyUtil.decode(it)
            }

            routeToStartSelected.routes.firstOrNull()?.polyline?.encodedPolyline?.let {
                polylinePointsToStartPoint = PolyUtil.decode(it)
            }

        } catch (e: Exception) {
            Toast.makeText(context, "Route API error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            userLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Your Location",
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.arrow),
                    rotation = if (isRotationEnabled) bearing else 0f,
                    flat = true
                )
                cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 18f)
            }

            stringToLocation(destination)?.latLng?.let {
                Marker(
                    state = MarkerState(position = GmsLatLng(it.latitude, it.longitude)),
                    title = "Destination",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
            }

            if (polylinePoints.isNotEmpty()) {
                Polyline(
                    points = polylinePoints,
                    color = androidx.compose.ui.graphics.Color.Blue,
                    width = 5f
                )
            }

            if (polylinePointsToStartPoint.isNotEmpty()) {
                Polyline(
                    points = polylinePointsToStartPoint,
                    color = androidx.compose.ui.graphics.Color.Red,
                    width = 5f
                )
            }
        }
        Button(
            onClick = { isRotationEnabled = !isRotationEnabled },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(60.dp)
        ) {
            Icon(
                imageVector = if (isRotationEnabled) Icons.Default.Close else Icons.Default.Check,
                contentDescription = if (isRotationEnabled) "Freeform Mode" else "Center Mode"
            )
        }
    }
}
