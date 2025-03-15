package com.project.travelcompanionapp.view.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.project.travelcompanionapp.view.activity.DestinationDetailActivity
import com.project.travelcompanionapp.databinding.ViewholderPopularBinding
import com.project.travelcompanionapp.model.ItemModel


class PopularAdapter(private val items: List<ItemModel>) :
    RecyclerView.Adapter<PopularAdapter.MyViewHolder>() {

    private var context: Context? = null

    class MyViewHolder(val binding: ViewholderPopularBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        context = parent.context
        val binding =
            ViewholderPopularBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.binding.titleTxt.text = items[position].title
        holder.binding.addressTxt.text = items[position].address
        holder.binding.scoreTxt.text = "" + items[position].score.toString()


        val firstImageUrl = items[position].images.firstOrNull() ?: ""

        val requestOptions = RequestOptions().transform(CenterCrop())
        Glide.with(holder.itemView.context)
            .load(firstImageUrl)
            .apply(requestOptions)
            .into(holder.binding.pic)

        holder.itemView.setOnClickListener {

            val destinationTitle = items[position].title
            val intent = Intent(context, DestinationDetailActivity::class.java)
            intent.putExtra("destination_title", destinationTitle)
            context?.startActivity(intent)


        }
    }

}