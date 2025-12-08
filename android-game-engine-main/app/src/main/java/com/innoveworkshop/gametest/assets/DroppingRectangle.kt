package com.innoveworkshop.gametest.assets

import android.util.Log
import com.innoveworkshop.gametest.engine.Rectangle
import com.innoveworkshop.gametest.engine.Vector


class DroppingRectangle(
    position: Vector?,
    mass: Float,
    width: Float,
    height: Float,
    gravityAcceleration: Float,
    color: Int
) : Rectangle(position, width, height, color) {
    var gravityAcceleration: Float = 0f
    var mass: Float = 0f
    var velocity: Vector = Vector(0f, 0f)
    var acceleration: Vector = Vector(0f, 0f)
    var justLanded: Boolean = false

    init {
        this.gravityAcceleration = gravityAcceleration
        this.mass = mass
    }

    override fun onFixedUpdate() {
        super.onFixedUpdate()

        acceleration.y = if (isFloored) 0f else gravityAcceleration

        velocity.x += acceleration.x
        velocity.y += acceleration.y

        if (isFloored && velocity.y > 0f) {
            velocity.y = 0f
        }

        position.x += velocity.x
        position.y += velocity.y

        acceleration.x = 0f
        acceleration.y = 0f
    }

    fun ApplyForce(force: Vector)
    {
        acceleration.x += force.x / mass
        acceleration.y += force.y / mass
    }
}
