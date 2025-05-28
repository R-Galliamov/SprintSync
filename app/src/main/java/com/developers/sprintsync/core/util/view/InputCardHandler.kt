package com.developers.sprintsync.core.util.view

import android.view.View
import android.widget.EditText
import com.developers.sprintsync.core.util.KeyboardUtils
import javax.inject.Inject

class InputCardHandler
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