package com.group_7.truck_routes.components

import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest


@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onPlaceSelected: (String) -> Unit
) {
    // Determine the appropriate text color based on the current theme
    val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    // Use AndroidView to integrate an Android View (AutoCompleteTextView) into Jetpack Compose
    AndroidView(
        factory = { context ->
            AutoCompleteTextView(context).apply {
                hint = "Search for a place" // Set the hint text for the search bar

                // Set the text color and hint text color based on the current theme
                setTextColor(textColor.toArgb())
                setHintTextColor(textColor.copy(alpha = 0.6f).toArgb()) // Set hint text color with some transparency

                // Set the layout params to ensure the view takes up the full width
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                // Initialize the Places API and the Autocomplete Adapter
                val autocompleteAdapter = ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line)
                val placesClient = Places.createClient(context)
                val autocompleteSessionToken = AutocompleteSessionToken.newInstance()

                // Add a text change listener to capture and handle the user's input
                addTextChangedListener(object : android.text.TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        val query = s?.toString() ?: ""
                        if (query.isNotEmpty()) {
                            val request = FindAutocompletePredictionsRequest.builder()
                                .setSessionToken(autocompleteSessionToken)
                                .setQuery(query)
                                .build()

                            placesClient.findAutocompletePredictions(request)
                                .addOnSuccessListener { response ->
                                    autocompleteAdapter.clear()
                                    response.autocompletePredictions.forEach { prediction ->
                                        autocompleteAdapter.add(prediction.getFullText(null).toString())
                                    }
                                    autocompleteAdapter.notifyDataSetChanged()
                                }
                        }
                    }

                    override fun afterTextChanged(s: android.text.Editable?) {}
                })

                // Set the adapter to the AutoCompleteTextView to display suggestions
                setAdapter(autocompleteAdapter)

                // Set an item click listener to handle when the user selects a suggestion
                setOnItemClickListener { _, _, position, _ ->
                    val selectedPlace = autocompleteAdapter.getItem(position) ?: return@setOnItemClickListener

                    // Call the callback function to handle the selected place
                    onPlaceSelected(selectedPlace)
                }
            }
        },
        modifier = modifier // Apply the passed modifier to the AutoCompleteTextView
            .fillMaxWidth() // Make sure the composable fills the maximum width available
            .padding(16.dp) //  Add padding to the search bar
    )
}