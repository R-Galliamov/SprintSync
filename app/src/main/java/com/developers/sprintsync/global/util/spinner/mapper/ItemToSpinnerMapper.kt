package com.developers.sprintsync.global.util.spinner.mapper

import com.developers.sprintsync.global.util.spinner.data.SpinnerItem

abstract class ItemToSpinnerMapper<T> {
    abstract val itemToSpinnerMap: Map<T, SpinnerItem>
}
