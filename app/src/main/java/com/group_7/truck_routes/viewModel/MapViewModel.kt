package com.group_7.truck_routes.viewModel

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel : ViewModel() {

    private val _selectedStartLocation = mutableStateOf<LatLng?>(null)
    val selectedStartLocation: State<LatLng?> = _selectedStartLocation

    private val _selectedDestinationLocation = mutableStateOf<LatLng?>(null)
    val selectedDestinationLocation: State<LatLng?> = _selectedDestinationLocation

    private val _userLocation = mutableStateOf<LatLng?>(null)
    val userLocation: State<LatLng?> = _userLocation

    private val _bearing = mutableFloatStateOf(0f)
    val bearing: State<Float> = _bearing

    private var locationCallback: LocationCallback? = null

    fun selectStartLocation(place: String, context: Context) {
        geocodePlace(place, context) { latLng ->
            _selectedStartLocation.value = latLng
        }
    }

    fun selectDestinationLocation(place: String, context: Context) {
        geocodePlace(place, context) { latLng ->
            _selectedDestinationLocation.value = latLng
        }
    }

    private fun geocodePlace(place: String, context: Context, callback: (LatLng?) -> Unit) {
        viewModelScope.launch {
            val geocoder = Geocoder(context)
            val addresses = withContext(Dispatchers.IO) {
                geocoder.getFromLocationName(place, 1)
            }
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                callback(LatLng(address.latitude, address.longitude))
            } else {
                Log.e("MapScreen", "No location found for $place")
                callback(null)
            }
        }
    }

    fun startUpdatingLocationPeriodically(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) return

        val locationRequest = LocationRequest.create().apply {
            interval = 3000
            fastestInterval = 2000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                location?.let {
                    _userLocation.value = LatLng(it.latitude, it.longitude)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, Looper.getMainLooper())
    }

//    fun stopLocationUpdates(fusedLocationClient: FusedLocationProviderClient) {
//        locationCallback?.let {
//            fusedLocationClient.removeLocationUpdates(it)
//        }
//    }

    fun startCompass(context: Context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)

        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        System.arraycopy(event.values, 0, gravity, 0, event.values.size)
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        System.arraycopy(event.values, 0, geomagnetic, 0, event.values.size)
                    }
                }

                val rotationMatrix = FloatArray(9)
                val orientation = FloatArray(3)

                if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    _bearing.floatValue = (azimuth + 360) % 360
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_UI)
    }
}
