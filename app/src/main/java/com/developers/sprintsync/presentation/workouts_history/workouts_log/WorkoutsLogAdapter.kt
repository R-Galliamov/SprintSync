package com.developers.sprintsync.presentation.workouts_history.workouts_log

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.ImageLoader
import coil.request.ImageRequest
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.ItemTrackCardBinding
import java.io.File

class WorkoutsLogAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<WorkoutLogItem, WorkoutsLogAdapter.TrackListViewHolder>(TrackDiffCallback()) {
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
        fun bind(track: WorkoutLogItem) {
            binding.apply {
                tvDate.text = track.date
                tvDistanceValue.text = track.distance
                tvDurationValue.text = track.duration
                tvCaloriesValue.text = track.calories
                bindImage(ivMapPreview, track.previewPath)
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

    private fun bindImage(
        imageView: ImageView,
        filePath: String?,
    ) {
        val context = imageView.context
        val imageError = R.drawable.im_map_placeholder
        if (filePath == null) {
            imageView.setImageResource(imageError)
        } else {
            val imageLoader = ImageLoader(context)
            val request =
                ImageRequest
                    .Builder(context)
                    .error(imageError)
                    .data(File(filePath))
                    .target(imageView)
                    .build()
            imageLoader.enqueue(request)
        }
    }

    private class TrackDiffCallback : DiffUtil.ItemCallback<WorkoutLogItem>() {
        override fun areItemsTheSame(
            oldItem: WorkoutLogItem,
            newItem: WorkoutLogItem,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: WorkoutLogItem,
            newItem: WorkoutLogItem,
        ): Boolean = oldItem == newItem
    }

    interface OnInteractionListener {
        fun onItemSelected(trackId: Int)
    }
}
