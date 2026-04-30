package com.misw.app.ui.musicians

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.misw.app.databinding.FragmentMusicianListBinding
import com.misw.app.ui.adapters.MusicianAdapter
import com.misw.app.viewmodel.MusicianViewModel

class MusicianListFragment : Fragment() {

    private var _binding: FragmentMusicianListBinding? = null
    private val binding get() = _binding!!
    private lateinit var musicianAdapter: MusicianAdapter
    private val viewModel: MusicianViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicianListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        musicianAdapter = MusicianAdapter { musicianId ->
            // Navegación a detalle
        }

        binding.rvMusicians.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = musicianAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.musicians.observe(viewLifecycleOwner) { musicians ->
            musicianAdapter.updateMusicians(musicians)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbAlbumList.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
