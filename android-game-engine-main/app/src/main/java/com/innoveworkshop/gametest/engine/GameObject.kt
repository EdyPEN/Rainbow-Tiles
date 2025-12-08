package com.innoveworkshop.gametest.engine

import android.graphics.Canvas

abstract class GameObject {
    var position: Vector

    protected var gameSurface: GameSurface? = null
    var isDestroyed: Boolean = false
        protected set

    constructor(position: Vector) {
        this.position = position
    }

    @JvmOverloads
    constructor(x: Float = 0f, y: Float = 0f) {
        position = Vector(x, y)
    }

    fun setPosition(x: Float, y: Float) {
        position.x = x
        position.y = y
    }

    fun destroy() {
        gameSurface!!.removeGameObject(this)
        isDestroyed = true
    }

    open fun onStart(surface: GameSurface?) {
        this.gameSurface = surface
    }

    open fun onFixedUpdate() {
        if (isDestroyed) {
            setPosition(-100f, -100f)
        }
    }

    open fun onDraw(canvas: Canvas?) {}
}
