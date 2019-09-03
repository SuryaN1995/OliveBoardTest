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
import com.example.oliverboardtest.api.model.Restaurants
import com.example.oliverboardtest.databinding.AdapterMainBinding
import com.example.oliverboardtest.ui.helperClass.RecyclerViewClick

open class MainAdapter(var items: List<Restaurants>, private val listener: RecyclerViewClick) :
    RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding = DataBindingUtil.inflate<AdapterMainBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_main, parent, false
        )
        return MainViewHolder(binding,listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class MainViewHolder(
        private val binding: AdapterMainBinding,
        private val listener: RecyclerViewClick
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(restaurant: Restaurants) {
            Glide.with(binding.root)
                .asDrawable()
                .load(restaurant.restaurant?.featured_image)
                .error(R.drawable.ic_placeholder)
                .placeholder(R.drawable.ic_placeholder)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(150,150)
                .transform(CenterCrop(), RoundedCorners(10))
                .into(binding.image)
            binding.name.apply {
                text = restaurant.restaurant?.name
                /**
                 * To enable marquee the textView has to be set isSelected to true
                 */
                isSelected = true
            }
            val rating = restaurant.restaurant?.user_rating?.aggregate_rating
            binding.ratingText.text = rating
            binding.ratings?.rating = rating?.toFloat() ?: 0f
            binding.location.apply {
                text = restaurant.restaurant?.location?.address
                /**
                 * To enable marquee the textView has to be set isSelected to true
                 */
                isSelected = true
            }

            binding.root.setOnClickListener {
                listener.onItemClickPosition(adapterPosition)
            }
        }

    }
}