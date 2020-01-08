package ru.laink.painta

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.os.Build
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat.requestPermissions
import kotlin.math.abs

class PainView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private lateinit var bitmap: Bitmap // Область рисования
    private lateinit var bitmapCanvas: Canvas // Для рисования линий на Bitmap
    private var paintScreen: Paint = Paint() // Для вывода Bitmap на экран
    private var paintLine: Paint = Paint() // Линия отрисовки
    private val pathMap = HashMap<Int, Path>() // Данные контуров
    private val pathPointsMap = HashMap<Int, Point>() // Данные точек

    init {
        paintScreen = Paint()
        paintLine = Paint()
        paintLine.isAntiAlias = true
        paintLine.color = Color.BLACK
        paintLine.strokeCap = Paint.Cap.ROUND
        paintLine.style = Paint.Style.STROKE
        paintLine.strokeWidth = 5.toFloat()
    }

    companion object {
        const val TOUCH_TOLERANCE = 10.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmapCanvas = Canvas(bitmap)  // Создание объекта для рисования внутри Bitmap
        bitmap.eraseColor(Color.WHITE) // Очитска белым цветом
    }

    // Перерисовка
    override fun onDraw(canvas: Canvas?) {
        canvas?.drawBitmap(bitmap, 0f, 0f, paintScreen)

        // Отрисовка контуров линиями
        pathMap.forEach {
            canvas?.drawPath(it.value, paintLine)
        }
    }

    // Обработка событий касания
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.actionMasked  // Тип действия
        val actionIndex = event!!.actionIndex // Индекс действия

        // Начало касания, конец или перемещение?
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            touchStarted(
                event.getX(actionIndex),
                event.getY(actionIndex),
                event.getPointerId(actionIndex)
            )
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex))
        } else {
            touchMoved(event)
        }

        invalidate()

        return true
    }

    private fun touchStarted(floatX: Float, floatY: Float, lineId: Int) {
        var path: Path? = null
        var point: Point? = null

        if (pathMap.containsKey(lineId)) {
            path = pathMap[lineId]
            path?.reset() // Очистка Path после нового касания
            point = pathPointsMap[lineId]
        } else {
            path = Path()
            pathMap[lineId] = path
            point = Point()
            pathPointsMap[lineId] = point
        }

        // Перход к координатам касания
        path?.moveTo(floatX, floatY)
        point?.x = floatX.toInt()
        point?.y = floatY.toInt()
    }

    private fun touchEnded(lineId: Int) {
        val path = pathMap[lineId]
        bitmapCanvas.drawPath(path!!, paintLine) // Прорисовка линии
        path.reset()
    }

    private fun touchMoved(event: MotionEvent) {

        for (i in 0 until event.pointerCount) {
            val pointerId = event.getPointerId(i)
            val pointerIndex = event.findPointerIndex(pointerId)

            if (pathMap.containsKey(pointerId)) {
                val newX = event.getX(pointerIndex)
                val newY = event.getY(pointerIndex)

                val path = pathMap[pointerId]
                val point = pathPointsMap[pointerId]!!

                val deltaX = abs(newX - point.x)
                val deltaY = abs(newY - point.y)

                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {

                    path?.quadTo(
                        point.x.toFloat(),
                        point.y.toFloat(),
                        (newX + point.x) / 2,
                        (newY + point.y) / 2
                    )

                    point.x = newX.toInt()
                    point.y = newY.toInt()
                }
            }
        }
    }

    fun save() {

        lateinit var message: Toast

        val name = "PrintA_" + System.currentTimeMillis() + "_.jpg"

        val location = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            bitmap,
            name,
            "PaintA drawing image"
        )

        // Если путь найден
        message = if (location != null)
        // Вывод сообщени об успешном сохранении
            Toast.makeText(context, R.string.message_saved, Toast.LENGTH_SHORT)
        else
        // Вывод сообщени об ошибке
            Toast.makeText(context, R.string.message_error_saving, Toast.LENGTH_SHORT)

        message.setGravity(Gravity.CENTER, message.xOffset / 2, message.yOffset / 2)
        message.show()
    }

    fun clear() {
        pathMap.clear() //  Удалить все контуры
        pathPointsMap.clear() // Удалить все предыдущие точки
        bitmap.eraseColor(Color.WHITE) // Очитска изображения
        invalidate()// Перерисовать изображение
    }

    fun getLineWidth(): Int {
        return paintLine.strokeWidth.toInt()
    }

    fun setLineWidth(width: Float) {
        paintLine.strokeWidth = width
    }
}
