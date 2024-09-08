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
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.citymap.database.AppDatabase
import com.example.citymap.database.entity.LocationEntity
import com.example.citymap.ViewModel.WeatherViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                    toggleViews() // Cambia las vistas cuando la conexión está disponible
                }
            }

            override fun onLost(network: Network) {
                runOnUiThread {
                    toggleViews() // Cambia las vistas cuando se pierde la conexión
                }
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        createFragment()
        setupSearchView()
        setupAutoComplete()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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

        // Evento de clic en el AutoCompleteTextView
        autoCompleteTextView.setOnClickListener {
            // Si ya se seleccionó una opción, borrar el texto
            if (isOptionSelected) {
                autoCompleteTextView.text.clear()
                isOptionSelected = false // Restablecer la selección
            }
        }
    }

    private fun setupSearchView() {
        val token = AutocompleteSessionToken.newInstance()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchLocation(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                /*if (newText != null && newText.length > 2) {
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setSessionToken(token)
                        .setQuery(newText)
                        .build()

                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { response ->
                            for (prediction in response.autocompletePredictions) {
                                // Aquí puedes mostrar las sugerencias de autocompletado
                                Log.i("Autocomplete", prediction.getFullText(null).toString())
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Autocomplete", "Error obteniendo predicciones: ", exception)
                        }
                }

                 */
                return false
            }
        })
    }


    private fun searchLocation(city: String) {
        if (isInternetAvailable()) {
            val geocoder = Geocoder(this, Locale.getDefault())
            CoroutineScope(Dispatchers.IO).launch {

                try {
                    val addressList = geocoder.getFromLocationName(city, 1)
                    if (addressList != null && addressList.isNotEmpty()) {
                        val address = addressList[0]
                        val latLng = LatLng(address.latitude, address.longitude)

                        val location = LocationEntity(name = city, latitude = address.latitude, longitude = address.longitude)
                        weatherViewModel.sendCoordinates(location.latitude, location.longitude)
                        // Guarda la ubicación en la base de datos
                        saveLocationToDatabase(location)



                        withContext(Dispatchers.Main) {
                            map.clear()
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                            map.addMarker(MarkerOptions().position(latLng).title(city))
                        }

                        /*map.clear()

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                        map.addMarker(MarkerOptions().position(latLng).title(city))

                         */
                    } else {
                        println("bicación no encontrada")
                        //Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    println("Error al buscar la ubicación")
                    //Toast.makeText(this, "Error al buscar la ubicación", Toast.LENGTH_SHORT).show()
                }

            }
        } else {
            // Búsqueda offline
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(this@MainActivity)
                val location = db.locationDao().searchByName(city)
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)

                    withContext(Dispatchers.Main) {
                        map.clear()
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                        map.addMarker(MarkerOptions().position(latLng).title(location.name))
                    }
                } else {
                    println("Ubicación no encontrada en caché")
                }
            }
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


    override fun onMapReady(googleMap: GoogleMap) {
        // Se crea cuando el mapa ha sido cargado
        map = googleMap
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
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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


    private suspend fun saveLocationToDatabase(location: LocationEntity) {
        val db = AppDatabase.getDatabase(this)
        db.locationDao().insert(location)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Anular el registro del NetworkCallback para evitar fugas de memoria
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }


}