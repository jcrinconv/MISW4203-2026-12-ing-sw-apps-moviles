package com.misw.app.ui.musicians

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.misw.app.databinding.FragmentMusicianDetailBinding
import com.misw.app.viewmodel.MusicianDetailViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.misw.app.R
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.android.material.shape.ShapeAppearanceModel

class MusicianDetailFragment : Fragment() {

    private val viewModel : MusicianDetailViewModel by viewModels()

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

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.nestedScrollView.visibility = if (isLoading) View.GONE else View.VISIBLE
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
                pillView.findViewById<TextView>(R.id.tvPrize).text = "Sin premios"
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

        val musicianId = arguments?.getInt("musician_id") ?: 100
        viewModel.loadMusician(musicianId)
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale("es"))
            val date = inputFormat.parse((dateString))
            val formatted = outputFormat.format(date!!)
            formatted.replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            dateString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}