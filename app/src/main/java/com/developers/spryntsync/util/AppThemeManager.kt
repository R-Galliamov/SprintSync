package com.developers.spryntsync.util

import android.content.Context
import android.util.TypedValue


class AppThemeManager(private val context: Context) {

    fun getSecondaryColor(): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorSecondary, typedValue, true
        )
        return typedValue.data
    }

    fun getPrimaryColor(): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimary, typedValue, true
        )
        return typedValue.data
    }
}