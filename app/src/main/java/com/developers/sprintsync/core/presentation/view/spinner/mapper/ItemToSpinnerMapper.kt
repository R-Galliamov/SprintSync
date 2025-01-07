package com.developers.sprintsync.core.presentation.view.spinner.mapper

import com.developers.sprintsync.core.presentation.view.spinner.data.SpinnerItem

abstract class ItemToSpinnerMapper<T> {
    abstract val itemToSpinnerMap: Map<T, SpinnerItem>
}
