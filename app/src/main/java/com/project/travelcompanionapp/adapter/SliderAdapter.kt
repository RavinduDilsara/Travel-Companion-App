package com.project.travelcompanionapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.request.RequestOptions
import com.project.travelcompanionapp.model.SliderModel
import com.project.travelcompanionapp.R

class SliderAdapter(
    private val sliderItems: List<SliderModel>,
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

   private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {

        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_item_container, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {

        holder.setImage(sliderItems[position], context)


    }

    override fun getItemCount(): Int = sliderItems.size

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.imageSlide)

        fun setImage(sliderItem: SliderModel, context: Context) {


            val requestOptions = RequestOptions().transform(CenterInside())
            Glide.with(context).load(sliderItem.url).apply(requestOptions).into(imageView)
        }
    }
}
