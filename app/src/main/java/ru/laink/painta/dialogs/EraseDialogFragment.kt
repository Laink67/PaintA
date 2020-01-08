package ru.laink.painta.dialogs

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import ru.laink.painta.MainActivityFragment
import ru.laink.painta.R

class EraseDialogFragment : MainDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        builder.setTitle(R.string.erase)

        builder.setPositiveButton(R.string.ok) { _, _ ->
            getPainView()?.painView?.clear()
        }
        builder.setNegativeButton(R.string.cancel, null)

        return builder.create()
    }
}