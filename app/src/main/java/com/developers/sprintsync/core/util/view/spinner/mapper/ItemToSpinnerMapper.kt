package com.developers.sprintsync.core.util.view.spinner.mapper

import com.developers.sprintsync.core.util.view.spinner.data.SpinnerItem

abstract class ItemToSpinnerMapper<T> {
    abstract val itemToSpinnerMap: Map<T, SpinnerItem>
}
