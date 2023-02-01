package com.stepa_0751.neco_gps_tracker.utils

import android.app.AlertDialog
import android.content.Context
import com.stepa_0751.neco_gps_tracker.R

object DialogManager {
    fun showLocEnableDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(R.string.location_dialog_message)
        dialog.setMessage(context.getString(R.string.location_dialog_message))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") { _, _ ->
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No") { _, _ ->
        }
        dialog.show()
    }


}
