package com.developers.sprintsync.core.util.extension

import android.view.View

fun View.setVisibility(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}
