package com.group_7.truck_routes.screens


import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button

import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.*
import com.group_7.truck_routes.ApiService
import com.group_7.truck_routes.R
import com.group_7.truck_routes.RetrofitInstance
import com.group_7.truck_routes.model.*
import com.group_7.truck_routes.viewModel.MapViewModel
import com.google.android.gms.maps.model.LatLng as GmsLatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory

@OptIn(ExperimentalMaterial3Api::class)
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

//    var isRotationEnabled by remember { mutableStateOf(false) }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    var selectedRouteInfo by remember { mutableStateOf<Route?>(null) }




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
            routeModifiers = RouteModifiers(false, false, false),
            languageCode = "en-US",
            units = "METRIC"
        )

        val routeRequestSpeedToStartPoint = PostRequest(
            origin = Origin(userLocWrapped),
            destination = Destination(originLocation),
            travelMode = "DRIVE",
            routingPreference = "TRAFFIC_AWARE_OPTIMAL",
            computeAlternativeRoutes = true,
            routeModifiers = RouteModifiers(false, false, false),
            languageCode = "en-US",
            units = "METRIC"
        )
        val routeRequestMileage = PostRequest(
            origin = Origin(originLocation),
            destination = Destination(destinationLocation),
            travelMode = "DRIVE",
            routingPreference = "TRAFFIC_AWARE",
            computeAlternativeRoutes = false,
            routeModifiers = RouteModifiers(false, true, false),
            languageCode = "en-US",
            units = "METRIC"
        )

        val routeRequestMileageToStartPoint = PostRequest(
            origin = Origin(userLocWrapped),
            destination = Destination(originLocation),
            travelMode = "DRIVE",
            routingPreference = "TRAFFIC_AWARE",
            computeAlternativeRoutes = false,
            routeModifiers = RouteModifiers(false, true, false),
            languageCode = "en-US",
            units = "METRIC"
        )
        val routeRequestAvoidToll = PostRequest(
            origin = Origin(originLocation),
            destination = Destination(destinationLocation),
            travelMode = "DRIVE",
            routingPreference = "TRAFFIC_AWARE",
            computeAlternativeRoutes = false,
            routeModifiers = RouteModifiers(false, false, true),
            languageCode = "en-US",
            units = "METRIC"
        )

        val routeRequestAvoidTollToStartPoint = PostRequest(
            origin = Origin(userLocWrapped),
            destination = Destination(originLocation),
            travelMode = "DRIVE",
            routingPreference = "TRAFFIC_AWARE",
            computeAlternativeRoutes = false,
            routeModifiers = RouteModifiers(false, false, true),
            languageCode = "en-US",
            units = "METRIC"
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
                "toll" -> responseAvoidToll
                else -> responseSpeed
            }

            val routeToStartSelected = when (route) {
                "speed" -> responseSpeedToStart
                "mileage" -> responseMileageToStart
                "toll" -> responseAvoidTollToStart
                else -> responseSpeedToStart
            }



            routeSelected.routes.firstOrNull()?.polyline?.encodedPolyline?.let {
                polylinePoints = PolyUtil.decode(it)
                selectedRouteInfo = routeSelected.routes.firstOrNull()
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
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false
            )
        ) {
            userLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Your Location",
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.arrow),
                    rotation =  bearing,  /*if (isRotationEnabled) bearing else 0f,*/
                    flat = true
                )
                LaunchedEffect(userLocation) {
                    userLocation?.let {
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 18f))
                    }
                }
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
        if(showBottomSheet){
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                val routeName = when (route) {
                    "speed" -> "Time-Optimized Route"
                    "mileage" -> "Fuel-Efficient Route"
                    "toll" -> "Toll-Free Route"
                    else -> "Time-Optimized Route"
                }
                BottomSheetContent(
                    route = selectedRouteInfo,
                    routeName = routeName,
                    onCloses = {
                        showBottomSheet = false
                    }
                )
            }
        }
        Row(modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp),
            horizontalArrangement = Arrangement.End) {
//            Button(
//                onClick = { isRotationEnabled = !isRotationEnabled },
//
//            ) {
//                Icon(
//                    imageVector = if (isRotationEnabled) Icons.Default.Close else Icons.Default.Check,
//                    contentDescription = if (isRotationEnabled) "Freeform Mode" else "Center Mode"
//                )
//            }
            Button(
                onClick = { showBottomSheet = true },

                ) {
                Icon(
                    imageVector = if (showBottomSheet) Icons.Default.Check else Icons.Default.Menu,
                    contentDescription = if (showBottomSheet) "Freeform Mode" else "Center Mode"
                )
            }


        }

    }
}


@Composable
fun BottomSheetContent(
    route: Route?,
    routeName: String,
    onCloses: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp)
            .padding(24.dp)
    ) {

        fun formatDuration(duration: String): String {
            val seconds = duration.replace("s", "").toIntOrNull() ?: return "Invalid time"
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        }

        Text(
            text = "${routeName}",
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = Modifier.height(12.dp))


        Spacer(modifier = Modifier.height(12.dp))

        if (route != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Distance", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = "${route.distanceMeters / 1000.0} km",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Estimated Duration", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = formatDuration(route.duration),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            Text(
                text = "⚠️ No route information available.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCloses,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Close")
        }
    }
}
