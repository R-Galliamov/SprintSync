package com.developers.sprintsync.global.manager

import android.content.Context
import android.util.TypedValue
import com.developers.sprintsync.R

class AppThemeManager(
    private val context: Context,
) {
    fun getPrimaryColor(): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimary,
            typedValue,
            true,
        )
        return typedValue.data
    }

    fun getPrimaryVariantColor(): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            R.attr.colorPrimaryVariant,
            typedValue,
            true,
        )
        return typedValue.data
    }

    fun getOnPrimaryColor(): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnPrimary,
            typedValue,
            true,
        )
        return typedValue.data
    }

    fun getOnPrimaryVariantColor(): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            R.attr.colorOnPrimaryVariant,
            typedValue,
            true,
        )
        return typedValue.data
    }

    fun getSecondaryColor(): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorSecondary,
            typedValue,
            true,
        )
        return typedValue.data
    }

    fun getSecondaryVariantColor(): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorSecondaryVariant,
            typedValue,
            true,
        )
        return typedValue.data
    }

    fun getThirdlyColor(): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            R.attr.colorThirdly,
            typedValue,
            true,
        )
        return typedValue.data
    }

    fun getFourthlyColor(): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            R.attr.colorFourthly,
            typedValue,
            true,
        )
        return typedValue.data
    }
}
