package com.developers.spryntsync.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.developers.spryntsync.R
import com.developers.spryntsync.ui.AppThemeManager

class CircularProgressBar(context: Context, attrs: AttributeSet) :
    View(context, attrs) {

    private val appThemeManager = AppThemeManager(context)
    val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar)

    private var progress =
        typedArray.getInt(R.styleable.CircularProgressBar_progress, DEFAULT_PROGRESS)
    private var progressColor = typedArray.getColor(
        R.styleable.CircularProgressBar_progressColor,
        getDefaultProgressColor()
    )
    private var backgroundColor = typedArray.getColor(
        R.styleable.CircularProgressBar_backgroundColor,
        getDefaultBackgroundColor()
    )
    private var strokeWidth =
        typedArray.getDimension(R.styleable.CircularProgressBar_strokeWidth, DEFAULT_STROKE_WIDTH)

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = backgroundColor
        strokeCap = Paint.Cap.ROUND
        strokeWidth = this@CircularProgressBar.strokeWidth
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = progressColor
        strokeCap = Paint.Cap.ROUND
        strokeWidth = this@CircularProgressBar.strokeWidth
    }

    private val ovalBounds = RectF()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = strokeWidth / 2
        ovalBounds.set(padding, padding, w.toFloat() - padding, h.toFloat() - padding)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawOval(ovalBounds, backgroundPaint)
        val angle = 360 * progress / 100f
        canvas.drawArc(ovalBounds, -90f, angle, false, progressPaint)
    }

    fun setStrokeWidth(strokeWidth: Float) {
        this.strokeWidth = strokeWidth
        invalidate()
    }

    fun setProgress(progress: Int) {
        this.progress = when {
            progress < 0 -> 0
            progress > 100 -> 100
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

    private fun getDefaultProgressColor(): Int =
        appThemeManager.getSecondaryColor()

    private fun getDefaultBackgroundColor(): Int = appThemeManager.getPrimaryColor()


    companion object {
        private const val DEFAULT_STROKE_WIDTH = 20f
        private const val DEFAULT_PROGRESS = 0
    }
}