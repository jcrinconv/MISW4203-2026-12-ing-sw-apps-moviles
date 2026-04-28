package com.misw.app.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.misw.app.R
import com.misw.app.databinding.FragmentAlbumDetailBinding
import com.misw.app.model.Track
import com.misw.app.viewmodel.AlbumDetailViewModel
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

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.nestedScrollView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.album.observe(viewLifecycleOwner) { album ->
            binding.tvAlbumName.text = album.name
            binding.tvReleaseDate.text = formatDate(album.releaseDate)
            binding.tvDescription.text = album.description
            binding.tvGenre.text = album.genre
            binding.tvRecordLabel.text = album.recordLabel
            binding.tvTrackCount.text = getString(R.string.track_count, album.tracks.size)

            binding.shimmerLayout.startShimmer()

            // Carga de imagen con Glide
            Glide.with(this)
                .load(album.cover)
                //.placeholder(R.drawable.ic_album)
                .error(R.drawable.ic_album)
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

            // Renderizado dinámico de tracks (estilo lista del diseño)
            renderTracks(album.tracks)
        }

        val albumId = arguments?.getInt("album_id") ?: 100
        viewModel.loadAlbum(albumId)
    }

    private fun renderTracks(tracks: List<Track>) {
        binding.llTracksContainer.removeAllViews()
        if (tracks.isNotEmpty()) {
            tracks.forEachIndexed { index, track ->
                val trackView = layoutInflater.inflate(R.layout.item_track, binding.llTracksContainer, false)
                trackView.findViewById<TextView>(R.id.tvTrackNumber).text = (index + 1).toString().padStart(2, '0')
                trackView.findViewById<TextView>(R.id.tvTrackName).text = track.name
                trackView.findViewById<TextView>(R.id.tvTrackDuration).text = track.duration
                binding.llTracksContainer.addView(trackView)
            }
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale("es"))
            val date = inputFormat.parse((dateString))
            val formatted = outputFormat.format(date!!)
            "Lanzado en ${formatted.replaceFirstChar { it.uppercase() }}"
        } catch (e: Exception) {
            dateString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}