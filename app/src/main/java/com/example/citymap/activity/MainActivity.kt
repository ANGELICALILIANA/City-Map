package com.example.citymap.activity

import android.animation.ValueAnimator
import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.example.citymap.adapter.MarkerDataAdapter
import com.example.citymap.adapter.PlaceAutocompleteAdapter
import com.example.citymap.R
import com.example.citymap.vo.LocationVO
import com.example.citymap.viewModel.WeatherViewModel
import com.example.citymap.databinding.EntityItemBinding
import com.example.citymap.databinding.WeatherLayoutBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var placesClient: PlacesClient
    private var isOptionSelected = false
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val weatherViewModel by viewModels<WeatherViewModel>()
    private var location: LocationVO? = null
    private lateinit var binding: WeatherLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashTheme)
        super.onCreate(savedInstanceState)

        binding = WeatherLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa la API de Places
        Places.initialize(applicationContext, getString(R.string.google_maps_api_key))
        placesClient = Places.createClient(this)

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                runOnUiThread {
                    toggleViews()
                }
            }

            override fun onLost(network: Network) {
                runOnUiThread {
                    toggleViews()
                }
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        toggleViews()

        createFragment()
        setupSearchView()
        setupAutoComplete()

        initObservers()
    }

    private fun initObservers() {
        weatherViewModel.locationDatabaseVO.observe(this, Observer {
            location = it
            val location = it
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                map.clear()
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                map.addMarker(MarkerOptions().position(latLng).title(location.name))

            } else {
                Toast.makeText(this, resources.getText(R.string.location_no_found), Toast.LENGTH_SHORT).show()
            }
        })

        weatherViewModel.weather.observe(this, Observer {
            location = it
            if (it?.current == null){
                Toast.makeText(this, resources.getText(R.string.data_no_available), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupAutoComplete() {
        val token = AutocompleteSessionToken.newInstance()
        val adapter = PlaceAutocompleteAdapter(this, placesClient, token)
        binding.autoCompleteTextView.setAdapter(adapter)
        binding.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            searchLocation(selectedItem)
            isOptionSelected = true
            hideKeyboard()
        }

        binding.autoCompleteTextView.setOnClickListener {
            if (isOptionSelected) {
                binding.autoCompleteTextView.text.clear()
                isOptionSelected = false
            }
        }
    }

    private fun setupSearchView() {

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchLocation(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }


    private fun searchLocation(city: String) {
        if (isInternetAvailable()) {
            val geocoder = Geocoder(this, Locale.getDefault())
            try {
                val addressList = geocoder.getFromLocationName(city, 1)
                if (addressList != null && addressList.isNotEmpty()) {
                    val address = addressList[0]
                    val latLng = LatLng(address.latitude, address.longitude)

                    weatherViewModel.sendCoordinates(address.latitude, address.longitude, city)

                    map.clear()
                    val marker = map.addMarker(MarkerOptions().position(latLng).title(city))
                    marker?.let {
                        animateMarker(it, latLng)
                    }
                    animateCamera(latLng)

                } else {
                    Toast.makeText(this, resources.getText(R.string.location_no), Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, resources.getText(R.string.location_error), Toast.LENGTH_SHORT).show()
            }
        } else {
            weatherViewModel.searchLocationByName(city)
        }

    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun toggleViews() {
        if (isInternetAvailable()) {
            binding.searchView.isVisible = false
            binding.autoCompleteTextView.isVisible = true
        } else {
            binding.searchView.isVisible = true
            binding.autoCompleteTextView.isVisible = false
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }

    private fun showCustomDialog(marker: Marker) {
        val binding = EntityItemBinding.inflate(layoutInflater)
        val dialogView = binding.root
        binding.tittleTableTextView.text = marker.title
        val image = location?.current?.weather?.first()?.iconWeather
        var setImage = R.drawable.ic_launcher_foreground
        when(image){
            "01d" -> setImage = R.drawable.d01
            "02d" -> setImage = R.drawable.d02
            "03d" -> setImage = R.drawable.d03
            "04d" -> setImage = R.drawable.d04
            "09d" -> setImage = R.drawable.d09
            "10d" -> setImage = R.drawable.d10
            "11d" -> setImage = R.drawable.d11
            "13d" -> setImage = R.drawable.d13
            "50d" -> setImage = R.drawable.d50
            "01n" -> setImage = R.drawable.n01
            "02n" -> setImage = R.drawable.n02
            "03n" -> setImage = R.drawable.n03
            "04n" -> setImage = R.drawable.n04
            "09n" -> setImage = R.drawable.n09
            "10n" -> setImage = R.drawable.n10
            "11n" -> setImage = R.drawable.n11
            "13n" -> setImage = R.drawable.n13
            "50n" -> setImage = R.drawable.n50
        }
        binding.iconHeaderImageView.setImageResource(setImage)
        val adapter = location?.let { MarkerDataAdapter(it) }
        binding.tableRecyclerView.adapter = adapter

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(true)
        val dialog = builder.create()

        binding.closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun animateMarker(marker: Marker, toPosition: LatLng) {
        val startPosition = marker.position

        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 2500
        valueAnimator.interpolator = LinearInterpolator()

        valueAnimator.addUpdateListener { animation ->
            val fraction = animation.animatedFraction
            val newLatLng = LatLng(
                startPosition.latitude + (toPosition.latitude - startPosition.latitude) * fraction,
                startPosition.longitude + (toPosition.longitude - startPosition.longitude) * fraction
            )
            marker.position = newLatLng
        }

        valueAnimator.start()
    }

    private fun animateCamera(toPosition: LatLng, zoom: Float = 12f) {
        val cameraPosition = CameraPosition.Builder()
            .target(toPosition)
            .zoom(zoom)
            .build()

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnMarkerClickListener { marker ->
            showCustomDialog(marker)
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }


}