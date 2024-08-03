package com.developers.sprintsync.global.manager

import android.content.Context
import android.util.TypedValue
import com.developers.sprintsync.R

class AppThemeManager(
    private val context: Context,
) {
    inner class Color {
        /*
    val primary: Int by lazy {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            R.attr.colorPrimary,
            typedValue,
            true,
        )
        typedValue.data
    }

    val primaryVariant: Int by lazy {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            R.attr.colorPrimaryVariant,
            typedValue,
            true,
        )
        typedValue.data
    }

    val onPrimary: Int by lazy {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            R.attr.colorOnPrimary,
            typedValue,
            true,
        )
        typedValue.data
    }
         */

        val onPrimaryVariant: Int by lazy {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(
                R.attr.colorOnPrimaryVariant,
                typedValue,
                true,
            )
            typedValue.data
        }

        val secondary: Int by lazy {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(
                R.attr.colorSecondary,
                typedValue,
                true,
            )
            typedValue.data
        }

        val secondaryVariant: Int by lazy {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(
                R.attr.colorSecondaryVariant,
                typedValue,
                true,
            )
            typedValue.data
        }

        val thirdly: Int by lazy {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(
                R.attr.colorThirdly,
                typedValue,
                true,
            )
            typedValue.data
        }

        val fourthly: Int by lazy {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(
                R.attr.colorFourthly,
                typedValue,
                true,
            )
            typedValue.data
        }
    }
}
