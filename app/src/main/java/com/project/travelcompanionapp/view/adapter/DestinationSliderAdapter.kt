package com.project.travelcompanionapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.request.RequestOptions
import com.project.travelcompanionapp.R


class DestinationSliderAdapter(
    private val images: List<String>
) : RecyclerView.Adapter<DestinationSliderAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_destination_image, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.setImage(images[position])
    }

    override fun getItemCount(): Int = images.size

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.destinationImage)

        fun setImage(imageUrl: String) {
            Glide.with(imageView.context)
                .load(imageUrl)
                .apply(RequestOptions().transform(CenterInside()))
                .into(imageView)
        }
    }

}
