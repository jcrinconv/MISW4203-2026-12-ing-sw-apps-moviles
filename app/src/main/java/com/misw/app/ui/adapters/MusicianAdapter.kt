package com.misw.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.misw.app.databinding.ItemMusicianBinding
import com.misw.app.models.Musician

class MusicianAdapter(private val musicians: List<Musician>) :
    RecyclerView.Adapter<MusicianAdapter.MusicianViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicianViewHolder {
        val binding = ItemMusicianBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicianViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicianViewHolder, position: Int) {
        holder.bind(musicians[position])
    }

    override fun getItemCount(): Int = musicians.size

    class MusicianViewHolder(private val binding: ItemMusicianBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(musician: Musician) {
            binding.tvMusicianName.text = musician.name
            Glide.with(binding.ivArtistImage.context)
                .load(musician.image)
                .into(binding.ivArtistImage)
        }
    }
}