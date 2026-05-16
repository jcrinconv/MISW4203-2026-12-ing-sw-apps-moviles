package com.misw.app.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.misw.app.databinding.FragmentTrackAssociateBinding
import com.misw.app.viewmodel.TrackAssociateViewModel

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

        // Cancel button
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        // Associate button
        binding.btnAssociate.setOnClickListener {
            val trackName = binding.etTrackName.text.toString()
            val minutos = binding.etMinutos.text.toString()
            val segundos = binding.etSegundos.text.toString()

            viewModel.associateTrack(albumId, trackName, minutos, segundos)
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnAssociate.isEnabled = !isLoading
            binding.btnCancel.isEnabled = !isLoading
        }

        // Observe error messages
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                android.widget.Toast.makeText(requireContext(), errorMessage, android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        // Observe association result
        viewModel.associationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
