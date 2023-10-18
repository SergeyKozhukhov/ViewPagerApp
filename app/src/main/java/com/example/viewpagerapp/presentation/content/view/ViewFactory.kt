package com.example.viewpagerapp.presentation.content.view

import android.content.Context
import android.graphics.Color
import java.util.Random

class ViewFactory {

    private val rnd = Random()

    fun createSmth(context: Context) = SmthView(context).apply {
        setColor(color = getColor())
    }

    private fun getColor() = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
}