package com.misw.app.ui.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.misw.app.R
import com.misw.app.databinding.ItemMusicianBinding
import com.misw.app.model.Musician

class MusicianAdapter(private val onMusicianClick: (Int) -> Unit) : 
    RecyclerView.Adapter<MusicianAdapter.MusicianViewHolder>() {

    private var musicians: List<Musician> = emptyList()

    fun updateMusicians(newMusicians: List<Musician>) {
        this.musicians = newMusicians
        notifyDataSetChanged()
    }

    inner class MusicianViewHolder(private val binding: ItemMusicianBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(musician: Musician) {
            binding.tvMusicianName.text = musician.name
            
            binding.shimmerLayout.startShimmer()

            Glide.with(binding.root.context)
                .load(musician.image)
                .centerCrop()
                .placeholder(R.drawable.ic_artists)
                .error(R.drawable.ic_artists) // Imagen por defecto si falla la carga o permisos
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
                        return false
                    }
                })
                .into(binding.ivArtistImage)
            
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
        holder.bind(musicians[position])
    }

    override fun getItemCount(): Int = musicians.size
}
