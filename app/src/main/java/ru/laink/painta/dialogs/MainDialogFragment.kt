package ru.laink.painta.dialogs

import android.content.Context
import androidx.fragment.app.DialogFragment
import ru.laink.painta.MainActivityFragment
import ru.laink.painta.R

open class MainDialogFragment : DialogFragment() {

    protected fun getPainView(): MainActivityFragment? {
        return fragmentManager?.findFragmentById(R.id.paint_fragment) as MainActivityFragment
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        getPainView()?.accelerationListener?.dialogOnScreen = true
    }

    override fun onDetach() {
        super.onDetach()

        getPainView()?.accelerationListener?.dialogOnScreen = false
    }
}