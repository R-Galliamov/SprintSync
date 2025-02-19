package com.developers.sprintsync.run_history.presentation.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.ImageLoader
import coil.request.ImageRequest
import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.CaloriesFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiPattern
import com.developers.sprintsync.core.util.formatter.DateFormatter
import com.developers.sprintsync.databinding.ItemTrackCardBinding

class TrackListAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Track, TrackListAdapter.TrackListViewHolder>(TrackDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TrackListViewHolder {
        val binding =
            ItemTrackCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackListViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TrackListViewHolder,
        position: Int,
    ) {
        val track = getItem(position)
        holder.bind(track)
    }

    inner class TrackListViewHolder(
        private val binding: ItemTrackCardBinding,
    ) : ViewHolder(binding.root) {
        fun bind(track: Track) {
            binding.apply {
                // TODO: provide with FormattedTrack data class
                tvDate.text = formatDate(track.timestamp)
                tvDistanceValue.text = formatDistance(track.distanceMeters)
                tvDurationValue.text = formatDuration(track.durationMillis)
                tvCaloriesValue.text = formatCalories(track.calories)

                // TODO set bitmap for ivMapPreview

                itemView.setOnClickListener {
                    onInteractionListener.onItemSelected(track.id)
                }
            }
        }

        fun unbind() {
            binding.ivMapPreview.setImageDrawable(null)
        }
    }

    override fun onViewRecycled(holder: TrackListViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    private fun formatDate(timestamp: Long): String = DateFormatter.formatDate(timestamp, DateFormatter.Pattern.DAY_MONTH_YEAR_WEEK_DAY)

    private fun formatDistance(distanceMeters: Float): String =
        DistanceUiFormatter.format(distanceMeters, DistanceUiPattern.WITH_UNIT)

    private fun formatDuration(durationMillis: Long): String = DurationFormatter.formatToHhMmSs(durationMillis)

    private fun formatCalories(calories: Int): String = CaloriesFormatter.formatCalories(calories, true)

    private fun loadBitmapIntoImageView(
        imageView: ImageView,
        bitmap: Bitmap,
    ) {
        val context = imageView.context
        val imageLoader = ImageLoader(context)
        val request =
            ImageRequest
                .Builder(context)
                .data(bitmap)
                .target(imageView)
                .build()
        imageLoader.enqueue(request)
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
