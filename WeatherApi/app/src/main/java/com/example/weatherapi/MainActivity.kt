package com.example.weatherapi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.location.LocationManager
import android.location.Geocoder
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import android.location.Address
import com.example.weatherapi.weathergpsfolder.weatherdata.WeatherResponse
import java.text.SimpleDateFormat
import java.util.*
import com.example.weatherapi.weathergpsfolder.RetrofitClient
import com.example.weatherapi.weathergpsfolder.WeatherApi



class MainActivity : AppCompatActivity() {
    private val apiKey = "b6f8d522644b7834a7abc01d5826a1ff"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvTemperature: TextView = findViewById(R.id.tvTemperature)
        val tvDescription: TextView = findViewById(R.id.tvDescription)
        val tvHumidity: TextView = findViewById(R.id.tvHumidity)
        val tvWindSpeed: TextView = findViewById(R.id.tvWindSpeed)
        val tvPressure: TextView = findViewById(R.id.tvPressure)
        val tvVisibility: TextView = findViewById(R.id.tvVisibility)
        val tvCloudiness: TextView = findViewById(R.id.tvCloudiness)
        val tvRain: TextView = findViewById(R.id.tvRain)
        val tvDateTime: TextView = findViewById(R.id.tvDateTime)

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Permission already granted, proceed to get location
            getLocationAndFetchWeather(tvTemperature, tvDescription, tvHumidity, tvWindSpeed, tvPressure, tvVisibility, tvCloudiness, tvRain, tvDateTime)
        }
    }

    private fun getLocationAndFetchWeather(
        tvTemperature: TextView,
        tvDescription: TextView,
        tvHumidity: TextView,
        tvWindSpeed: TextView,
        tvPressure: TextView,
        tvVisibility: TextView,
        tvCloudiness: TextView,
        tvRain: TextView,
        tvDateTime: TextView
    ) {
        // Get GPS coordinates
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        val location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        location?.let {
            val latitude = it.latitude
            val longitude = it.longitude

            // Reverse geocoding to get city name
            val geocoder = Geocoder(this)
            try {
                val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val cityName = addresses[0].locality
                    val tvCity: TextView = findViewById(R.id.tvCity)
                    tvCity.text = cityName

                    // Fetch weather for the obtained coordinates
                    fetchWeather(latitude, longitude, tvTemperature, tvDescription, tvHumidity, tvWindSpeed, tvPressure, tvVisibility, tvCloudiness, tvRain, tvDateTime)
                } else {
                    showToast("City name not found")
                }
            } catch (e: IOException) {
                showToast("Geocoding error: ${e.message}")
            }
        } ?: run {
            showToast("Unable to retrieve GPS coordinates")
        }
    }

    private fun fetchWeather(
        latitude: Double,
        longitude: Double,
        tvTemperature: TextView,
        tvDescription: TextView,
        tvHumidity: TextView,
        tvWindSpeed: TextView,
        tvPressure: TextView,
        tvVisibility: TextView,
        tvCloudiness: TextView,
        tvRain: TextView,
        tvDateTime: TextView
    ) {
        val weatherApi = RetrofitClient.getClient().create(WeatherApi::class.java)
        val call = weatherApi.getWeatherForecast(latitude, longitude, apiKey)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    weatherResponse?.let {
                        val forecast = it.list.firstOrNull()
                        forecast?.let { firstForecast ->
                            val description = firstForecast.weather.firstOrNull()?.description ?: "No description"
                            val temp = firstForecast.main.temp - 273.15 // Convert Kelvin to Celsius
                            val humidity = firstForecast.main.humidity
                            val windSpeed = firstForecast.wind.speed
                            val pressure = firstForecast.main.pressure
                            val visibility = firstForecast.visibility
                            val cloudiness = firstForecast.clouds.all
                            val rainVolume = firstForecast.rain?.`3h` ?: 0.0
                            val dateTime = firstForecast.dt_txt

                            // Format date-time to be more readable
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val outputFormat = SimpleDateFormat("EEE, MMM d, yyyy h:mm a", Locale.getDefault())
                            val date: Date = inputFormat.parse(dateTime)
                            val formattedDate: String = outputFormat.format(date)

                            // Update TextViews
                            tvTemperature.text = "Temperature: %.2f°C".format(temp)
                            tvDescription.text = "Description: $description"
                            tvHumidity.text = "Humidity: $humidity%"
                            tvWindSpeed.text = "Wind Speed: $windSpeed m/s"
                            tvPressure.text = "Pressure: $pressure hPa"
                            tvVisibility.text = "Visibility: $visibility m"
                            tvCloudiness.text = "Cloudiness: $cloudiness%"
                            tvRain.text = "Rain Volume (3h): $rainVolume mm"
                            tvDateTime.text = "Date & Time: $formattedDate"
                        }
                    }
                } else {
                    showToast("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                showToast("Failure: ${t.message}")
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get location and fetch weather
                val tvTemperature: TextView = findViewById(R.id.tvTemperature)
                val tvDescription: TextView = findViewById(R.id.tvDescription)
                val tvHumidity: TextView = findViewById(R.id.tvHumidity)
                val tvWindSpeed: TextView = findViewById(R.id.tvWindSpeed)
                val tvPressure: TextView = findViewById(R.id.tvPressure)
                val tvVisibility: TextView = findViewById(R.id.tvVisibility)
                val tvCloudiness: TextView = findViewById(R.id.tvCloudiness)
                val tvRain: TextView = findViewById(R.id.tvRain)
                val tvDateTime: TextView = findViewById(R.id.tvDateTime)
                getLocationAndFetchWeather(tvTemperature, tvDescription, tvHumidity, tvWindSpeed, tvPressure, tvVisibility, tvCloudiness, tvRain, tvDateTime)
            } else {
                // Permission denied, show a message or handle accordingly
                showToast("Location permission denied")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}