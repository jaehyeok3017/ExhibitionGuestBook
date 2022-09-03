package com.example.exhibitionguestbook

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat


@Suppress("DEPRECATION")
class CanvasView(context : Context, attrs : AttributeSet?) : View(context, attrs) {
    init{
        setLayerType(FrameLayout.LAYER_TYPE_HARDWARE, null)
    }

    companion object {
        private var STROKE_WIDTH = 12f

        private var strokePaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            isDither = false

            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = STROKE_WIDTH
        }

        private val eraseCirclePaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = STROKE_WIDTH
        }

        private val erasePaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }

    private lateinit var extraCanvas : Canvas
    private lateinit var extraBitmap : Bitmap

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.white, null)
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private var path = Path()
    private var touchEventX = 0f
    private var touchEventY = 0f
    private var erasePositionX = 0f
    private var erasePositionY = 0f
    private var currentX = 0f
    private var currentY = 0f
    private var isMoving = false
    private var isErasing = false

    override fun onDraw(canvas: Canvas){
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        if(isErasing && isMoving){
            canvas.drawCircle(erasePositionX, erasePositionY, STROKE_WIDTH, eraseCirclePaint)
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(event == null) return false

        // S-pen
        if(event.getToolType(0) != MotionEvent.TOOL_TYPE_STYLUS) return false
        if(event.buttonState == MotionEvent.BUTTON_STYLUS_PRIMARY){
            if(!isMoving) {
                isErasing = !isErasing
            }
        }

        touchEventX = event.x
        touchEventY = event.y

        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                if(isErasing){
                    extraCanvas.drawCircle(touchEventX, touchEventY, STROKE_WIDTH, erasePaint)
                    buildDrawingCache()
                    erasePositionX = touchEventX
                    erasePositionY = touchEventY
                }
                else touchStart()
            }
            MotionEvent.ACTION_MOVE -> {
                if(isErasing){
                    path.addCircle(touchEventX, touchEventY, STROKE_WIDTH, Path.Direction.CW)
                    erasePositionX = touchEventX
                    erasePositionY = touchEventY
                }
                else touchMove()
            }
            MotionEvent.ACTION_UP -> {
                if(isErasing) extraCanvas.drawPath(path, erasePaint)
                else extraCanvas.drawPath(path, strokePaint)
                touchUp()
            }
        }

        return true
    }

    private fun touchStart() {
        path.reset()
        path.moveTo(touchEventX, touchEventY)

        currentX = touchEventX
        currentY = touchEventY
    }

    private fun touchMove() {
        val dx = Math.abs(touchEventX - currentX)
        val dy = Math.abs(touchEventY - currentY)

        if(dx >= touchTolerance || dy >= touchTolerance){
            path.quadTo(currentX, currentY,
                (touchEventX + currentX) / 2,
                (touchEventY + currentY) / 2)

            currentX = touchEventX
            currentY = touchEventY
            extraCanvas.drawPath(path, strokePaint)
        }

        invalidate()
    }

    private fun touchUp() {
        isMoving = false

        erasePositionX = 0f
        erasePositionY = 0f

        path.reset()
        invalidate()
    }

    fun drawColorSet(changeColor: String){
        isErasing = false

        var nextColor : Int = when(changeColor){
            "black" -> R.color.black
            "red" -> R.color.red
            "orange" -> R.color.orange
            "green" -> R.color.green
            "blue" -> R.color.blue
            "pink" -> R.color.pink
            "white" -> R.color.white
            else -> R.color.black
        }

        val resourceNextColor = ResourcesCompat.getColor(resources, nextColor, null)
        strokePaint.color = resourceNextColor
    }

    fun reset(width : Int, height: Int) {
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
        extraCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY)
        extraCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), erasePaint);
    }

    fun strokeWidthSet(changeStroke : String){
        when(changeStroke){
            "plus" -> strokePaint.strokeWidth += 1.0f
            "minus" -> strokePaint.strokeWidth -= 1.0f
        }
    }

    fun returnBitmap(backgroundWidth : Int, backgroundHeight: Int) : Bitmap {
        return extraBitmap
    }
}