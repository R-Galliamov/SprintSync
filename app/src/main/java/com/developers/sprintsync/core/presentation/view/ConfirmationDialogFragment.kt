package com.developers.sprintsync.core.presentation.view

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.developers.sprintsync.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ConfirmationDialogFragment : DialogFragment() {
    interface DialogListener {
        fun onConfirmed()

        fun onCancelled()
    }

    private var dialogListener: DialogListener? = null

    fun setListener(dialogListener: DialogListener) {
        this.dialogListener = dialogListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext(), R.style.ConfirmationMaterialDialog)
            .setTitle(R.string.dialog_delete_track_title)
            .setMessage(R.string.dialog_delete_track_message)
            .setNegativeButton("No") { _, _ ->
                dialogListener?.onCancelled()
            }.setPositiveButton("Yes") { _, _ ->
                dialogListener?.onConfirmed()
            }.setCancelable(true)
            .create()
}
