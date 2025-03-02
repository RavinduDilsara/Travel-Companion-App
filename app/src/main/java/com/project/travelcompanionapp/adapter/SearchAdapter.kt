package com.project.travelcompanionapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.travelcompanionapp.model.DestinationListModel
import com.project.travelcompanionapp.R

class SearchAdapter(
    destinationList: List<DestinationListModel>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var filteredList: List<DestinationListModel> = destinationList.take(5)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textViewItem)
        val imageView: ImageView = itemView.findViewById(R.id.imageViewItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_searchitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val destination = filteredList[position]

        holder.textView.text = destination.title
        Glide.with(holder.itemView.context)
            .load(destination.pic)
            .into(holder.imageView)


        holder.itemView.setOnClickListener {
            onItemClick(destination.title)
        }
    }

    override fun getItemCount(): Int = filteredList.size

}
