package com.developers.sprintsync.global.manager.view.spinner.mapper

import com.developers.sprintsync.global.manager.view.spinner.data.SpinnerItem

abstract class ItemToSpinnerMapper<T> {
    abstract val itemToSpinnerMap: Map<T, SpinnerItem>
}
