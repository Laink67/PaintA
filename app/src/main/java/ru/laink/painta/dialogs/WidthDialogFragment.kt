package ru.laink.painta.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.line_width.*
import ru.laink.painta.PainView
import ru.laink.painta.R

class WidthDialogFragment : MainDialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val paintView = getPainView()!!.painView
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.width_dialog_message)
        val view = activity?.layoutInflater?.inflate(R.layout.line_width, null)
        builder.setView(view)

        val seekBar = view?.findViewById<SeekBar>(R.id.seekbar_width)
        seekBar!!.progress = paintView.getLineWidth()

        builder.setPositiveButton(R.string.ok) { _, _ ->
            paintView.setLineWidth(seekBar.progress.toFloat())
        }

        builder.setNegativeButton(R.string.cancel, null)

        return builder.create()
    }
}