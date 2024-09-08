package com.example.citymap

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.citymap.VO.LocationVO
import com.example.citymap.ViewModel.WeatherViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
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
    private lateinit var searchView: SearchView
    private lateinit var placesClient: PlacesClient
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private var isOptionSelected = false
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val weatherViewModel by viewModels<WeatherViewModel>()
    private var location: LocationVO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.weather_layout)

        // Inicializa la API de Places
        Places.initialize(applicationContext, getString(R.string.google_maps_api_key))
        placesClient = Places.createClient(this)

        searchView = findViewById(R.id.searchView)
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView)

        // Verificar si el dispositivo está conectado a internet
        toggleViews()

        // Escucha los cambios de conexión en tiempo real (opcional)
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // Registrar el NetworkCallback para detectar cambios en la conexión
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
                Toast.makeText(this, "Ubicación no encontrada en caché", Toast.LENGTH_SHORT).show()
            }
        })

        weatherViewModel.weather.observe(this, Observer {
            location = it
        })
    }

    private fun setupAutoComplete() {
        val token = AutocompleteSessionToken.newInstance()
        val adapter = PlaceAutocompleteAdapter(this, placesClient, token)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            searchLocation(selectedItem)
            isOptionSelected = true
            hideKeyboard()
        }

        autoCompleteTextView.setOnClickListener {
            if (isOptionSelected) {
                autoCompleteTextView.text.clear()
                isOptionSelected = false
            }
        }
    }

    private fun setupSearchView() {

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                    map.addMarker(MarkerOptions().position(latLng).title(city))

                } else {
                    Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error al buscar la ubicación", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Búsqueda offline
            weatherViewModel.searchLocationByName(city)
        }

    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun toggleViews() {
        if (isInternetAvailable()) {
            searchView.isVisible = false
            autoCompleteTextView.isVisible = true
        } else {
            searchView.isVisible = true
            autoCompleteTextView.isVisible = false
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }

    private fun showCustomDialog(marker: Marker) {
        val dialogView = layoutInflater.inflate(R.layout.entity_item, null)
        val titleTextView = dialogView.findViewById<TextView>(R.id.tittleTableTextView)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.tableRecyclerView)

        titleTextView.text = marker.title

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val adapter = location?.let { MarkerDataAdapter(it) }
        recyclerView.adapter = adapter

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.show()
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