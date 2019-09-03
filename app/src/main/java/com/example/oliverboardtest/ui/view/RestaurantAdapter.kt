package com.example.oliverboardtest.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.oliverboardtest.R
import com.example.oliverboardtest.api.model.Photos
import com.example.oliverboardtest.databinding.AdapterRestaurantBinding

open class RestaurantAdapter(private val photos: List<Photos>) :
    RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<AdapterRestaurantBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_restaurant, parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    class ViewHolder(private val binding: AdapterRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photos: Photos) {
            Glide.with(binding.root)
                .asDrawable()
                .load(photos.photo?.thumb_url)
                .error(R.drawable.ic_placeholder)
                .placeholder(R.drawable.ic_placeholder)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(125, 125)
                .transform(CenterCrop(),RoundedCorners(10))
                .into(binding.photo)
        }

    }
}