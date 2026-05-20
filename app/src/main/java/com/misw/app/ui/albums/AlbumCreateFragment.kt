package com.misw.app.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.misw.app.R
import com.misw.app.databinding.FragmentAlbumCreateBinding
import com.misw.app.viewmodel.AlbumCreateViewModel

class AlbumCreateFragment : Fragment() {

    private val viewModel: AlbumCreateViewModel by viewModels()
    private var _binding: FragmentAlbumCreateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumCreateBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        setupButtons()
        observeViewModel()
    }

    private fun setupSpinners() {
        // Months Spinner
        val months = resources.getStringArray(R.array.months_array)
        val monthAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, months)
        binding.spinnerMonth.adapter = monthAdapter

        // Genres Spinner
        viewModel.genres.observe(viewLifecycleOwner) { genres ->
            val genreAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, genres)
            binding.spinnerGenre.adapter = genreAdapter
        }

        // Record Labels Spinner
        viewModel.recordLabels.observe(viewLifecycleOwner) { recordLabels ->
            val recordLabelAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, recordLabels)
            binding.spinnerRecordLabel.adapter = recordLabelAdapter
        }
    }

    private fun setupButtons() {
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCreate.setOnClickListener {
            viewModel.createAlbum(
                name = binding.etAlbumName.text.toString(),
                cover = binding.etCoverUrl.text.toString(),
                description = binding.etDescription.text.toString(),
                day = binding.etDay.text.toString(),
                monthIndex = binding.spinnerMonth.selectedItemPosition,
                year = binding.etYear.text.toString(),
                genre = binding.spinnerGenre.selectedItem?.toString() ?: "",
                recordLabel = binding.spinnerRecordLabel.selectedItem?.toString() ?: ""
            )
        }
    }

    private fun observeViewModel() {
        viewModel.isSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), R.string.album_created_success, Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigateUp()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
