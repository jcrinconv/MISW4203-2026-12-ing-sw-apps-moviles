package com.misw.app.ui.albums

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
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

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_album_list, menu)
                val item = menu.findItem(R.id.action_create)
                item.actionView?.findViewById<Button>(R.id.buttonCreate)?.setOnClickListener {
                    findNavController().navigate(R.id.action_albumsListFragment_to_albumCreateFragment)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner)

        setupRecyclerView()
        setupControls()
        setupSearch()
        observeViewModel()

        updateSortButtonsUI(true)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchAlbums()
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

        binding.btnSwapOrder.ibSwapOrder.setOnClickListener {
            viewModel.toggleSortOrder()
            // Rotación visual del icono de swap
            binding.btnSwapOrder.ibSwapOrder.animate().rotationBy(180f).setDuration(300).start()
        }
    }

    private fun updateSortButtonsUI(isNameSelected: Boolean) {
        val pink = ContextCompat.getColor(requireContext(), R.color.wild_strawberry)
        val white = Color.WHITE
        val gray = Color.GRAY

        binding.btnSortName.backgroundTintList =
            ColorStateList.valueOf(if (isNameSelected) pink else Color.TRANSPARENT)
        binding.btnSortName.setTextColor(if (isNameSelected) white else gray)

        binding.btnSortDate.backgroundTintList =
            ColorStateList.valueOf(if (isNameSelected) Color.TRANSPARENT else pink)
        binding.btnSortDate.setTextColor(if (isNameSelected) gray else white)
    }

    private fun setupSearch() {
        if (binding.searchBar.etSearchAlbum.text.toString() != viewModel.query.value) {
            binding.searchBar.etSearchAlbum.setText(viewModel.query.value)
        }

        binding.searchBar.etSearchAlbum.doOnTextChanged { text, _, _, _ ->
            viewModel.filterAlbums(text.toString())
        }
    }

    private fun setupRecyclerView() {
        albumAdapter = AlbumAdapter { albumId ->
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

    private fun observeViewModel() {
        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            albumAdapter.updateAlbums(albums)
            updateUIState(albums, viewModel.error.value)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            updateUIState(viewModel.albums.value ?: emptyList<Any>(), errorMessage)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbAlbumList.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.llEmptyState.visibility = View.GONE
            }
        }
    }

    private fun updateUIState(albums: List<*>, error: String?) {
        when {
            error != null -> {
                binding.llEmptyState.visibility = View.VISIBLE
                binding.rvAlbumList.visibility = View.GONE
                binding.tvEmptyState.text = error
                binding.ivEmptyState.setImageResource(android.R.drawable.stat_notify_error)
            }

            albums.isEmpty() -> {
                binding.llEmptyState.visibility = View.VISIBLE
                binding.rvAlbumList.visibility = View.GONE
                binding.tvEmptyState.text = getString(R.string.no_albums_found)
                binding.ivEmptyState.setImageResource(R.drawable.ic_album)
            }

            else -> {
                binding.llEmptyState.visibility = View.GONE
                binding.rvAlbumList.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}