package com.developers.sprintsync.tracking.analytics.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.developers.sprintsync.databinding.ItemTrackingHistoryBinding
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.DateFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.DurationFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.mapper.indicator.DistanceMapper
import com.developers.sprintsync.tracking.session.model.track.Track

class TrackListAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Track, TrackListAdapter.TrackingHistoryViewHolder>(TrackDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TrackingHistoryViewHolder {
        val binding =
            ItemTrackingHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackingHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TrackingHistoryViewHolder,
        position: Int,
    ) {
        val track = getItem(position)
        holder.bind(track)
    }

    inner class TrackingHistoryViewHolder(
        private val binding: ItemTrackingHistoryBinding,
    ) : ViewHolder(binding.root) {
        fun bind(track: Track) {
            binding.apply {
                // TODO: set date mapper and calories mapper
                tvDate.text = DateFormatter.formatDate(track.timestamp)
                tvDistance.text =
                    DistanceMapper.metersToPresentableKilometers(track.distanceMeters, true)
                tvDuration.text = DurationFormatter.formatToHhMmSs(track.durationMillis)
                tvCalories.text = track.calories.toString()
                itemView.setOnClickListener {
                    onInteractionListener.onItemSelected(track.id)
                }
            }
        }
    }

    private class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(
            oldItem: Track,
            newItem: Track,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Track,
            newItem: Track,
        ): Boolean = oldItem == newItem
    }

    interface OnInteractionListener {
        fun onItemSelected(trackId: Int)
    }
}