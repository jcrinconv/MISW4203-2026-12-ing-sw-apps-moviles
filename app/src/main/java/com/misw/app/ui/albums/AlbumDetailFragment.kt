package com.misw.app.ui.albums

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.misw.app.R
import com.misw.app.databinding.FragmentAlbumDetailBinding
import com.misw.app.ui.adapters.TrackAdapter
import com.misw.app.viewmodel.AlbumDetailViewModel
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AlbumDetailFragment : Fragment() {

    companion object {
        private const val DATE_INPUT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        private const val DATE_OUTPUT_PATTERN = "MMM d, yyyy"
    }

    private val viewModel: AlbumDetailViewModel by viewModels()
    private var _binding: FragmentAlbumDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter()
        binding.rvTracksContainer.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
            setHasFixedSize(false)
        }

        // Refresh album when returning from adding tracks
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.refreshAlbum()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.nestedScrollView.visibility = View.GONE
                binding.llEmptyState.visibility = View.GONE
            } else if (viewModel.error.value == null) {
                binding.nestedScrollView.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            updateUIState(viewModel.album.value, errorMessage)
        }

        viewModel.album.observe(viewLifecycleOwner) { album ->
            updateUIState(album, viewModel.error.value)
            
            updateUI(album)
            binding.tvTrackCount.text = resources.getQuantityString(R.plurals.track_count, album.tracks.size, album.tracks.size)

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

            trackAdapter.submitTracks(album.tracks)
        }

        val albumId = requireArguments().getInt("album_id")

        binding.btnAssociateTracks.setOnClickListener {
            val bundle = Bundle().apply { putInt("album_id", albumId) }
            findNavController().navigate(R.id.action_albumDetailFragment_to_trackAssociateFragment, bundle)
        }

        viewModel.loadAlbum(albumId)
    }

    private fun updateUIState(album: com.misw.app.model.Album?, error: String?) {
        when {
            error != null -> {
                binding.llEmptyState.visibility = View.VISIBLE
                binding.nestedScrollView.visibility = View.GONE
                binding.tvEmptyState.text = getString(R.string.error_loading_content)
                binding.ivEmptyState.setImageResource(android.R.drawable.stat_notify_error)
            }
            album == null -> {
                binding.llEmptyState.visibility = View.VISIBLE
                binding.nestedScrollView.visibility = View.GONE
                binding.tvEmptyState.text = getString(R.string.error_loading_content)
                binding.ivEmptyState.setImageResource(android.R.drawable.stat_notify_error)
            }
            else -> {
                binding.llEmptyState.visibility = View.GONE
                binding.nestedScrollView.visibility = View.VISIBLE
            }
        }
    }

    private fun updateUI(album: com.misw.app.model.Album) {
        binding.tvAlbumName.text = album.name
        binding.tvReleaseDate.text = formatDate(album.releaseDate)
        binding.tvRecordLabel.text = album.recordLabel
        binding.tvGenre.text = album.genre
        binding.tvDescription.text = album.description

        // Lógica de "Ver más" para la descripción
        binding.tvDescription.post {
            val tv = binding.tvDescription
            if (tv.layout != null) {
                val lines = tv.layout.lineCount
                if (lines > 0 && tv.layout.getEllipsisCount(lines - 1) > 0) {
                    binding.tvReadMore.visibility = View.VISIBLE
                    var isExpanded = false
                    
                    binding.tvReadMore.setOnClickListener {
                        isExpanded = !isExpanded
                        if (isExpanded) {
                            tv.maxLines = Integer.MAX_VALUE
                            binding.tvReadMore.text = getString(R.string.see_less_label)
                        } else {
                            tv.maxLines = 3
                            binding.tvReadMore.text = getString(R.string.see_more_label)
                        }
                    }
                } else {
                    binding.tvReadMore.visibility = View.GONE
                }
            }
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat(DATE_INPUT_PATTERN, Locale.getDefault())
            val outputFormat = SimpleDateFormat(DATE_OUTPUT_PATTERN, Locale.forLanguageTag("es"))
            val date = inputFormat.parse(dateString)
            val formatted = outputFormat.format(date!!)
            "Lanzado en ${formatted.replaceFirstChar { it.uppercase() }}"
        } catch (_: Exception) {
            dateString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}