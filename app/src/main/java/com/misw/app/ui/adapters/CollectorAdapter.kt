package com.misw.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.misw.app.databinding.ItemCollectorBinding
import com.misw.app.model.Collector

class CollectorAdapter(private val onCollectorClick: (Int) -> Unit) :
    ListAdapter<Collector, CollectorAdapter.CollectorViewHolder>(CollectorDiffCallback()) {

    inner class CollectorViewHolder(private val binding: ItemCollectorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(collector: Collector) {
            binding.tvCollectorName.text = collector.name
            binding.tvCollectorEmail.text = collector.email

            binding.root.contentDescription = "Coleccionista: ${collector.name}, Correo: ${collector.email}"

            binding.root.setOnClickListener {
                onCollectorClick(collector.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectorViewHolder {
        val binding = ItemCollectorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateCollectors(newCollectors: List<Collector>) {
        submitList(newCollectors)
    }

    class CollectorDiffCallback : DiffUtil.ItemCallback<Collector>() {
        override fun areItemsTheSame(oldItem: Collector, newItem: Collector): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Collector, newItem: Collector): Boolean {
            return oldItem == newItem
        }
    }
}
