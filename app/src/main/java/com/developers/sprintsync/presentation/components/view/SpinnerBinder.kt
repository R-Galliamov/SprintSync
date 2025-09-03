package com.developers.sprintsync.presentation.components.view

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.developers.sprintsync.R

class SpinnerBinder<T>(
    private val label: (T) -> String,
    private val areSame: (T, T) -> Boolean = { a, b -> a == b }
) {
    private var spinner: Spinner? = null
    private var items: List<T> = emptyList()
    private var adapter: ArrayAdapter<String>? = null

    fun attach(spinner: Spinner, items: List<T>) {
        this.spinner = spinner
        this.items = items
        val layout = R.layout.item_spinner
        val a = ArrayAdapter(
            spinner.context,
            layout,
            items.map(label)
        ).also { it.setDropDownViewResource(layout) }
        spinner.adapter = a
        adapter = a
    }

    fun detach() {
        spinner?.adapter = null
        spinner = null
        adapter = null
        items = emptyList()
    }

    fun update(items: List<T>) {
        val s = spinner ?: run { this.items = items; return }
        attach(s, items)
    }

    fun setSelection(item: T) {
        val pos = items.indexOfFirst { areSame(it, item) }
        if (pos >= 0) spinner?.setSelection(pos)
    }

    fun getSelectionOrNull(): T? =
        spinner?.selectedItemPosition?.let { pos -> items.getOrNull(pos) }

    fun setOnSelected(listener: (T) -> Unit) {
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                items.getOrNull(pos)?.let(listener)
            }

            override fun onNothingSelected(p: AdapterView<*>) {}
        }
    }
}
