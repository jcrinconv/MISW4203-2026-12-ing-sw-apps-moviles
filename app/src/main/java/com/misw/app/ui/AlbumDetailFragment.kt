package com.misw.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.misw.app.R
import com.misw.app.databinding.FragmentAlbumDetailBinding
import com.misw.app.model.Track
import com.misw.app.viewmodel.AlbumDetailViewModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale

class AlbumDetailFragment : Fragment() {

    private val viewModel: AlbumDetailViewModel by viewModels()
    private var _binding: FragmentAlbumDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.album.observe(viewLifecycleOwner) { album ->
            binding.tvAlbumName.text = album.name
            binding.tvReleaseDate.text = formatDate(album.releaseDate)
            binding.tvDescription.text = album.description
            binding.tvGenre.text = album.genre
            binding.tvRecordLabel.text = album.recordLabel

            // Carga de imagen con Picasso
            Picasso.get()
                .load(album.cover)
                .placeholder(R.drawable.ic_album)
                .into(binding.ivAlbumCover)

            // Renderizado dinámico de tracks (estilo lista del diseño)
            renderTracks(album.tracks)
        }

        val albumId = arguments?.getInt("album_id") ?: 100
        viewModel.loadAlbum(albumId)
    }

    private fun renderTracks(tracks: List<Track>) {
        binding.llTracksContainer.removeAllViews()
        tracks.forEachIndexed { index, track ->
            val trackView = layoutInflater.inflate(R.layout.item_track_row, binding.llTracksContainer, false)
            trackView.findViewById<TextView>(R.id.tvTrackNumber).text = String.format("%02d", index + 1)
            trackView.findViewById<TextView>(R.id.tvTrackName).text = track.name
            trackView.findViewById<TextView>(R.id.tvTrackDuration).text = track.duration
            binding.llTracksContainer.addView(trackView)
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
            val date = inputFormat.parse(dateString)
            "Released ${outputFormat.format(date!!)}"
        } catch (e: Exception) {
            dateString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
