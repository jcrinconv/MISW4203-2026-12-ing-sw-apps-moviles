package com.misw.app.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.misw.app.databinding.FragmentTrackAssociateBinding
import com.misw.app.viewmodel.TrackAssociateViewModel
import androidx.appcompat.app.AlertDialog
import com.misw.app.R
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.appbar.MaterialToolbar

class TrackAssociateFragment : Fragment() {

    private val viewModel: TrackAssociateViewModel by viewModels()
    private var _binding: FragmentTrackAssociateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackAssociateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val albumId = arguments?.getInt("album_id") ?: return

        setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBack()
                }
            })

        // Cancel button
        binding.btnCancel.setOnClickListener {
            val trackName = binding.etTrackName.text.toString()
            val minutos = binding.etMinutos.text.toString()
            val segundos = binding.etSegundos.text.toString()

            if (viewModel.areFieldsEmpty(trackName, minutos, segundos)) {
                findNavController().popBackStack()
            } else {
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Desea cancelar la asociación de la canción?")
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

        // Associate button
        binding.btnAssociate.setOnClickListener {
            val trackName = binding.etTrackName.text.toString()
            val minutos = binding.etMinutos.text.toString()
            val segundos = binding.etSegundos.text.toString()

            if (viewModel.isFormValid(trackName, minutos, segundos)) {
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle("¿Desea asociar la canción ${trackName}?")
                    .setMessage("Esta acción no se puede deshacer")
                    .setPositiveButton("Sí") {_, _ ->
                        viewModel.associateTrack(albumId, trackName, minutos, segundos)
                    }
                    .setNegativeButton("No", null)
                    .show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.prize_pink_text))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.prize_pink_text))
            }
            else {
                viewModel.associateTrack(albumId, trackName, minutos, segundos)
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnAssociate.isEnabled = !isLoading
            binding.btnCancel.isEnabled = !isLoading
        }

        // Observe error messages
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        // Observe association result
        viewModel.associationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Canción asociada exitosamente", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
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
        val fieldsEmpty = binding.etTrackName.text.isBlank() && binding.etMinutos.text.isBlank() && binding.etSegundos.text.isBlank()
        if (!fieldsEmpty) {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("¿Desea volver al detalle del álbum?")
                .setMessage("Esta eliminará su progreso")
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
