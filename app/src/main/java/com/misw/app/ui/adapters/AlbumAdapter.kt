package com.misw.app.ui.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.misw.app.databinding.ItemAlbumBinding
import com.misw.app.model.Album
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target

class AlbumAdapter(private val onAlbumClick: (Int) -> Unit) :
    RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    private var albums: List<Album> = emptyList()

    inner class AlbumViewHolder(private val binding: ItemAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.tvAlbumName.text = album.name
            binding.tvAlbumGenre.text = album.genre
            binding.tvAlbumDate.text = album.releaseDate.take(10)

            binding.shimmerLayout.startShimmer()

            Glide.with(binding.root.context)
                .load(album.cover)
                .centerCrop()
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
                .into(binding.ivAlbumCover)

            binding.root.setOnClickListener {
                onAlbumClick(album.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(albums[position])
    }

    override fun getItemCount(): Int = albums.size

    fun updateAlbums(newAlbums: List<Album>) {
        this.albums = newAlbums
        notifyDataSetChanged()
    }
}