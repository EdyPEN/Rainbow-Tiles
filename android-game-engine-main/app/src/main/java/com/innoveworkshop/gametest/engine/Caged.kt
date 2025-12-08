package com.innoveworkshop.gametest.engine

interface Caged {
    /**
     * Checks if a GameObject has hit the left wall.
     *
     * @return True if it has hit the left wall, otherwise False.
     */
    fun hitLeftWall(): Boolean

    /**
     * Checks if a GameObject has hit the right wall.
     *
     * @return True if it has hit the right wall, otherwise False.
     */
    fun hitRightWall(): Boolean

    /**
     * Checks if a GameObject is on the floor.
     *
     * @return True if it's on the floor, otherwise False.
     */
    val isFloored: Boolean
}
