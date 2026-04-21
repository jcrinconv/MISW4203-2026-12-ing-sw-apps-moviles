package com.misw.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.misw.app.databinding.ItemTrackBinding
import com.misw.app.model.Track

class TrackAdapter(private var tracks: List<Track> = emptyList()) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    inner class TrackViewHolder(private val binding: ItemTrackBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track) {
            binding.tvTrackName.text = track.name
            binding.tvTrackDuration.text = track.duration
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    fun submitList(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

}