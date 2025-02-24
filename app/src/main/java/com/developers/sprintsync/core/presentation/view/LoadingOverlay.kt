package com.developers.sprintsync.core.presentation.view

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.developers.sprintsync.databinding.ViewLoadingOverlayBinding

class LoadingOverlay(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    // TODO add loading text

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val binding = ViewLoadingOverlayBinding.inflate(LayoutInflater.from(context), this, true)
    private val animation: AnimatedVectorDrawable? = binding.loadingAnimation.drawable as? AnimatedVectorDrawable

    val isVisible get() = visibility == View.VISIBLE

    init {
        visibility = View.GONE
    }

    fun show() {
        visibility = View.VISIBLE
        animation?.start()
    }

    fun hide() {
        visibility = View.GONE
        animation?.stop()
    }

    fun bindToLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    animation?.stop()
                }
            },
        )
    }
}
