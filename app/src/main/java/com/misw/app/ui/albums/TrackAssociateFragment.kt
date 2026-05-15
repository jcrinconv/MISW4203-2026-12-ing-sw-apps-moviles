package com.misw.app.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
        val albumId = requireArguments().getInt("album_id")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
