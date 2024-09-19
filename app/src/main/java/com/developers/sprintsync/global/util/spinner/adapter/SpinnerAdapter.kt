package com.developers.sprintsync.global.util.spinner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.developers.sprintsync.databinding.ItemSpinnerBinding
import com.developers.sprintsync.global.util.spinner.data.SpinnerItem

class SpinnerAdapter(
    context: Context,
    private val items: List<SpinnerItem>,
) : ArrayAdapter<SpinnerItem>(context, DEFAULT_RESOURCE, items) {
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View = createItemView(position, parent)

    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View = createItemView(position, parent)

    private fun createItemView(
        position: Int,
        parent: ViewGroup,
    ): View {
        val binding = ItemSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val item = items[position]
        binding.tvTitle.text = item.displayText
        return binding.tvTitle
    }

    companion object {
        private const val DEFAULT_RESOURCE = 0
    }
}
