package com.innoveworkshop.gametest.engine

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas

class Sprite(var bmp: Bitmap, var x: Float, var y: Float) {
    constructor(
        resId: Int,
        x: Float,
        y: Float
    ) : this(BitmapFactory.decodeResource(Resources.getSystem(), resId), x, y)

    fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bmp, x, y, null)
    }
}
