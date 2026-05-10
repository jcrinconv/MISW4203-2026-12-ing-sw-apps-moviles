package com.misw.app.ui.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.shape.ShapeAppearanceModel
import com.misw.app.R
import com.misw.app.databinding.ItemMusicianBinding
import com.misw.app.model.Musician

class MusicianAdapter(private val onMusicianClick: (Int) -> Unit) : 
    ListAdapter<Musician, MusicianAdapter.MusicianViewHolder>(MusicianDiffCallback()) {

    fun updateMusicians(newMusicians: List<Musician>) {
        submitList(newMusicians)
    }

    inner class MusicianViewHolder(private val binding: ItemMusicianBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(musician: Musician) {
            binding.tvMusicianName.text = musician.name
            
            // Set dynamic content description for accessibility
            binding.ivMusicianImage.contentDescription = binding.root.context.getString(
                R.string.musician_image_description,
                musician.name
            )
            
            binding.shimmerLayout.startShimmer()

            Glide.with(binding.root.context)
                .load(musician.image)
                .centerCrop()
                .error(R.drawable.ic_artists)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.hideShimmer()
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.hideShimmer()
                        binding.shimmerLayout.background = null
                        binding.ivMusicianImage.shapeAppearanceModel = ShapeAppearanceModel()
                        return false
                    }
                })
                .into(binding.ivMusicianImage)
            
            binding.root.setOnClickListener {
                onMusicianClick(musician.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicianViewHolder {
        val binding =
            ItemMusicianBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicianViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicianViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MusicianDiffCallback : DiffUtil.ItemCallback<Musician>() {
        override fun areItemsTheSame(oldItem: Musician, newItem: Musician): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Musician, newItem: Musician): Boolean {
            return oldItem == newItem
        }
    }
}
