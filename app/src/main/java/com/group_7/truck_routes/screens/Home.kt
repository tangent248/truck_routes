package com.group_7.truck_routes.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.libraries.places.api.Places
import com.group_7.truck_routes.Routs
import com.group_7.truck_routes.components.SearchBar
import com.group_7.truck_routes.utils.ManifestUtils
import com.group_7.truck_routes.viewModel.MapViewModel

@Composable
fun Home(mapViewModel: MapViewModel, navController: NavController) {
    var userStartPoint by remember { mutableStateOf("") }
    var userDestination by remember { mutableStateOf("") }
    var loadTons by remember { mutableStateOf("") }

    val selectedStartLocation by mapViewModel.selectedStartLocation
    val selectedDestinationLocation by mapViewModel.selectedDestinationLocation

    val context = LocalContext.current
    val apiKey = ManifestUtils.getApiKeyFromManifest(context)

    if (!Places.isInitialized() && apiKey != null) {
        Places.initialize(context.applicationContext, apiKey)
    }

    LaunchedEffect(selectedStartLocation) {
        selectedStartLocation?.let {
            userStartPoint = "${it.latitude},${it.longitude}"
        }
    }

    LaunchedEffect(selectedDestinationLocation) {
        selectedDestinationLocation?.let {
            userDestination = "${it.latitude},${it.longitude}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Truck Route Planner",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Start Point",
                    style = MaterialTheme.typography.labelLarge
                )
                SearchBar(onPlaceSelected = { place ->
                    mapViewModel.selectStartLocation(place, context)
                })

                Text(
                    text = "Destination Point",
                    style = MaterialTheme.typography.labelLarge
                )
                SearchBar(onPlaceSelected = { place ->
                    mapViewModel.selectDestinationLocation(place, context)
                })

                OutlinedTextField(
                    value = loadTons,
                    onValueChange = { loadTons = it },
                    label = { Text("Enter Load (tons)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (userStartPoint.isNotEmpty() && userDestination.isNotEmpty() && loadTons.isNotEmpty()) {
                            val load = loadTons.toDoubleOrNull() ?: 0.0
                            navController.navigate(
                                Routs.RouteSelectionScreen(
                                    startPoint = userStartPoint,
                                    destination = userDestination,
                                    loadTons = load
                                )
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Please fill all the fields",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Find Routes")
                }
            }
        }
    }
}
