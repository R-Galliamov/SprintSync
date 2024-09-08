package com.developers.sprintsync.global.util

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidUtils
    @Inject
    constructor(
        val context: Context,
    ) {
        fun hideKeyboard(view: View) {
            val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun showKeyboard(view: View) {
            val inputMethodManager =
                context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, SHOW_IMPLICIT)
        }
    }
