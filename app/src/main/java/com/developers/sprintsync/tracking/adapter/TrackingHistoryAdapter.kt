package com.developers.sprintsync.tracking.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.developers.sprintsync.databinding.ItemTrackingHistoryBinding
import com.developers.sprintsync.tracking.mapper.indicator.DistanceMapper
import com.developers.sprintsync.tracking.mapper.indicator.TimeMapper
import com.developers.sprintsync.tracking.model.Track

class TrackingHistoryAdapter : ListAdapter<Track, TrackingHistoryAdapter.TrackingHistoryViewHolder>(TrackDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TrackingHistoryViewHolder {
        TODO("Not yet implemented")
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
                tvDate.text = track.startTimeDateMillis.toString()
                tvDistance.text = DistanceMapper.metersToPresentableKilometers(track.distanceMeters)
                tvDuration.text = TimeMapper.millisToPresentableTime(track.durationMillis)
                tvCalories.text = track.calories.toString()
            }
        }
    }

    class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(
            oldItem: Track,
            newItem: Track,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Track,
            newItem: Track,
        ): Boolean = oldItem == newItem
    }
}
