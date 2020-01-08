package ru.laink.painta

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.hardware.*
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_main.*
import ru.laink.painta.dialogs.EraseDialogFragment
import ru.laink.painta.dialogs.WidthDialogFragment

class MainActivityFragment : Fragment() {

    companion object {
        const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }

    lateinit var accelerationListener: AccelerationListener
    lateinit var painView: PainView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        painView = view.findViewById(R.id.paint_view)
        setHasOptionsMenu(true)

        accelerationListener = AccelerationListener(fragmentManager!!)
        accelerationListener.lastAcceleration = SensorManager.GRAVITY_EARTH
        accelerationListener.currentAcceleration = accelerationListener.lastAcceleration

        accelerationListener.sensorManager =
            context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        return view
    }

    override fun onResume() {
        super.onResume()
        // Прослушивание событий акселерометра
        accelerationListener.register()
    }

    override fun onPause() {
        super.onPause()
        // Прекращение прослушивания событий акселерометра
        accelerationListener.unregister()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.item_save ->
                saveWithPermission()
            R.id.item_delete -> {
                val eraseDialogFragment = EraseDialogFragment()
                eraseDialogFragment.show(fragmentManager!!, "Erase dialog")
            }
            R.id.item_line_width -> {
                val widthDialogFragment = WidthDialogFragment()
                widthDialogFragment.show(fragmentManager!!, "Width dialog")
            }
        }


        return super.onOptionsItemSelected(item)
    }

    private fun saveWithPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                val builder = AlertDialog.Builder(activity)

                builder.setMessage(R.string.permission_message)
                builder.setPositiveButton(R.string.ok) { _, _ ->
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                    )
                }
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )
            }
        } else {
            // Permission has already been granted
            painView.save()
        }
    }
}