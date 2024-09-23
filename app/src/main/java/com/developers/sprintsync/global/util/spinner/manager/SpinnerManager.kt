package com.developers.sprintsync.global.util.spinner.manager

import android.widget.Spinner
import com.developers.sprintsync.global.util.spinner.adapter.SpinnerAdapter
import com.developers.sprintsync.global.util.spinner.converter.SpinnerItemConverter
import com.developers.sprintsync.global.util.spinner.mapper.ItemToSpinnerMapper

class SpinnerManager<T>(
    private val spinner: Spinner,
    private val items: List<T>,
    itemToSpinnerMapper: ItemToSpinnerMapper<T>,
) {
    private val spinnerItemConverter = SpinnerItemConverter(itemToSpinnerMapper)
    private var spinnerAdapter: SpinnerAdapter = createSpinnerAdapter().also { initAdapter(it) }

    private fun createSpinnerAdapter(): SpinnerAdapter {
        val spinnerItems = spinnerItemConverter.toSpinnerItems(items)
        return SpinnerAdapter(spinner.context, spinnerItems)
    }

    private fun initAdapter(adapter: SpinnerAdapter) {
        spinner.adapter = adapter
    }

    fun setSelectedItem(item: T) {
        val position = spinnerAdapter.getPosition(spinnerItemConverter.toSpinnerItem(item))
        spinner.setSelection(position)
    }

    fun getSelectedItem(): T {
        val selectedPosition = spinner.selectedItemPosition
        val spinnerItem = spinnerAdapter.getItem(selectedPosition) ?: throw IllegalStateException()
        return spinnerItemConverter.fromSpinnerItem(spinnerItem)
    }
}
