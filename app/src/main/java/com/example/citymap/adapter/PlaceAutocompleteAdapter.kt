package com.example.citymap.adapter

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Filter
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

class PlaceAutocompleteAdapter(
    private val context: Context,
    private val placesClient: PlacesClient,
    private val token: AutocompleteSessionToken
) : ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line) {

    private var predictions: List<AutocompletePrediction> = listOf()

    fun updatePredictions(predictions: List<AutocompletePrediction>) {
        this.predictions = predictions
        clear()
        addAll(predictions.map { it.getFullText(null).toString() })
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint != null) {
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setSessionToken(token)
                        .setQuery(constraint.toString())
                        .build()

                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { response ->
                            val predictionList = response.autocompletePredictions
                            updatePredictions(predictionList)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Autocomplete", "Error obteniendo predicciones: ", exception)
                        }
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // Se actualizan los resultados en el adaptador
            }
        }
    }
}
