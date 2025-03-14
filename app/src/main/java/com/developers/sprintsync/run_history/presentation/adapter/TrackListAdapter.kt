package com.developers.sprintsync.run_history.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.ImageLoader
import coil.request.ImageRequest
import com.developers.sprintsync.databinding.ItemTrackCardBinding
import com.developers.sprintsync.run_history.presentation.ui_model.UiTrackPreviewWrapper
import java.io.File

class TrackListAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<UiTrackPreviewWrapper, TrackListAdapter.TrackListViewHolder>(TrackDiffCallback()) {
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
        fun bind(track: UiTrackPreviewWrapper) {
            binding.apply {
                tvDate.text = track.date
                tvDistanceValue.text = track.distance
                tvDurationValue.text = track.duration
                tvCaloriesValue.text = track.calories
                if (track.previewPath != null) {
                    bindImage(ivMapPreview, track.previewPath)
                } else {
                    // TODO set default image
                }

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
        filePath: String,
    ) {
        val context = imageView.context
        val imageLoader = ImageLoader(context)
        val request =
            ImageRequest
                .Builder(context)
                .data(File(filePath))
                .target(imageView)
                .build()
        imageLoader.enqueue(request)
    }

    private class TrackDiffCallback : DiffUtil.ItemCallback<UiTrackPreviewWrapper>() {
        override fun areItemsTheSame(
            oldItem: UiTrackPreviewWrapper,
            newItem: UiTrackPreviewWrapper,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: UiTrackPreviewWrapper,
            newItem: UiTrackPreviewWrapper,
        ): Boolean = oldItem == newItem
    }

    interface OnInteractionListener {
        fun onItemSelected(trackId: Int)
    }
}
