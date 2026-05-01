package com.misw.app.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        (activity as AppCompatActivity).supportActionBar?.title = "Vynils App"

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
            iconWrapper.backgroundTintList =
                android.content.res.ColorStateList.valueOf(iconBackgroundColor)
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
            iconWrapper.backgroundTintList =
                android.content.res.ColorStateList.valueOf(iconBackgroundColor)
            menuCardContainer.setOnClickListener {
                onAction()
            }
        }
    }

    private fun setupHomeButtons() {
        val albumBinding = ItemMenuCardBinding.bind(binding.includeAlbums.root)
        setupMenuButton(
            binding = albumBinding,
            title = "Álbumes",
            subtitle = "Explora colecciones",
            iconRes = R.drawable.ic_album,
            iconBackgroundColor = androidx.core.content.ContextCompat.getColor(
                requireContext(), R.color.wild_strawberry
            )
        ) {
            findNavController().navigate(R.id.action_homeFragment_to_albumListFragment)
        }

        val artistsBinding = ItemMenuSmallCardBinding.bind(binding.includeArtists.root)
        setupMenuSmallButton(
            binding = artistsBinding,
            title = "Artistas",
            iconRes = R.drawable.ic_artists,
            iconBackgroundColor = androidx.core.content.ContextCompat.getColor(
                requireContext(),
                R.color.bright_ube,
            )
        ) {
            findNavController().navigate(R.id.action_homeFragment_to_musicianListFragment)
        }

        val tracksBinding = ItemMenuSmallCardBinding.bind(binding.includeTracks.root)
        setupMenuSmallButton(
            binding = tracksBinding,
            title = "Tracks",
            iconRes = R.drawable.ic_tracks,
            iconBackgroundColor = androidx.core.content.ContextCompat.getColor(
                requireContext(),
                R.color.malibu,
            )
        ) {
            Toast.makeText(context, "Próximamente...", Toast.LENGTH_SHORT).show()
        }

        val collectorsBinding = ItemMenuCardBinding.bind(binding.includeCollectors.root)
        setupMenuButton(
            binding = collectorsBinding,
            title = "Coleccionistas",
            subtitle = "Comunidad de élite",
            iconRes = R.drawable.ic_people,
            iconBackgroundColor = androidx.core.content.ContextCompat.getColor(
                requireContext(),
                R.color.bright_ube,
            )
        ) {
            Toast.makeText(context, "Próximamente...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
