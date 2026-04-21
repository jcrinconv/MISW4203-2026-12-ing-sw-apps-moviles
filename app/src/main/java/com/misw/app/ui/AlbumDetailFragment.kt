package com.misw.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.misw.app.databinding.FragmentAlbumDetailBinding
import com.misw.app.ui.adapters.TrackAdapter
import com.misw.app.viewmodel.AlbumDetailViewModel
import java.text.SimpleDateFormat
import java.util.Locale


class AlbumDetailFragment : Fragment() {

    private val viewModel: AlbumDetailViewModel by viewModels()
    private var _binding: FragmentAlbumDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackAdapter: TrackAdapter

    companion object {
        private const val ARG_ALBUM_ID = "album_id"

        fun newInstance(albumId: Int): AlbumDetailFragment {
            val fragment = AlbumDetailFragment()
            val args = Bundle()
            args.putInt(ARG_ALBUM_ID, albumId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter()
        binding.rvTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTracks.adapter = trackAdapter

        viewModel.album.observe(viewLifecycleOwner) { album ->
            binding.tvReleaseDate.text = formatDate(album.releaseDate)
            binding.tvRecordLabel.text = album.recordLabel
            binding.tvGenre.text = album.genre
            binding.tvDescription.text = album.description
            trackAdapter.submitList(album.tracks)
        }

        val albumId = arguments?.getInt(ARG_ALBUM_ID) ?: error("AlbumDetailFragment requiere un albumId")
        viewModel.loadAlbum(albumId)

        viewModel.error.observe(viewLifecycleOwner) { error ->
            android.util.Log.e("AlbumDetail", "Error: $error")
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale("es"))
            val date = inputFormat.parse((dateString))
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}