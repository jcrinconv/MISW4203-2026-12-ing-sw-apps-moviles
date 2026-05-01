package com.misw.app.ui.musicians

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.misw.app.R
import com.misw.app.databinding.FragmentMusicianListBinding
import com.misw.app.ui.adapters.MusicianAdapter
import com.misw.app.viewmodel.MusicianViewModel

class MusicianListFragment : Fragment() {

    private var _binding: FragmentMusicianListBinding? = null
    private val binding get() = _binding!!
    private lateinit var musicianAdapter: MusicianAdapter
    private val viewModel: MusicianViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicianListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupControls()
        setupSearch()
        observeViewModel()
    }

    private fun setupControls() {
        binding.btnSwapOrder.ibSwapOrder.setOnClickListener {
            viewModel.toggleSortOrder()
            binding.btnSwapOrder.ibSwapOrder.animate().rotationBy(180f).setDuration(300).start()
        }
    }

    private fun setupRecyclerView() {
        musicianAdapter = MusicianAdapter { musicianId ->
            val bundle = Bundle().apply {
                putInt("musician_id", musicianId)
            }
            findNavController().navigate(
                R.id.action_musicianListFragment_to_musicianDetailFragment, bundle
            )
        }

        binding.rvMusicians.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = musicianAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearch() {
        if (binding.searchBar.etSearchAlbum.text.toString() != viewModel.query.value) {
            binding.searchBar.etSearchAlbum.setText(viewModel.query.value)
        }

        binding.searchBar.etSearchAlbum.doOnTextChanged { text, _, _, _ ->
            viewModel.filterMusicians(text.toString())
        }
    }

    private fun observeViewModel() {
        viewModel.musicians.observe(viewLifecycleOwner) { musicians ->
            musicianAdapter.updateMusicians(musicians)
            updateUIState(musicians, viewModel.error.value)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            updateUIState(viewModel.musicians.value ?: emptyList<Any>(), errorMessage)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbAlbumList.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.llEmptyState.visibility = View.GONE
            }
        }
    }

    private fun updateUIState(musicians: List<*>, error: String?) {
        when {
            error != null -> {
                binding.llEmptyState.visibility = View.VISIBLE
                binding.rvMusicians.visibility = View.GONE
                binding.tvEmptyState.text = error
                binding.ivEmptyState.setImageResource(android.R.drawable.stat_notify_error)
            }

            musicians.isEmpty() -> {
                binding.llEmptyState.visibility = View.VISIBLE
                binding.rvMusicians.visibility = View.GONE
                binding.tvEmptyState.text = getString(R.string.no_artists_found)
                binding.ivEmptyState.setImageResource(R.drawable.ic_artists)
            }

            else -> {
                binding.llEmptyState.visibility = View.GONE
                binding.rvMusicians.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
