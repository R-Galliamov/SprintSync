package com.developers.sprintsync.global.util.spinner.manager

import android.content.Context
import android.widget.Spinner
import com.developers.sprintsync.global.util.spinner.adapter.SpinnerAdapter
import com.developers.sprintsync.global.util.spinner.mapper.ItemToSpinnerMapper
import com.developers.sprintsync.global.util.spinner.converter.SpinnerItemConverter

class SpinnerManager<T>(
    private val context: Context,
    itemToSpinnerMapper: ItemToSpinnerMapper<T>,
) {
    private val spinnerItemConverter = SpinnerItemConverter(itemToSpinnerMapper)
    private var _spinnerAdapter: SpinnerAdapter? = null
    private val spinnerAdapter get() = checkNotNull(_spinnerAdapter)

    fun initSpinner(
        spinner: Spinner,
        items: List<T>,
    ) {
        initSpinnerAdapter(items)
        spinner.adapter = spinnerAdapter
    }

    private fun initSpinnerAdapter(items: List<T>) {
        val spinnerItems = spinnerItemConverter.toSpinnerItems(items)
        _spinnerAdapter = SpinnerAdapter(context, spinnerItems)
    }

    fun setSelectedItem(
        spinner: Spinner,
        item: T,
    ) {
        val position = spinnerAdapter.getPosition(spinnerItemConverter.toSpinnerItem(item))
        spinner.setSelection(position)
    }

    fun getSelectedItem(spinner: Spinner): T {
        val selectedPosition = spinner.selectedItemPosition
        val spinnerItem = spinnerAdapter.getItem(selectedPosition) ?: throw IllegalStateException()
        return spinnerItemConverter.fromSpinnerItem(spinnerItem)
    }
}