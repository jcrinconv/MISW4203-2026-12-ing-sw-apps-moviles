package com.misw.app.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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

        setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBack()
                }
            })

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
            val name = binding.etAlbumName.text.toString()
            val cover = binding.etCoverUrl.text.toString()
            val description = binding.etDescription.text.toString()
            val day = binding.etDay.text.toString()
            val monthIndex = binding.spinnerMonth.selectedItemPosition
            val year = binding.etYear.text.toString()
            val genreIndex = binding.spinnerGenre.selectedItemPosition
            val recordLabelIndex = binding.spinnerRecordLabel.selectedItemPosition

            if (viewModel.areFieldsEmpty(name, cover, description, day, monthIndex, year, genreIndex, recordLabelIndex)) {
                findNavController().navigateUp()
            } else {
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Desea cancelar la creación del álbum?")
                    .setMessage("Esta acción no se puede deshacer")
                    .setPositiveButton("Sí") {_, _ ->
                        findNavController().popBackStack()
                    }
                    .setNegativeButton("No", null)
                    .show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.prize_pink_text))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.prize_pink_text))
            }
        }

        binding.btnCreate.setOnClickListener {
            val name = binding.etAlbumName.text.toString()
            val cover = binding.etCoverUrl.text.toString()
            val description = binding.etDescription.text.toString()
            val day = binding.etDay.text.toString()
            val monthIndex = binding.spinnerMonth.selectedItemPosition
            val year = binding.etYear.text.toString()
            val genre = binding.spinnerGenre.selectedItem?.toString() ?: ""
            val recordLabel = binding.spinnerRecordLabel.selectedItem?.toString() ?: ""

            if (viewModel.validateFields(name, cover, description, day, monthIndex, year, genre, recordLabel)) {
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Desea crear el álbum ${name}?")
                    .setMessage("Esta acción no se puede deshacer")
                    .setPositiveButton("Sí") {_, _ ->
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
                    .setNegativeButton("No", null)
                    .show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.prize_pink_text))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.prize_pink_text))
            }
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

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            handleBack()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleBack() {
        val name = binding.etAlbumName.text.toString()
        val cover = binding.etCoverUrl.text.toString()
        val description = binding.etDescription.text.toString()
        val day = binding.etDay.text.toString()
        val monthIndex = binding.spinnerMonth.selectedItemPosition
        val year = binding.etYear.text.toString()
        val genreIndex = binding.spinnerGenre.selectedItemPosition
        val recordLabelIndex = binding.spinnerRecordLabel.selectedItemPosition

        val fieldsEmpty = viewModel.areFieldsEmpty(name, cover, description, day, monthIndex, year, genreIndex, recordLabelIndex)
        if (!fieldsEmpty) {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("¿Desea volver al listado de álbumes?")
                .setMessage("Esto eliminará el contenido de los campos que haya llenado")
                .setPositiveButton("Sí") {_, _ ->
                    findNavController().popBackStack()
                }
                .setNegativeButton("No", null)
                .show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.prize_pink_text))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.prize_pink_text))
        } else {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
