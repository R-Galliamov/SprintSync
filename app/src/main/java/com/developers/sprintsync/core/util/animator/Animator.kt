package com.developers.sprintsync.core.util.animator

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Animator {
    companion object {
        fun startAnimation(
            startValue: Float,
            targetValue: Float,
            durationMillis: Long = ANIMATION_DURATION_MILLIS,
            animationUpdateListener: (Float) -> Unit,
            animationEndListener: (() -> Unit)? = null,
        ) {
            CoroutineScope(Dispatchers.Main).launch {
                val distance = targetValue - startValue

                // Log.d(TAG, "startX: $startValue, targetX: $targetValue, distance: $distance")

                val startTime = System.currentTimeMillis()
                val endTime = startTime + durationMillis

                val animator = ValueAnimator.ofFloat(ANIMATION_START_VALUE, ANIMATION_END_VALUE)
                animator.duration = durationMillis

                animator.addUpdateListener {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime < endTime) {
                        val progress = (currentTime - startTime) / durationMillis.toFloat()
                        val newValue = startValue + (distance * progress)
                        animationUpdateListener(newValue)
                        // Log.d(TAG, "new Value: $newValue")
                    } else {
                        animationUpdateListener(targetValue)
                        // Log.d(TAG, "final Value: $targetValue")
                    }
                }

                animator.addListener(
                    object : AnimatorListener {
                        override fun onAnimationStart(p0: Animator) {
                        }

                        override fun onAnimationEnd(p0: Animator) {
                            // Log.d(TAG, "onAnimationEnd")
                            animator.cancel()
                            animationUpdateListener(targetValue)
                            animationEndListener?.invoke()
                        }

                        override fun onAnimationCancel(p0: Animator) {
                        }

                        override fun onAnimationRepeat(p0: Animator) {
                        }
                    },
                )

                animator.start()
            }
        }

        private const val ANIMATION_DURATION_MILLIS = 200L
        private const val ANIMATION_START_VALUE = 0f
        private const val ANIMATION_END_VALUE = 1f

        private const val TAG = "My Stack: Animator"
    }
}
