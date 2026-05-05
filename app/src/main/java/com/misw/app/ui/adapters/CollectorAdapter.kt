package com.misw.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.misw.app.databinding.ItemCollectorBinding
import com.misw.app.model.Collector

class CollectorAdapter(private val onCollectorClick: (Int) -> Unit) :
    RecyclerView.Adapter<CollectorAdapter.CollectorViewHolder>() {

    private var collectors: List<Collector> = emptyList()
    inner class CollectorViewHolder(private val binding: ItemCollectorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(collector: Collector) {
            binding.tvCollectorName.text = collector.name
            binding.tvCollectorEmail.text = collector.email

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
        holder.bind(collectors[position])
    }

    override fun getItemCount(): Int = collectors.size

    fun updateCollectors(newCollectors: List<Collector>) {
        this.collectors = newCollectors
        notifyDataSetChanged()
    }
}