package com.misw.app.ui.albums

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.misw.app.R
import com.misw.app.databinding.FragmentAlbumListBinding
import com.misw.app.ui.adapters.AlbumAdapter
import com.misw.app.viewmodel.AlbumViewModel
import com.misw.app.viewmodel.SortCriterion

class AlbumListFragment : Fragment() {
    private val viewModel: AlbumViewModel by viewModels()
    private var _binding: FragmentAlbumListBinding? = null
    private val binding get() = _binding!!
    private lateinit var albumAdapter: AlbumAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupControls()

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbAlbumList.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvAlbumList.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            albumAdapter.updateAlbums(albums)
        }

        if (binding.etSearchAlbum.text.toString() != viewModel.query.value) {
            binding.etSearchAlbum.setText(viewModel.query.value)
        }

        updateSortButtonsUI(true)
        setupSearch()
    }

    private fun setupControls() {
        binding.btnSortName.setOnClickListener {
            viewModel.setSortCriterion(SortCriterion.NAME)
            updateSortButtonsUI(isNameSelected = true)
        }

        binding.btnSortDate.setOnClickListener {
            viewModel.setSortCriterion(SortCriterion.RELEASE_DATE)
            updateSortButtonsUI(isNameSelected = false)
        }

        binding.btnSwapOrder.setOnClickListener {
            viewModel.toggleSortOrder()
            // Rotación visual del icono de swap
            binding.btnSwapOrder.animate().rotationBy(180f).setDuration(300).start()
        }
    }

    private fun updateSortButtonsUI(isNameSelected: Boolean) {
        val pink = ContextCompat.getColor(requireContext(), R.color.wild_strawberry)
        val transparent = Color.TRANSPARENT
        val white = Color.WHITE
        val gray = Color.GRAY

        binding.btnSortName.backgroundTintList =
            ColorStateList.valueOf(if (isNameSelected) pink else transparent)
        binding.btnSortName.setTextColor(if (isNameSelected) white else gray)

        binding.btnSortDate.backgroundTintList =
            ColorStateList.valueOf(if (isNameSelected) transparent else pink)
        binding.btnSortDate.setTextColor(if (isNameSelected) gray else white)
    }

    private fun setupSearch() {
        binding.etSearchAlbum.doOnTextChanged { text, _, _, _ ->
            viewModel.filterAlbums(text.toString())
        }
    }

    private fun setupRecyclerView() {
        albumAdapter = AlbumAdapter() { albumId ->
            val bundle = Bundle().apply {
                putInt("album_id", albumId)
            }

            findNavController().navigate(
                R.id.action_albumsListFragment_to_albumDetailFragment,
                bundle
            )
        }
        binding.rvAlbumList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = albumAdapter
            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}