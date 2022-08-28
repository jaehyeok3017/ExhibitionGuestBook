package com.example.exhibitionguestbook

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat

class CanvasView(context : Context, attrs : AttributeSet?) : View(context, attrs) {
    private lateinit var extraCanvas : Canvas
    private lateinit var extraBitmap : Bitmap

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.white, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.black, null)
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private var path = Path()
    private var touchEventX = 0f
    private var touchEventY = 0f
    private var currentX = 0f
    private var currentY = 0f

    private val paint = Paint().apply {
        color = drawColor
        isAntiAlias = false
        isDither = true

        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = STROKE_WIDTH
    }

    override fun onDraw(canvas: Canvas){
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchEventX = event.x
        touchEventY = event.y

        when(event.action){
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
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
            extraCanvas.drawPath(path, paint)
        }

        invalidate()
    }

    private fun touchUp() {
        path.reset()
    }

    companion object {
        private const val STROKE_WIDTH = 12f
    }
}