package com.misw.app.ui.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.misw.app.R
import com.misw.app.databinding.ItemCollectorDetailCardBinding
import com.misw.app.databinding.ItemCollectorFavoriteArtistBinding

data class CollectorDetailItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val imageUrl: String,
    val rating: Int = 0,
    val type: DetailType
)

enum class DetailType { ALBUM, ARTIST }

class CollectorDetailAdapter(private val onItemClick: (Int, DetailType) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<CollectorDetailItem> = emptyList()

    companion object {
        const val VIEW_TYPE_ALBUM = 0
        const val VIEW_TYPE_ARTIST = 1
    }

    fun setData(newItems: List<CollectorDetailItem>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = newItems.size
            override fun areItemsTheSame(oldPos: Int, newPos: Int) =
                items[oldPos].id == newItems[newPos].id && items[oldPos].type == newItems[newPos].type
            override fun areContentsTheSame(oldPos: Int, newPos: Int) =
                items[oldPos] == newItems[newPos]
        })
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].type == DetailType.ALBUM) VIEW_TYPE_ALBUM else VIEW_TYPE_ARTIST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ALBUM) {
            val binding = ItemCollectorDetailCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            AlbumViewHolder(binding)
        } else {
            val binding = ItemCollectorFavoriteArtistBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ArtistViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is AlbumViewHolder) {
            holder.bind(item)
        } else if (holder is ArtistViewHolder) {
            holder.bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class AlbumViewHolder(private val binding: ItemCollectorDetailCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CollectorDetailItem) {
            binding.tvItemTitle.text = item.title
            binding.tvItemSubtitle.text = item.subtitle
            binding.tvItemDescription.text = item.description

            binding.root.contentDescription = "${item.title}, ${item.subtitle}, ${item.description}, calificación ${item.rating} de 5 estrellas"

            Glide.with(binding.ivItemImage.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_album)
                .into(binding.ivItemImage)

            binding.ivItemImage.contentDescription = "Portada de ${item.title}"

            setupRating(item.rating)

            binding.root.setOnClickListener {
                onItemClick(item.id, item.type)
            }
        }

        private fun setupRating(rating: Int) {
            binding.llRating.visibility = View.VISIBLE
            val stars = listOf(
                binding.star1, binding.star2, binding.star3, binding.star4, binding.star5
            )

            stars.forEachIndexed { index, imageView ->
                val colorId = if (index < rating) R.color.wild_strawberry else R.color.silver_chalice
                imageView.setColorFilter(ContextCompat.getColor(binding.root.context, colorId))
            }
        }
    }

    inner class ArtistViewHolder(private val binding: ItemCollectorFavoriteArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CollectorDetailItem) {
            binding.tvArtistName.text = item.title

            Glide.with(binding.ivArtistImage.context).clear(binding.ivArtistImage)

            if (item.imageUrl.isNotEmpty() && item.imageUrl.startsWith("http")) {
                binding.ivArtistPlaceholder.visibility = View.GONE
                binding.ivArtistImage.visibility = View.VISIBLE
                
                Glide.with(binding.ivArtistImage.context)
                    .load(item.imageUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.ivArtistPlaceholder.visibility = View.VISIBLE
                            binding.ivArtistImage.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.ivArtistPlaceholder.visibility = View.GONE
                            binding.ivArtistImage.visibility = View.VISIBLE
                            return false
                        }
                    })
                    .into(binding.ivArtistImage)
            } else {
                binding.ivArtistPlaceholder.visibility = View.VISIBLE
                binding.ivArtistImage.visibility = View.GONE
                binding.ivArtistImage.setImageDrawable(null)
            }

            binding.root.setOnClickListener {
                onItemClick(item.id, item.type)
            }
        }
    }
}
