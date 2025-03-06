package com.project.travelcompanionapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.travelcompanionapp.R
import com.project.travelcompanionapp.model.ForecastItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class WeatherForecastAdapter(private var forecastList: List<ForecastItem>) :
    RecyclerView.Adapter<WeatherForecastAdapter.ForecastViewHolder>() {

    class ForecastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtForecastDate: TextView = view.findViewById(R.id.txtForecastDate)
        val forecastIcon: ImageView = view.findViewById(R.id.forecastIcon)
        val txtForecastTemp: TextView = view.findViewById(R.id.txtForecastTemp)
        val txtForecastRain: TextView = view.findViewById(R.id.txtForecastRain)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.forecast_details_recyclerview, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val item = forecastList[position]


        val dateFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
        val date = dateFormat.format(Date(item.dt * 1000))

        holder.txtForecastDate.text = date
        holder.txtForecastTemp.text = "${item.main.temp.roundToInt()}°C"
        holder.txtForecastRain.text = "Rain: ${(item.pop * 100).toInt()}%"

        // Load weather icon using Glide
        val iconUrl = "https://openweathermap.org/img/wn/${item.weather[0].icon}@2x.png"
        Glide.with(holder.itemView.context)
            .load(iconUrl)
            .into(holder.forecastIcon)
    }

    override fun getItemCount(): Int = forecastList.size

    fun updateData(newForecasts: List<ForecastItem>) {
        forecastList = newForecasts
        notifyDataSetChanged()
    }
}
