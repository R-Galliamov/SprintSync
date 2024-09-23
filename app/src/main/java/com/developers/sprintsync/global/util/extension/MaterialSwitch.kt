package com.developers.sprintsync.global.util.extension

import com.google.android.material.materialswitch.MaterialSwitch

fun MaterialSwitch.setState(
    isChecked: Boolean,
    animate: Boolean = true,
) {
    if (this.isChecked == isChecked) return
    this.isChecked = isChecked
    if (!animate) {
        this.jumpDrawablesToCurrentState()
    }
}
