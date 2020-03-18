package app

import model.LevelMap
import java.awt.image.BufferedImage
import java.io.File

abstract class Drawer(
    protected val mapWidth: Int,
    protected val mapHeight: Int,
    protected val level: Map.Entry<Pair<Int, Int>, LevelMap>,
    protected val out: BufferedImage
) {

    // pixels from border of bitmap to current level
    private val xOffset = level.key.first * levelConfig.levelWidth
    private val yOffset = level.key.second * (levelConfig.levelHeight)

    abstract fun drawLevel()

    protected fun getImage(name: String) = File("res/$name")

    protected fun draw(
        x: Int,
        y: Int,
        segment: BufferedImage,
        out: BufferedImage
    ) {
        val tx = transformX(x)
        val ty = transformY(y)
        out.graphics.drawImage(
            segment,
            tx,
            ty,
            TILE_SIZE,
            TILE_SIZE,
            null
        )
    }

    private fun transformY(verticalPosition: Int): Int {
        val y = mapHeight - ((verticalPosition + 1) + yOffset)
        return TILE_SIZE * (y)
    }

    private fun transformX(horizontalPosition: Int) = (xOffset + horizontalPosition) * TILE_SIZE

}