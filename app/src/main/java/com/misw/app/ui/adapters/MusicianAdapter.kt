package com.misw.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
            Glide.with(binding.ivArtistImage.context)
                .load(musician.image)
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
