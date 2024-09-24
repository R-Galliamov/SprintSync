package com.developers.sprintsync.global.manager.view.input

import android.view.View
import android.widget.EditText
import com.developers.sprintsync.global.util.KeyboardUtils
import com.developers.sprintsync.statistics.model.ui.InputCardView
import javax.inject.Inject

class InputCardManager
    @Inject
    constructor(
        private val keyboardUtils: KeyboardUtils,
    ) {
        fun configureInputCards(
            containerView: View,
            inputCardViews: List<InputCardView>,
        ) {
            val editTextFields = inputCardViews.map { it.editText }
            clearFocusOnBackgroundClick(containerView, editTextFields)
            inputCardViews.forEach { inputView ->
                setupCardClickListener(inputView.card)
                setupFocusChangeListener(inputView.editText)
            }
        }

        private fun clearFocusOnBackgroundClick(
            root: View,
            editTextView: List<EditText>,
        ) = root.setOnClickListener { editTextView.forEach { it.clearFocus() } }

        private fun setupFocusChangeListener(editText: EditText) =
            editText.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    keyboardUtils.showKeyboard(view)
                } else {
                    keyboardUtils.hideKeyboard(view)
                }
            }

        private fun setupCardClickListener(card: View) =
            card.setOnClickListener { view ->
                view.requestFocus()
            }
    }