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
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.databinding.ItemWorkoutCardBinding
import java.io.File
import javax.inject.Inject

/**
 * Adapter for displaying a list of workout logs in a RecyclerView.
 */
class WorkoutsLogAdapter @Inject constructor(
    private val log: AppLogger,
) : ListAdapter<WorkoutLogItem, WorkoutsLogAdapter.WorkoutViewHolder>(WorkoutDiffCallback()) {

    var onInteractionListener: OnInteractionListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): WorkoutViewHolder {
        try {
            val binding = ItemWorkoutCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
            log.d("Created view holder for workout item")
            return WorkoutViewHolder(binding)
        } catch (e: Exception) {
            log.e("Error creating view holder", e)
            throw e
        }
    }

    override fun onBindViewHolder(
        holder: WorkoutViewHolder,
        position: Int,
    ) {
        try {
            val workout = getItem(position)
            holder.bind(workout)
            log.d("Bound workout item: id=${workout.id}, position=$position")
        } catch (e: Exception) {
            log.e("Error binding workout item at position=$position", e)
        }
    }

    inner class WorkoutViewHolder(
        private val binding: ItemWorkoutCardBinding,
    ) : ViewHolder(binding.root) {
        fun bind(workout: WorkoutLogItem) {
            try {
                binding.apply {
                    tvDate.text = workout.date
                    tvDistanceValue.text = workout.distance
                    tvDurationValue.text = workout.duration
                    tvCaloriesValue.text = workout.calories
                    bindImage(ivMapPreview, workout.previewPath)
                    itemView.setOnClickListener {
                        onInteractionListener?.onItemSelected(workout.id)
                    }
                }
            } catch (e: Exception) {
                log.e("Error binding workout: id=${workout.id}", e)
            }

        }

        // Clears image to prevent recycling issues
        fun unbind() {
            binding.ivMapPreview.setImageDrawable(null)
            log.d("Unbound workout view holder")
        }
    }

    override fun onViewRecycled(holder: WorkoutViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    // Loads image into ImageView or sets placeholder
    private fun bindImage(
        imageView: ImageView,
        filePath: String?,
    ) {
        try {
            val context = imageView.context
            val placeholder = R.drawable.im_map_placeholder
            if (filePath == null) {
                imageView.setImageResource(placeholder)
                log.d("Set placeholder image for null filePath")
            } else {
                val imageLoader = ImageLoader(context)
                val request =
                    ImageRequest
                        .Builder(context)
                        .error(placeholder)
                        .data(File(filePath))
                        .target(imageView)
                        .build()
                imageLoader.enqueue(request)
                log.d("Loading image from filePath: $filePath")
            }
        } catch (e: Exception) {
            log.e("Error loading image for filePath=$filePath", e)
            imageView.setImageResource(R.drawable.im_map_placeholder)
        }

    }

    private class WorkoutDiffCallback : DiffUtil.ItemCallback<WorkoutLogItem>() {
        override fun areItemsTheSame(
            oldItem: WorkoutLogItem,
            newItem: WorkoutLogItem,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: WorkoutLogItem,
            newItem: WorkoutLogItem,
        ): Boolean = oldItem == newItem
    }

    /**
     * Listener for handling workout item interactions.
     */
    interface OnInteractionListener {
        fun onItemSelected(trackId: Int)
    }
}
