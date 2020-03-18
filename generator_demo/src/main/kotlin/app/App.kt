package app

import model.*
import java.awt.image.BufferedImage
import java.io.*
import javax.imageio.ImageIO
import generator.Config
import generator.SegmentType

internal const val OUT = "out.png"
internal const val FORMAT = "PNG"
internal const val TILE_SIZE = 16

internal val levelConfig = Config(
    levelWidth = 12,
    levelHeight = 50
)

fun main(args: Array<String>) {
    val worldMap = WorldMap()
    for (x in 0..1) {
        for (y in 0..1) {
            worldMap.addLevel(x, y, levelConfig)
        }
    }
    saveMap(worldMap)
}

fun saveMap(worldMap: WorldMap) {

    // level size
    val w = worldMap.getWidth()
    val h = worldMap.getHeight()

    // buffered images
    val out = BufferedImage(
        w * levelConfig.levelWidth * TILE_SIZE,
        h * levelConfig.levelHeight * TILE_SIZE, BufferedImage.TYPE_INT_ARGB
    )

    for (level in worldMap.getLevels().entries) {
        KenneyDrawer(
            w * levelConfig.levelWidth,
            h * levelConfig.levelHeight,
            level,
            out
        ).drawLevel()
    }

    ImageIO.write(out, FORMAT, File(OUT))

    println()
}