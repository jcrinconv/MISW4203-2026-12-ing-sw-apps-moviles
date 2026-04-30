package com.misw.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.misw.app.databinding.ItemMusicianBinding
import com.misw.app.model.Musician

class MusicianAdapter() : RecyclerView.Adapter<MusicianAdapter.MusicianViewHolder>() {

    private var musicians: List<Musician> = emptyList()

    inner class MusicianViewHolder(private val binding: ItemMusicianBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(musician: Musician) {
            TODO("Implement load musician card")
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