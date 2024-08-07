package com.developers.sprintsync.global.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.developers.sprintsync.R
import com.developers.sprintsync.global.styleProvider.AppThemeProvider

class CircularProgressBar(
    private val context: Context,
    private val attrs: AttributeSet,
) : View(context, attrs) {
    private var colors = AppThemeProvider(context).Color()

    private var progress = Attributes().getProgress()

    private var progressColor = Attributes().getProgressColor()

    private var backgroundColor = Attributes().getBackgroundColor()

    private var strokeWidth = Attributes().getStrokeWidth()

    private val backgroundPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = backgroundColor
            strokeCap = Paint.Cap.ROUND
            strokeWidth = this@CircularProgressBar.strokeWidth
        }

    private val progressPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = progressColor
            strokeCap = Paint.Cap.ROUND
            strokeWidth = this@CircularProgressBar.strokeWidth
        }

    private val ovalBounds = RectF()

    private inner class Attributes {
        private val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar)

        fun getProgress() = typedArray.getInt(R.styleable.CircularProgressBar_progress, DEFAULT_PROGRESS)

        fun getProgressColor() =
            typedArray.getColor(
                R.styleable.CircularProgressBar_progressColor,
                getDefaultProgressColor(),
            )

        fun getBackgroundColor() =
            typedArray.getColor(
                R.styleable.CircularProgressBar_backgroundColor,
                getDefaultBackgroundColor(),
            )

        fun getStrokeWidth() =
            typedArray.getDimension(
                R.styleable.CircularProgressBar_strokeWidth,
                DEFAULT_STROKE_WIDTH,
            )
    }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int,
    ) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = strokeWidth / 2
        ovalBounds.set(padding, padding, w.toFloat() - padding, h.toFloat() - padding)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawOval(ovalBounds, backgroundPaint)
        val angle = FULL_CIRCLE_DEGREES * progress / MAX_PROGRESS.toFloat()
        canvas.drawArc(ovalBounds, START_ANGLE_DEGREES, angle, false, progressPaint)
    }

    fun setStrokeWidth(strokeWidth: Float) {
        this.strokeWidth = strokeWidth
        invalidate()
    }

    fun setProgress(progress: Int) {
        this.progress =
            when {
                progress < MIN_PROGRESS -> MIN_PROGRESS
                progress > MAX_PROGRESS -> MAX_PROGRESS
                else -> progress
            }
        invalidate()
    }

    fun setProgressColor(color: Int) {
        progressColor = color
        progressPaint.color = progressColor
        invalidate()
    }

    override fun setBackgroundColor(color: Int) {
        backgroundColor = color
        backgroundPaint.color = backgroundColor
        invalidate()
    }

    private fun getDefaultProgressColor(): Int = colors.thirdly

    private fun getDefaultBackgroundColor(): Int = colors.secondary

    companion object {
        private const val DEFAULT_STROKE_WIDTH = 20f
        private const val DEFAULT_PROGRESS = 0
        private const val MIN_PROGRESS = 0
        private const val MAX_PROGRESS = 100
        private const val FULL_CIRCLE_DEGREES = 360f
        private const val START_ANGLE_DEGREES = -90f
    }
}
