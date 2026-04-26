package com.misw.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.misw.app.databinding.ItemAlbumBinding
import com.misw.app.model.Album

class AlbumAdapter(private val onAlbumClick: (Int) -> Unit) :
    RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    private var albumsList: List<Album> = emptyList()
    private var filteredAlbumsList: List<Album> = emptyList()

    inner class AlbumViewHolder(private val binding: ItemAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.tvAlbumName.text = album.name
            binding.tvAlbumGenre.text = album.genre
            binding.tvAlbumDate.text = album.releaseDate.take(10)

            Glide.with(binding.root.context).load(album.cover).centerCrop()
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
        holder.bind(filteredAlbumsList[position])
    }

    override fun getItemCount(): Int = filteredAlbumsList.size

    fun updateAlbums(newAlbums: List<Album>) {
        this.albumsList = newAlbums
        this.filteredAlbumsList = newAlbums
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredAlbumsList = if (query.isEmpty()) {
            albumsList
        } else {
            albumsList.filter { it.name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}