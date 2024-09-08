package com.example.citymap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.citymap.R
import com.example.citymap.vo.LocationVO

class MarkerDataAdapter(private val locationVO: LocationVO) : RecyclerView.Adapter<MarkerDataAdapter.MarkerViewHolder>() {

    private val markerDataList = listOf(
        Pair("Latitud", locationVO.latitude.toString()),
        Pair("Longitud",locationVO.longitude.toString()),
        Pair("Zona horaria", locationVO.timezone),
        Pair("Zona horaria offset", locationVO.timezoneOffset.toString()),
        Pair("Tiempo actual", locationVO.current?.currentTime.toString()),
        Pair("Salida del sol", locationVO.current?.sunrise.toString()),
        Pair("Puesta del sol", locationVO.current?.sunset.toString()),
        Pair("Temperatura", locationVO.current?.temperature.toString()),
        Pair("Temperatura de clima", locationVO.current?.feelsLike.toString()),
        Pair("Presión", locationVO.current?.pressure.toString()),
        Pair("Humedad", locationVO.current?.humidity.toString() + "%"),
        Pair("Temperatura atmosférica", locationVO.current?.dewPoint.toString()),
        Pair("Indice UV", locationVO.current?.uv.toString()),
        Pair("Nubosidad", locationVO.current?.clouds.toString() + "%"),
        Pair("Visibilidad media", locationVO.current?.visibility.toString()+ "KM"),
        Pair("Velocidad de viento", locationVO.current?.windSpeed.toString()),
        Pair("Dirección de viento", locationVO.current?.windDirection.toString()),
        Pair("Ráfaga de viento", locationVO.current?.windGust.toString()),
        Pair("Identificación", locationVO.current?.weather?.first()?.idWeather.toString()),
        Pair("Parámetro meteorológico", locationVO.current?.weather?.first()?.mainWeather.toString()),
        Pair("Condición meteorológica", locationVO.current?.weather?.first()?.descriptionWeather.toString()),
        Pair("Ícono del tiempo", locationVO.current?.weather?.first()?.iconWeather.toString())
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