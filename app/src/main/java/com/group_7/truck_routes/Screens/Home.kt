package com.group_7.truck_routes.Screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.group_7.truck_routes.routs

@Composable
fun Home(navController: NavController) {
    var userStartPoint by remember { mutableStateOf(value = "") }
    var userDestination by remember { mutableStateOf(value = "") }
    var userRoute by remember { mutableStateOf(value = "") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Enter details below",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = userStartPoint,
            onValueChange = {
                userStartPoint = it
            },
            label = { Text("Start Point") },
            placeholder = { Text("Enter Start Point") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Home, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),

            singleLine = true,


            )
        OutlinedTextField(
            value = userDestination,
            onValueChange = {
                userDestination = it
            },
            label = { Text("Destination") },
            placeholder = { Text("Enter Destination") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = userRoute,
            onValueChange = {
                userRoute = it
            },
            label = { Text("Routes") },
            placeholder = { Text("select route") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Info, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true

        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            if (userStartPoint.isNotEmpty() && userDestination.isNotEmpty() && userRoute.isNotEmpty()) {
                navController.navigate(
                    routs.Maps(
                        startPoint =  userStartPoint,
                        destination = userDestination,
                        route = userRoute
                    )
               )
            } else {
                Toast.makeText(
                    navController.context,
                    "Please fill all the fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Submit")
        }
    }


}
