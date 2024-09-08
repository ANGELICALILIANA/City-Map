package com.example.citymap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.Marker

class MarkerDataAdapter(private val marker: Marker) : RecyclerView.Adapter<MarkerDataAdapter.MarkerViewHolder>() {

    private val markerDataList = listOf(
        Pair("Latitude", marker.position.latitude.toString()),
        Pair("Longitude", marker.position.longitude.toString())
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weather_adapter, parent, false)
        return MarkerViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarkerViewHolder, position: Int) {
        val (title, data) = markerDataList[position]
        holder.rowOneTextView.text = title
        holder.dataOneTextView.text = data
    }

    override fun getItemCount(): Int = markerDataList.size

    class MarkerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rowOneTextView: TextView = itemView.findViewById(R.id.rowOneTextView)
        val dataOneTextView: TextView = itemView.findViewById(R.id.dataOneTextView)
    }
}
