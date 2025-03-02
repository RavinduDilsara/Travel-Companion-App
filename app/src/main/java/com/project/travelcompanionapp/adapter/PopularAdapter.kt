package com.project.travelcompanionapp.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
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

        val requestOptions = RequestOptions().transform(CenterCrop())
        Glide.with(holder.itemView.context).load(items[position].pic).apply(requestOptions).into(holder.binding.pic)

        holder.itemView.setOnClickListener{


        }

    }
}