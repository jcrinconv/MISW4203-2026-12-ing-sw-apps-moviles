package com.misw.app.ui.collectors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayout
import com.misw.app.R
import com.misw.app.databinding.FragmentCollectorDetailBinding
import com.misw.app.model.Collector
import com.misw.app.model.CollectorAlbum
import com.misw.app.model.Comment
import com.misw.app.model.FavoritePerformer
import com.misw.app.ui.adapters.CollectorDetailAdapter
import com.misw.app.ui.adapters.CollectorDetailItem
import com.misw.app.ui.adapters.DetailType
import com.misw.app.viewmodel.CollectorViewModel

class CollectorDetailFragment : Fragment() {

    private var _binding: FragmentCollectorDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CollectorViewModel by viewModels()
    private lateinit var detailAdapter: CollectorDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectorDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstance: Bundle?) {
        super.onViewCreated(view, savedInstance)

        val collectorId = arguments?.getInt("collector_id") ?: return
        viewModel.fetchCollectorDetail(collectorId)

        setupRecyclerView()
        setupTabs()
        setupSearch()
        observeViewModel()

        //binding.btnBack.setOnClickListener {
        //    findNavController().navigateUp()
        //}
    }

    private fun setupRecyclerView() {
        detailAdapter = CollectorDetailAdapter { id, type ->
            if (type == DetailType.ALBUM) {
                findNavController().navigate(
                    R.id.action_collectorDetailFragment_to_albumDetailFragment,
                    bundleOf("album_id" to id)
                )
            } else if (type == DetailType.ARTIST) {
                findNavController().navigate(
                    R.id.action_collectorDetailFragment_to_musicianDetailFragment,
                    bundleOf("musician_id" to id)
                )
            }
        }
        
        val gridLayoutManager = GridLayoutManager(context, 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // Albums (tipo 0) ocupan 2 columnas, Artistas (tipo 1) ocupan 1 columna
                return if (detailAdapter.getItemViewType(position) == 0) 2 else 1
            }
        }

        binding.rvCollectorDetail.apply {
            layoutManager = gridLayoutManager
            adapter = detailAdapter
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val hintRes = if (tab?.position == 0) R.string.search_album else R.string.search_hint_artist
                binding.etSearch.hint = getString(hintRes)
                updateList(viewModel.collector.value, binding.etSearch.text.toString())
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupSearch() {
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            updateList(viewModel.collector.value, text.toString())
        }
    }

    private fun observeViewModel() {
        viewModel.collector.observe(viewLifecycleOwner) { collector ->
            collector?.let {
                binding.tvCollectorName.text = it.name.replace(" ", "\n")
                binding.tvCollectorEmail.text = it.email
                updateList(it, binding.etSearch.text.toString())
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun updateList(collector: Collector?, filter: String = "") {
        collector ?: return

        val commentMap = collector.comments.associateBy { it.id }

        val items = when (binding.tabLayout.selectedTabPosition) {
            0 -> collector.collectorAlbums.map { it.toDetailItem(commentMap) }
            1 -> collector.favoritePerformers.map { it.toDetailItem() }
            else -> emptyList()
        }

        val filteredItems = if (filter.isEmpty()) {
            items
        } else {
            items.filter { it.title.contains(filter, ignoreCase = true) }
        }

        detailAdapter.setData(filteredItems)
    }

    private fun CollectorAlbum.toDetailItem(commentMap: Map<Int, Comment>): CollectorDetailItem {
        val comment = commentMap[album?.id]
        return CollectorDetailItem(
            id = album?.id ?: 0,
            title = album?.name ?: "Álbum",
            subtitle = "${album?.recordLabel ?: ""} • ${album?.genre ?: ""}",
            description = comment?.description ?: "Sin comentarios",
            imageUrl = album?.cover ?: "",
            rating = comment?.rating ?: 0,
            type = DetailType.ALBUM
        )
    }

    private fun FavoritePerformer.toDetailItem() = CollectorDetailItem(
        id = id,
        title = name,
        subtitle = "",
        description = "",
        imageUrl = image,
        rating = 0,
        type = DetailType.ARTIST
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
