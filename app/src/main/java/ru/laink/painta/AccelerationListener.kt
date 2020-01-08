package ru.laink.painta

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.fragment.app.FragmentManager
import ru.laink.painta.dialogs.EraseDialogFragment

class AccelerationListener(private val fragmentManager: FragmentManager) : SensorEventListener {
    var dialogOnScreen: Boolean = false
    var currentAcceleration = 0f
    var lastAcceleration = 0f
    var accelerationChange = 0f
    lateinit var sensorManager: SensorManager

    companion object {
        const val ACCELERATION_VARIABLE = 10000000
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(sensorEvent: SensorEvent?) {

        if (!dialogOnScreen) {
            val x = sensorEvent!!.values[0]
            val y = sensorEvent.values[1]
            val z = sensorEvent.values[2]

            currentAcceleration = x * x + y * y + z * z

            accelerationChange =
                currentAcceleration * currentAcceleration - lastAcceleration * lastAcceleration

            if (accelerationChange > ACCELERATION_VARIABLE) {
                //Очистка рисунок
                val eraseDialogFragment = EraseDialogFragment()
                eraseDialogFragment.show(fragmentManager, "Erase dialog")
            }
        }
    }

    fun register() {
        sensorManager.registerListener(
            this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun unregister() {
        sensorManager.unregisterListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        )
    }

}