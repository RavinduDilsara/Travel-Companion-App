package com.project.travelcompanionapp.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.travelcompanionapp.R
import com.project.travelcompanionapp.model.ItemModel
import com.project.travelcompanionapp.view.activity.DestinationDetailActivity

class SearchAdapter(
    itemList: List<ItemModel>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var filteredList: List<ItemModel> = itemList.take(3)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textView: TextView = itemView.findViewById(R.id.textViewItem)
        val imageView: ImageView = itemView.findViewById(R.id.imageViewItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_destination_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val destination = filteredList[position]
        holder.textView.text = destination.title

        val imageUrl = destination.images.firstOrNull() ?: ""
        Glide.with(holder.itemView.context).load(imageUrl).into(holder.imageView)

        holder.itemView.setOnClickListener {
            onItemClick(destination.title)
            val context = holder.itemView.context
            val intent = Intent(context, DestinationDetailActivity::class.java).apply {
                putExtra("destination_title", destination.title)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = filteredList.size
}
