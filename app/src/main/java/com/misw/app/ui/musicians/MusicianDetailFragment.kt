package com.misw.app.ui.musicians

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.shape.ShapeAppearanceModel
import com.misw.app.R
import com.misw.app.databinding.FragmentMusicianDetailBinding
import com.misw.app.ui.adapters.AlbumAdapter
import com.misw.app.viewmodel.MusicianDetailViewModel
import com.misw.app.viewmodel.SortCriterion
import java.text.SimpleDateFormat
import java.util.Locale

class MusicianDetailFragment : Fragment() {

    private val viewModel : MusicianDetailViewModel by viewModels()
    private lateinit var albumAdapter: AlbumAdapter

    private var _binding: FragmentMusicianDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicianDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupControls()
        setupSearch()

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.nestedScrollView.visibility = View.GONE
                binding.llEmptyState.visibility = View.GONE
            } else if (viewModel.error.value == null) {
                binding.nestedScrollView.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.llEmptyState.visibility = View.VISIBLE
                binding.nestedScrollView.visibility = View.GONE
                binding.tvEmptyState.text = getString(R.string.error_loading_content)
            } else if (viewModel.isLoading.value == false) {
                binding.llEmptyState.visibility = View.GONE
                binding.nestedScrollView.visibility = View.VISIBLE
            }
        }

        viewModel.musician.observe(viewLifecycleOwner) { musician ->
            binding.tvMusicianName.text = musician.name
            binding.tvMusicianBirthDate.text = formatDate(musician.birthDate)
            binding.tvDescription.text = musician.description

            binding.shimmerLayout.startShimmer()

            Glide.with(this)
                .load(musician.image)
                //.placeholder(R.drawable.ic_artists)
                .error(R.drawable.ic_artists)
                .listener(object  : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resrource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.hideShimmer()
                        return false
                    }
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.hideShimmer()
                        binding.shimmerLayout.background = null
                        binding.ivMusicianImage.shapeAppearanceModel = ShapeAppearanceModel()
                        return false
                    }
                })
                .into(binding.ivMusicianImage)
        }

        viewModel.prizes.observe(viewLifecycleOwner) { prizes ->
            val container = binding.flexPrizes
            container.removeAllViews()

            if (prizes.isEmpty()) {
                val pillView = layoutInflater.inflate(
                    R.layout.item_prize,
                    container,
                    false
                )
                pillView.setBackgroundResource(R.drawable.pill_prize_gray)
                pillView.findViewById<ImageView>(R.id.ivTrophy).setImageResource(R.drawable.ic_sad_face)
                pillView.findViewById<TextView>(R.id.tvPrize).text = getString(R.string.no_prizes)
                pillView.findViewById<TextView>(R.id.tvPrize).setTextColor(
                    ContextCompat.getColor(pillView.context, R.color.silver_chalice)
                )
                container.addView(pillView)
                return@observe
            }

            prizes.forEachIndexed { index, prize ->
                val pillView = layoutInflater.inflate(
                    R.layout.item_prize,
                    container,
                    false
                )
                pillView.findViewById<TextView>(R.id.tvPrize).text = prize.name
                if (index%2 != 0) {
                    pillView.setBackgroundResource(R.drawable.pill_prize_pink)
                    pillView.findViewById<ImageView>(R.id.ivTrophy).setColorFilter(
                        ContextCompat.getColor(pillView.context, R.color.prize_pink_text)
                    )
                    pillView.findViewById<TextView>(R.id.tvPrize).setTextColor(
                        ContextCompat.getColor(pillView.context, R.color.prize_pink_text)
                    )
                }
                container.addView(pillView)
            }
        }

        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            albumAdapter.updateAlbums(albums)
        }

        val musicianId = arguments?.getInt("musician_id") ?: 100
        viewModel.loadMusician(musicianId)
    }

    private fun setupControls() {
        binding.btnSortName.setOnClickListener {
            viewModel.setSortCriterion(SortCriterion.NAME)
            updateSortButtonsUI(isNameSelected = true)
        }

        binding.btnSortDate.setOnClickListener {
            viewModel.setSortCriterion(SortCriterion.RELEASE_DATE)
            updateSortButtonsUI(isNameSelected = false)
        }

        binding.btnSwapOrder.ibSwapOrder.setOnClickListener {
            viewModel.toggleSortOrder()
            binding.btnSwapOrder.ibSwapOrder.animate().rotationBy(180f).setDuration(300).start()
        }
    }

    private fun updateSortButtonsUI(isNameSelected: Boolean) {
        val pink = ContextCompat.getColor(requireContext(), R.color.wild_strawberry)
        val white = Color.WHITE
        val gray = Color.GRAY

        binding.btnSortName.backgroundTintList =
            ColorStateList.valueOf(if (isNameSelected) pink else Color.TRANSPARENT)
        binding.btnSortName.setTextColor(if (isNameSelected) white else gray)

        binding.btnSortDate.backgroundTintList =
            ColorStateList.valueOf(if (isNameSelected) Color.TRANSPARENT else pink)
        binding.btnSortDate.setTextColor(if (isNameSelected) gray else white)
    }

    private fun setupSearch() {
        binding.searchBar.etSearchAlbum.doOnTextChanged { text, _, _, _ ->
            viewModel.filterAlbums(text.toString())
        }
    }

    private fun setupRecyclerView() {
        albumAdapter = AlbumAdapter { albumId ->
            val bundle = Bundle().apply {
                putInt("album_id", albumId)
            }
            findNavController().navigate(
                R.id.action_musicianDetailFragment_to_albumDetailFragment, bundle
            )
        }
        binding.rvAlbums.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = albumAdapter
            setHasFixedSize(true)
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale("es"))
            val date = inputFormat.parse((dateString))
            val formatted = outputFormat.format(date!!)
            formatted.replaceFirstChar { it.uppercase() }
        } catch (ignored: Exception) {
            dateString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}