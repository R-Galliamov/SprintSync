package com.developers.sprintsync.global.util.spinner.converter

import com.developers.sprintsync.global.util.spinner.data.SpinnerItem
import com.developers.sprintsync.global.util.spinner.mapper.ItemToSpinnerMapper

class SpinnerItemConverter<T>(
    itemToSpinnerMapper: ItemToSpinnerMapper<T>,
) {
    private val itemToSpinnerMap: Map<T, SpinnerItem> = itemToSpinnerMapper.itemToSpinnerMap

    fun toSpinnerItem(item: T): SpinnerItem =
        itemToSpinnerMap[item] ?: throw IllegalArgumentException(
            EXCEPTION_DESCRIPTION + "$item",
        )

    fun fromSpinnerItem(item: SpinnerItem): T =
        itemToSpinnerMap.entries.find { it.value == item }?.key
            ?: throw IllegalArgumentException(EXCEPTION_DESCRIPTION + "$item")

    fun toSpinnerItems(items: List<T>): List<SpinnerItem> = items.map { toSpinnerItem(it) }

    fun fromSpinnerItems(items: List<SpinnerItem>): List<T> = items.map { fromSpinnerItem(it) }

    companion object {
        private const val EXCEPTION_DESCRIPTION = "Unknown item: "
    }
}
