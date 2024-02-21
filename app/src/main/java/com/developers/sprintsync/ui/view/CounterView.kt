package com.developers.sprintsync.ui.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.developers.sprintsync.R

class CounterView @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var progress: Int = DEFAULT_PROGRESS
    private var maxNumber: Int = DEFAULT_MAX_NUMBER
    private var units: String? = null

    private var progressTextSize: Float = DEFAULT_TEXT_SIZE_FLOAT
    private var textSize: Float = DEFAULT_TEXT_SIZE_FLOAT

    private var progressTextView: TextView? = null
    private var slashTextView: TextView? = null
    private var maxNumberTextView: TextView? = null
    private var unitsTextView: TextView? = null

    private var textColor: Int? = null
    private var typeface: Typeface = Typeface.DEFAULT

    init {
        attrs?.let { setAttrs() }
        setConfigs()
        setProgressTextView()
        setSplashTextView()
        setMaxNumberTextView()
        if (!units.isNullOrBlank()) {
            setUnitsTextView()
        }
        addViews()
    }

    // TODO fix setters
    fun setProgress(int: Int) {
        progressTextView?.text = int.toString()
    }

    fun setMaxNumber(int: Int) {
        progressTextView?.text = int.toString()
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
            progress = getProgress()
            maxNumber = getMuxNumber()
            textSize = getTextSize()
            progressTextSize = getProgressTextSize()
            units = getUnits()
            typeface = Typeface.defaultFromStyle(getTextStyle())
            textColor = getTextColor()
            recycle()
        }
    }

    private fun setConfigs() {
        orientation = HORIZONTAL
        gravity = Gravity.BOTTOM
    }

    private fun setProgressTextView() {
        progressTextView = TextView(context).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, progressTextSize)
            typeface = typeface?.let { Typeface.create(it, this@CounterView.typeface.style) }
            text = progress.toString()
            this@CounterView.textColor?.let { setTextColor(it) }
        }
    }

    private fun setSplashTextView() {
        slashTextView = TextView(context).apply {
            textSize = this@CounterView.textSize
            text = "/"
            typeface = typeface?.let { Typeface.create(it, this@CounterView.typeface.style) }
            this@CounterView.textColor?.let { setTextColor(it) }
        }
    }

    private fun setMaxNumberTextView() {
        maxNumberTextView = TextView(context).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, this@CounterView.textSize)
            typeface = typeface?.let { Typeface.create(it, this@CounterView.typeface.style) }
            text = maxNumber.toString()
            this@CounterView.textColor?.let { setTextColor(it) }
        }
    }

    private fun setUnitsTextView() {
        unitsTextView = TextView(context)
        unitsTextView?.text = units
        unitsTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
        textColor?.let { unitsTextView?.setTextColor(it) }
    }

    private fun addViews() {
        addView(progressTextView)
        addView(slashTextView)
        addView(maxNumberTextView)
        unitsTextView?.let { addView(it) }
    }

    private inner class Attributes {
        private val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.CounterView, defStyleAttr, 0
        )

        fun getProgress() = typedArray.getInt(R.styleable.CounterView_progress, DEFAULT_PROGRESS)

        fun getMuxNumber() =
            typedArray.getInt(R.styleable.CounterView_maxNumber, DEFAULT_MAX_NUMBER)

        fun getTextSize() =
            typedArray.getInteger(
                R.styleable.CounterView_textSize,
                DEFAULT_TEXT_SIZE_INT
            ).toFloat()

        fun getProgressTextSize() =
            typedArray.getInteger(
                R.styleable.CounterView_progressTextSize,
                DEFAULT_TEXT_SIZE_INT
            ).toFloat().also {
                Log.d("COUNTER VIEW", it.toString())
            }

        fun getUnits() = typedArray.getString(R.styleable.CounterView_units)

        fun getTextColor(): Int? {
            val textColor =
                typedArray.getColor(R.styleable.CounterView_textColor, COLOR_ABSENT_CODE)
            return if (textColor != COLOR_ABSENT_CODE) textColor else null
        }

        fun getTextStyle(): Int =
            typedArray.getInt(R.styleable.CounterView_android_textStyle, Typeface.DEFAULT.style)
        fun recycle() = typedArray.recycle()
    }

    private companion object {
        private const val DEFAULT_TEXT_SIZE_INT: Int = 20
        private const val DEFAULT_TEXT_SIZE_FLOAT: Float = DEFAULT_TEXT_SIZE_INT.toFloat()
        private const val DEFAULT_PROGRESS: Int = 0
        private const val DEFAULT_MAX_NUMBER: Int = 100
        private const val COLOR_ABSENT_CODE = -999
    }
}
