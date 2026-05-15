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
import androidx.recyclerview.widget.LinearLayoutManager
import com.misw.app.R
import com.misw.app.databinding.FragmentCollectorListBinding
import com.misw.app.ui.adapters.CollectorAdapter
import com.misw.app.viewmodel.CollectorViewModel

class CollectorListFragment : Fragment() {

    private val viewModel: CollectorViewModel by viewModels()
    private var _binding: FragmentCollectorListBinding? = null
    private val binding get() = _binding!!
    private lateinit var collectorAdapter: CollectorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectorListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstance: Bundle?) {
        super.onViewCreated(view, savedInstance)

        setupRecyclerView()
        observeViewModel()
        setupSearch()
        viewModel.fetchCollectors()
    }

    private fun setupRecyclerView() {
        collectorAdapter = CollectorAdapter { collectorId ->
            val bundle = Bundle().apply {
                putInt("collector_id", collectorId)
            }
            findNavController().navigate(
                R.id.action_collectorListFragment_to_collectorDetailFragment,
                bundle
            )
        }
        binding.rvCollectorList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = collectorAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearch() {
        if (binding.collectorsSearchBar.etSearchAlbum.text.toString() != viewModel.query.value) {
            binding.collectorsSearchBar.etSearchAlbum.setText(viewModel.query.value)
        }

        binding.collectorsSearchBar.etSearchAlbum.doOnTextChanged { text, _, _, _ ->
            viewModel.filterCollectors(text.toString())
        }
    }

    private fun observeViewModel() {
        viewModel.collectors.observe(viewLifecycleOwner) { collectors ->
            collectorAdapter.updateCollectors(collectors)
            updateUIState(collectors, viewModel.error.value)

        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            updateUIState(viewModel.collectors.value ?: emptyList<Any>(), errorMessage)

        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbCollectorList.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.llEmptyState.visibility = View.GONE
            }
        }
    }

    private fun updateUIState(collectors: List<*>, error: String?) {
        when {
            error != null -> {
                binding.llEmptyState.visibility = View.VISIBLE
                binding.rvCollectorList.visibility = View.GONE
                binding.tvEmptyState.text = error
                binding.ivEmptyState.setImageResource(android.R.drawable.stat_notify_error)
            }

            collectors.isEmpty() -> {
                binding.llEmptyState.visibility = View.VISIBLE
                binding.rvCollectorList.visibility = View.GONE
                binding.tvEmptyState.text = getString(R.string.no_collectors_found)
                binding.ivEmptyState.setImageResource(R.drawable.ic_people)
            }

            else -> {
                binding.llEmptyState.visibility = View.GONE
                binding.rvCollectorList.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
