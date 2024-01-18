package com.developers.sprintsync.util.manager

import android.content.Context
import android.util.TypedValue
import com.developers.sprintsync.R


class AppThemeManager(private val context: Context) {

    fun getPrimaryColor(): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimary, typedValue, true
        )
        return typedValue.data
    }

    fun getSecondaryColor(): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorSecondary, typedValue, true
        )
        return typedValue.data
    }

    fun getThirdlyColor(): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            R.attr.colorThirdly, typedValue, true
        )
        return typedValue.data
    }

    fun getFourthlyColor(): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            R.attr.colorFourthly, typedValue, true
        )
        return typedValue.data
    }
}