package com.developers.sprintsync.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.developers.sprintsync.R

class CounterView @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var icon: Drawable? = null
    private var progress: Int = DEFAULT_PROGRESS
    private var maxNumber: Int = DEFAULT_MAX_NUMBER
    private var units: String? = null

    private var progressTextSize: Float = DEFAULT_TEXT_SIZE
    private var textSize: Float = DEFAULT_TEXT_SIZE

    private var imageView: ImageView? = null
    private var progressTextView: TextView? = null
    private var slashTextView: TextView? = null
    private var maxNumberTextView: TextView? = null
    private var unitsTextView: TextView? = null

    private var textColor: Int? = null

    init {
        attrs?.let { setAttrs() }
        setConfigs()
        setProgressTextView()
        setSplashTextView()
        setMaxNumberTextView()
        icon?.let { setImageView() }
        if (!units.isNullOrBlank()) {
            setUnitsTextView()
        }
        addViews()
    }

    fun setProgress(int: Int) {
        progressTextView?.text = int.toString()
    }

    fun setMaxNumber(int: Int) {
        progressTextView?.text = int.toString()
    }

    fun setIcon(drawable: Drawable) {
        //todo write logic if layout doesn't contain view
        imageView?.setImageDrawable(drawable)
    }

    fun setTextSize(float: Float) {
        textSize = float
    }

    fun setProgressTextSize(float: Float) {
        progressTextSize = float
    }

    fun setTextColor(int: Int) {
        textColor = int
    }

    private fun setAttrs() {
        val attrs = Attributes()
        attrs.apply {
            icon = getIcon()
            progress = getProgress()
            maxNumber = getMuxNumber()
            textSize = getTextSize()
            progressTextSize = getProgressTextSize()
            units = getUnits()
            textColor = getTextColor()
            recycle()
        }
    }

    private fun setConfigs() {
        orientation = HORIZONTAL
        gravity = Gravity.BOTTOM
    }

    private fun setProgressTextView() {
        progressTextView = TextView(context)
        progressTextView?.apply {
            text = progress.toString()
            textSize = progressTextSize
            textColor?.let { setTextColor(it) }
        }
    }

    private fun setSplashTextView() {
        slashTextView = TextView(context)
        slashTextView?.textSize = textSize
        slashTextView?.text = "/"
        textColor?.let { slashTextView?.setTextColor(it) }
    }

    private fun setMaxNumberTextView() {
        maxNumberTextView = TextView(context)
        maxNumberTextView?.textSize = textSize
        maxNumberTextView?.text = maxNumber.toString()
        textColor?.let { maxNumberTextView?.setTextColor(it) }
    }

    private fun setImageView() {
        imageView = ImageView(context)
        imageView?.setImageDrawable(icon)
    }

    private fun setUnitsTextView() {
        unitsTextView = TextView(context)
        unitsTextView?.text = units
        unitsTextView?.textSize = textSize
        textColor?.let { unitsTextView?.setTextColor(it) }
    }

    private fun addViews() {
        imageView?.let { addView(it) }
        addView(progressTextView)
        addView(slashTextView)
        addView(maxNumberTextView)
        unitsTextView?.let { addView(it) }
    }

    private inner class Attributes() {
        private val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.CounterView, defStyleAttr, 0
        )

        fun getIcon(): Drawable? = typedArray.getDrawable(
            R.styleable.CounterView_icon
        )

        fun getProgress() =
            typedArray.getInt(R.styleable.CounterView_progress, DEFAULT_PROGRESS)

        fun getMuxNumber() =
            typedArray.getInt(R.styleable.CounterView_maxNumber, DEFAULT_MAX_NUMBER)

        fun getTextSize() =
            typedArray.getDimension(R.styleable.CounterView_textSize, DEFAULT_TEXT_SIZE)

        fun getProgressTextSize() =
            typedArray.getDimension(R.styleable.CounterView_progressTextSize, textSize)

        fun getUnits() =
            typedArray.getString(R.styleable.CounterView_units)

        fun getTextColor(): Int? {
            val textColor =
                typedArray.getColor(R.styleable.CounterView_textColor, COLOR_ABSENT_CODE)
            return if (textColor != COLOR_ABSENT_CODE) textColor else null
        }

        fun recycle() = typedArray.recycle()
    }

    companion object {
        const val DEFAULT_TEXT_SIZE: Float = 24.0f
        const val DEFAULT_PROGRESS: Int = 0
        const val DEFAULT_MAX_NUMBER: Int = 100
        const val COLOR_ABSENT_CODE = -999
    }

}