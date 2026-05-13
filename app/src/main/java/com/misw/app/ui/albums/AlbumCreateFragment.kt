package com.misw.app.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.misw.app.databinding.FragmentAlbumCreateBinding
import com.misw.app.viewmodel.AlbumCreateViewModel

class AlbumCreateFragment : Fragment() {

    private val viewModel: AlbumCreateViewModel by viewModels()
    private var _binding : FragmentAlbumCreateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}