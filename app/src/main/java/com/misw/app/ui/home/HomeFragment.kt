package com.misw.app.ui.home

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.misw.app.R
import com.misw.app.databinding.FragmentHomeBinding
import com.misw.app.databinding.ItemMenuCardBinding
import com.misw.app.databinding.ItemMenuSmallCardBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.app_name)

        setupHomeButtons()
    }

    private fun setupMenuButton(
        binding: ItemMenuCardBinding,
        title: String,
        subtitle: String,
        iconRes: Int,
        iconBackgroundColor: Int,
        onAction: () -> Unit
    ) {
        binding.apply {
            tvMenuTitle.text = title
            tvMenuSubtitle.text = subtitle
            ivMenuIcon.setImageResource(iconRes)
            iconWrapper.backgroundTintList = ColorStateList.valueOf(iconBackgroundColor)
            menuCardContainer.setOnClickListener {
                onAction()
            }
        }
    }

    private fun setupMenuSmallButton(
        binding: ItemMenuSmallCardBinding,
        title: String,
        iconRes: Int,
        iconBackgroundColor: Int,
        onAction: () -> Unit
    ) {
        binding.apply {
            tvMenuTitle.text = title
            ivMenuIcon.setImageResource(iconRes)
            iconWrapper.backgroundTintList = ColorStateList.valueOf(iconBackgroundColor)
            menuCardContainer.setOnClickListener {
                onAction()
            }
        }
    }

    private fun setupHomeButtons() {
        val context = requireContext()

        setupMenuButton(
            binding = ItemMenuCardBinding.bind(binding.includeAlbums.root),
            title = getString(R.string.title_albums),
            subtitle = getString(R.string.subtitle_albums),
            iconRes = R.drawable.ic_album,
            iconBackgroundColor = ContextCompat.getColor(context, R.color.wild_strawberry)
        ) {
            findNavController().navigate(R.id.action_homeFragment_to_albumListFragment)
        }

        setupMenuSmallButton(
            binding = ItemMenuSmallCardBinding.bind(binding.includeArtists.root),
            title = getString(R.string.title_artists),
            iconRes = R.drawable.ic_artists,
            iconBackgroundColor = ContextCompat.getColor(context, R.color.bright_ube)
        ) {
            findNavController().navigate(R.id.action_homeFragment_to_musicianListFragment)
        }

        setupMenuSmallButton(
            binding = ItemMenuSmallCardBinding.bind(binding.includeTracks.root),
            title = getString(R.string.title_tracks),
            iconRes = R.drawable.ic_tracks,
            iconBackgroundColor = ContextCompat.getColor(context, R.color.malibu)
        ) {
            Toast.makeText(context, R.string.coming_soon, Toast.LENGTH_SHORT).show()
        }

        setupMenuButton(
            binding = ItemMenuCardBinding.bind(binding.includeCollectors.root),
            title = getString(R.string.title_collectors),
            subtitle = getString(R.string.subtitle_collectors),
            iconRes = R.drawable.ic_people,
            iconBackgroundColor = ContextCompat.getColor(context, R.color.bright_ube)
        ) {
            findNavController().navigate(R.id.action_homeFragment_to_collectorListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
