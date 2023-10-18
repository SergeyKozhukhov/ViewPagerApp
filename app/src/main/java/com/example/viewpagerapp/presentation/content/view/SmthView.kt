package com.example.viewpagerapp.presentation.content.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class SmthView(context: Context) : View(context) {

    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.GREEN
    }

    fun setColor(color: Int) {
        paint.color = color
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(300f, 300f, 100f, paint)
    }
}