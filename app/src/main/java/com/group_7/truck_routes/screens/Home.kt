package com.group_7.truck_routes.screens

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
import com.group_7.truck_routes.Routs

@Composable
fun Home(navController: NavController) {
    var userStartPoint by remember { mutableStateOf(value = " 22.763458,86.238714") }
    var userDestination by remember { mutableStateOf(value = "18.651402,73.817583") }
//    var userRoute by remember { mutableStateOf(value = "") }
//
//    var isExpanded by remember { mutableStateOf(false) }


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

//        DropDownMenu(
//            label = userRoute,
//            placeholder = "select route",
//            expanded = isExpanded,
//            onExpandedChange = {
//                isExpanded = it
//            },
//            onOptionSelected = { unit ->
//                userRoute = unit
//                isExpanded = false
//
//            }
//
//
//        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            if (userStartPoint.isNotEmpty() && userDestination.isNotEmpty()) {
                navController.navigate(
                    Routs.RouteSelectionScreen(
                        startPoint = userStartPoint,
                        destination = userDestination
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

//@Composable
//fun DropDownMenu(
//    label: String,
//    placeholder: String,
//    expanded: Boolean,
//    onExpandedChange: (Boolean) -> Unit,
//    onOptionSelected: (String) -> Unit,
//) {
//    Box {
//        Button(
//            onClick = { onExpandedChange(!expanded) },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(
//                text = if (label.isNotBlank()) label else placeholder,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
//
//            Icon(
//                imageVector = Icons.Default.ArrowDropDown,
//                contentDescription = null,
//                modifier = Modifier.rotate(if (expanded) 180f else 0f)
//            )
//        }
//
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { onExpandedChange(false) },
//            modifier = Modifier
//                .fillMaxWidth()
//        ) {
//            listOf(
//                "speed",
//                "mileage",
//                "toll"
//            ).forEach { unit ->
//                DropdownMenuItem(
//                    text = { Text(text = unit) },
//                    onClick = {
//                        onExpandedChange(false)
//                        onOptionSelected(unit)
//                    }
//                )
//            }
//        }
//    }
//}

