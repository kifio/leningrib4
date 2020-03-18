package app

import generator.SegmentType
import model.LevelMap
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


class KenneyDrawer(
    mapWidth: Int,
    mapHeight: Int,
    level: Map.Entry<Pair<Int, Int>, LevelMap>,
    out: BufferedImage
) : Drawer(
    mapWidth,
    mapHeight,
    level,
    out
) {

    companion object {
        private const val TREES_MAP = "trees_map.png"
    }

    private val affineTransform = AffineTransform()

    init {
        affineTransform.concatenate(AffineTransform.getScaleInstance(-1.0, 1.0))
        affineTransform.concatenate(AffineTransform.getTranslateInstance(-16.0, 0.0))
    }

    private val treesMap = ImageIO.read(getImage(TREES_MAP))

    // Bottom border corners
    val BOTTOM_LEFT_CORNER_COMMON_TOP: BufferedImage = treesMap.getSubimage(0, 32, 16, 16)
    val BOTTOM_LEFT_CORNER_COMMON_BOTTOM: BufferedImage = treesMap.getSubimage(0, 48, 16, 16)

    val BOTTOM_LEFT_CORNER_OUT_BORDER_TOP: BufferedImage = treesMap.getSubimage(48, 32, 16, 16)
    val BOTTOM_LEFT_CORNER_OUT_BORDER_BOTTOM: BufferedImage = treesMap.getSubimage(48, 48, 16, 16)

    val BOTTOM_RIGHT_CORNER_COMMON_BOTTOM: BufferedImage = flip(BOTTOM_LEFT_CORNER_COMMON_BOTTOM)
    val BOTTOM_RIGHT_CORNER_COMMON_TOP: BufferedImage = flip(BOTTOM_LEFT_CORNER_COMMON_TOP)

    val BOTTOM_RIGHT_CORNER_OUT_BORDER_BOTTOM: BufferedImage = flip(BOTTOM_LEFT_CORNER_OUT_BORDER_BOTTOM)
    val BOTTOM_RIGHT_CORNER_OUT_BORDER_TOP: BufferedImage = flip(BOTTOM_LEFT_CORNER_OUT_BORDER_TOP)

    // Top border corners
    val TOP_LEFT_CORNER_COMMON_BOTTOM: BufferedImage = treesMap.getSubimage(0, 16, 16, 16)
    val TOP_LEFT_CORNER_OUT_BORDER_TOP: BufferedImage = treesMap.getSubimage(48, 48, 16, 16)
    val TOP_LEFT_CORNER_COMMON_TOP: BufferedImage = treesMap.getSubimage(0, 0, 16, 16)

    val TOP_RIGHT_CORNER_COMMON_BOTTOM: BufferedImage = flip(TOP_LEFT_CORNER_COMMON_BOTTOM)
    val TOP_RIGHT_CORNER_OUT_BORDER_TOP: BufferedImage = flip(TOP_LEFT_CORNER_OUT_BORDER_TOP)
    val TOP_RIGHT_CORNER_COMMON_TOP: BufferedImage = flip(TOP_LEFT_CORNER_COMMON_TOP)

    // Bottom common segments
    val BOTTOM_COMMON_LEFT_TOP: BufferedImage = treesMap.getSubimage(16, 32, 16, 16)
    val BOTTOM_COMMON_LEFT_BOTTOM: BufferedImage = treesMap.getSubimage(16, 48, 16, 16)

    val BOTTOM_COMMON_RIGHT_BOTTOM: BufferedImage = BOTTOM_COMMON_LEFT_BOTTOM
    val BOTTOM_COMMON_RIGHT_TOP: BufferedImage = BOTTOM_COMMON_LEFT_TOP

    // Top common segments
    val TOP_COMMON_LEFT_TOP: BufferedImage = treesMap.getSubimage(16, 0, 16, 16)
    val TOP_COMMON_LEFT_BOTTOM: BufferedImage = treesMap.getSubimage(16, 16, 16, 16)

    val TOP_COMMON_RIGHT_TOP: BufferedImage = TOP_COMMON_LEFT_TOP
    val TOP_COMMON_RIGHT_BOTTOM: BufferedImage = TOP_COMMON_LEFT_BOTTOM

    // Left segments
    val LEFT_BEGIN_BOTTOM: BufferedImage = treesMap.getSubimage(80, 16, 16, 16)
    val LEFT_BEGIN_TOP: BufferedImage = treesMap.getSubimage(80, 0, 16, 16)
    val LEFT_COMMON_BOTTOM: BufferedImage = treesMap.getSubimage(96, 16, 16, 16)
    val LEFT_COMMON_TOP: BufferedImage = treesMap.getSubimage(96, 0, 16, 16)
    val LEFT_END_TOP: BufferedImage = treesMap.getSubimage(64, 0, 16, 16)
    val LEFT_END_BOTTOM: BufferedImage = treesMap.getSubimage(64, 16, 16, 16)

    // Right Segments
    val RIGHT_BEGIN_BOTTOM: BufferedImage = flip(LEFT_BEGIN_BOTTOM)
    val RIGHT_BEGIN_TOP: BufferedImage = flip(LEFT_BEGIN_TOP)
    val RIGHT_COMMON_BOTTOM: BufferedImage = flip(LEFT_COMMON_BOTTOM)
    val RIGHT_COMMON_TOP: BufferedImage = flip(LEFT_COMMON_TOP)
    val RIGHT_END_TOP: BufferedImage = flip(LEFT_END_TOP)
    val RIGHT_END_BOTTOM: BufferedImage = flip(LEFT_END_BOTTOM)

    // Bottom room outs
    val BOTTOM_ROOM_OUT_LEFT_BORDER_TOP: BufferedImage = treesMap.getSubimage(32, 32, 16, 16)
    val BOTTOM_ROOM_OUT_LEFT_BORDER_BOTTOM: BufferedImage = treesMap.getSubimage(32, 48, 16, 16)
    val BOTTOM_ROOM_OUT_RIGHT_BORDER_TOP: BufferedImage = flip(BOTTOM_ROOM_OUT_LEFT_BORDER_TOP)
    val BOTTOM_ROOM_OUT_RIGHT_BORDER_BOTTOM: BufferedImage = flip(BOTTOM_ROOM_OUT_LEFT_BORDER_BOTTOM)

    // Top room outs
    val TOP_ROOM_OUT_LEFT_BORDER_TOP: BufferedImage = treesMap.getSubimage(32, 0, 16, 16)
    val TOP_ROOM_OUT_LEFT_BORDER_BOTTOM: BufferedImage = treesMap.getSubimage(32, 16, 16, 16)
    val TOP_ROOM_OUT_RIGHT_BORDER_BOTTOM: BufferedImage = flip(TOP_ROOM_OUT_LEFT_BORDER_BOTTOM)
    val TOP_ROOM_OUT_RIGHT_BORDER_TOP: BufferedImage = flip(TOP_ROOM_OUT_LEFT_BORDER_TOP)


    val ROOM_WALL_START_TOP: BufferedImage = treesMap.getSubimage(64, 32, 16, 16)
    val ROOM_WALL_START_BOTTOM: BufferedImage = treesMap.getSubimage(64, 48, 16, 16)

    val ROOM_WALL_COMMON_TOP: BufferedImage = treesMap.getSubimage(80, 32, 16, 16)
    val ROOM_WALL_COMMON_BOTTOM: BufferedImage = treesMap.getSubimage(80, 48, 16, 16)

    val ROOM_WALL_END_BOTTOM: BufferedImage = flip(ROOM_WALL_START_BOTTOM)
    val ROOM_WALL_END_TOP: BufferedImage = flip(ROOM_WALL_START_TOP)

    override fun drawLevel() {
//        val grass = forest.getSubimage(
//            0,
//            0,
//            2 * TILE_SIZE,
//            2 * TILE_SIZE
//        )
//
//        for (x in 0..levelConfig.levelWidth) {
//            for (y in 0..levelConfig.levelHeight) {
//                draw(x, y, grass, out)
//            }
//        }

                for (s in level.value.getSegments()) {
            when (s.getValue()) {

                SegmentType.RIGHT_COMMON_TOP -> draw(s.x, s.y, RIGHT_COMMON_TOP, out)
                SegmentType.RIGHT_COMMON_BOTTOM -> draw(s.x, s.y, RIGHT_COMMON_BOTTOM, out)

                SegmentType.RIGHT_BEGIN_TOP -> draw(s.x, s.y, RIGHT_BEGIN_TOP, out)
                SegmentType.RIGHT_BEGIN_BOTTOM -> draw(s.x, s.y, RIGHT_BEGIN_BOTTOM, out)

                SegmentType.RIGHT_END_TOP -> draw(s.x, s.y, RIGHT_END_TOP, out)
                SegmentType.RIGHT_END_BOTTOM -> draw(s.x, s.y, RIGHT_END_BOTTOM, out)

                SegmentType.TOP_COMMON_LEFT_TOP -> draw(s.x, s.y, TOP_COMMON_LEFT_TOP, out)
                SegmentType.TOP_COMMON_RIGHT_TOP -> draw(s.x, s.y, TOP_COMMON_RIGHT_TOP, out)

                SegmentType.TOP_COMMON_LEFT_BOTTOM -> draw(s.x, s.y, TOP_COMMON_LEFT_BOTTOM, out)
                SegmentType.TOP_COMMON_RIGHT_BOTTOM -> draw(s.x, s.y, TOP_COMMON_RIGHT_BOTTOM, out)

                SegmentType.TOP_ROOM_OUT_RIGHT_BORDER_TOP -> draw(s.x, s.y, TOP_ROOM_OUT_RIGHT_BORDER_TOP, out)
                SegmentType.TOP_ROOM_OUT_LEFT_BORDER_TOP -> draw(s.x, s.y, TOP_ROOM_OUT_LEFT_BORDER_TOP, out)

                SegmentType.TOP_ROOM_OUT_RIGHT_BORDER_BOTTOM -> draw(s.x, s.y, TOP_ROOM_OUT_RIGHT_BORDER_BOTTOM, out)
                SegmentType.TOP_ROOM_OUT_LEFT_BORDER_BOTTOM -> draw(s.x, s.y, TOP_ROOM_OUT_LEFT_BORDER_BOTTOM, out)

                SegmentType.LEFT_COMMON_TOP -> draw(s.x, s.y, LEFT_COMMON_TOP, out)
                SegmentType.LEFT_COMMON_BOTTOM -> draw(s.x, s.y, LEFT_COMMON_BOTTOM, out)

                SegmentType.LEFT_BEGIN_TOP -> draw(s.x, s.y, LEFT_BEGIN_TOP, out)
                SegmentType.LEFT_BEGIN_BOTTOM -> draw(s.x, s.y, LEFT_BEGIN_BOTTOM, out)

                SegmentType.LEFT_END_TOP -> draw(s.x, s.y, LEFT_END_TOP, out)
                SegmentType.LEFT_END_BOTTOM -> draw(s.x, s.y, LEFT_END_BOTTOM, out)

                SegmentType.BOTTOM_COMMON_LEFT_BOTTOM -> draw(s.x, s.y, BOTTOM_COMMON_LEFT_BOTTOM, out)
                SegmentType.BOTTOM_COMMON_LEFT_TOP -> draw(s.x, s.y, BOTTOM_COMMON_LEFT_TOP, out)
                SegmentType.BOTTOM_COMMON_RIGHT_BOTTOM -> draw(s.x, s.y, BOTTOM_COMMON_RIGHT_BOTTOM, out)
                SegmentType.BOTTOM_COMMON_RIGHT_TOP -> draw(s.x, s.y, BOTTOM_COMMON_RIGHT_TOP, out)

                SegmentType.BOTTOM_LEFT_CORNER_COMMON_BOTTOM -> draw(s.x, s.y, BOTTOM_LEFT_CORNER_COMMON_BOTTOM, out)
                SegmentType.BOTTOM_LEFT_CORNER_COMMON_TOP -> draw(s.x, s.y, BOTTOM_LEFT_CORNER_COMMON_TOP, out)

                SegmentType.BOTTOM_LEFT_CORNER_OUT_BORDER_BOTTOM -> draw(s.x, s.y, BOTTOM_LEFT_CORNER_OUT_BORDER_BOTTOM, out)
                SegmentType.BOTTOM_LEFT_CORNER_OUT_BORDER_TOP -> draw(s.x, s.y, BOTTOM_LEFT_CORNER_OUT_BORDER_TOP, out)

                SegmentType.BOTTOM_RIGHT_CORNER_COMMON_BOTTOM -> draw(s.x, s.y, BOTTOM_RIGHT_CORNER_COMMON_BOTTOM, out)
                SegmentType.BOTTOM_RIGHT_CORNER_COMMON_TOP -> draw(s.x, s.y, BOTTOM_RIGHT_CORNER_COMMON_TOP, out)

                SegmentType.BOTTOM_RIGHT_CORNER_OUT_BORDER_BOTTOM -> draw(s.x, s.y, BOTTOM_RIGHT_CORNER_OUT_BORDER_BOTTOM, out)
                SegmentType.BOTTOM_RIGHT_CORNER_OUT_BORDER_TOP -> draw(s.x, s.y, BOTTOM_RIGHT_CORNER_OUT_BORDER_TOP, out)

                SegmentType.TOP_LEFT_CORNER_OUT_BORDER_TOP -> draw(s.x, s.y, TOP_LEFT_CORNER_OUT_BORDER_TOP, out)
                SegmentType.TOP_LEFT_CORNER_COMMON_TOP -> draw(s.x, s.y, TOP_LEFT_CORNER_COMMON_TOP, out)
                SegmentType.TOP_LEFT_CORNER_COMMON_BOTTOM -> draw(s.x, s.y, TOP_LEFT_CORNER_COMMON_BOTTOM, out)

                SegmentType.TOP_RIGHT_CORNER_OUT_BORDER_TOP -> draw(s.x, s.y, TOP_RIGHT_CORNER_OUT_BORDER_TOP, out)
                SegmentType.TOP_RIGHT_CORNER_COMMON_TOP -> draw(s.x, s.y, TOP_RIGHT_CORNER_COMMON_TOP, out)
                SegmentType.TOP_RIGHT_CORNER_COMMON_BOTTOM -> draw(s.x, s.y, TOP_RIGHT_CORNER_COMMON_BOTTOM, out)

                SegmentType.BOTTOM_ROOM_OUT_LEFT_BORDER_BOTTOM -> draw(s.x, s.y, BOTTOM_ROOM_OUT_LEFT_BORDER_BOTTOM, out)
                SegmentType.BOTTOM_ROOM_OUT_LEFT_BORDER_TOP -> draw(s.x, s.y, BOTTOM_ROOM_OUT_LEFT_BORDER_TOP, out)
                SegmentType.BOTTOM_ROOM_OUT_RIGHT_BORDER_TOP -> draw(s.x, s.y, BOTTOM_ROOM_OUT_RIGHT_BORDER_TOP, out)
                SegmentType.BOTTOM_ROOM_OUT_RIGHT_BORDER_BOTTOM -> draw(s.x, s.y, BOTTOM_ROOM_OUT_RIGHT_BORDER_BOTTOM, out)

                SegmentType.ROOM_WALL_START_BOTTOM -> draw(s.x, s.y, ROOM_WALL_START_BOTTOM, out)
                SegmentType.ROOM_WALL_START_TOP -> draw(s.x, s.y, ROOM_WALL_START_TOP, out)
                SegmentType.ROOM_WALL_COMMON_BOTTOM -> draw(s.x, s.y, ROOM_WALL_COMMON_BOTTOM, out)
                SegmentType.ROOM_WALL_COMMON_TOP -> draw(s.x, s.y, ROOM_WALL_COMMON_TOP, out)
                SegmentType.ROOM_WALL_END_BOTTOM -> draw(s.x, s.y, ROOM_WALL_END_BOTTOM, out)
                SegmentType.ROOM_WALL_END_TOP -> draw(s.x, s.y, ROOM_WALL_END_TOP, out)

                SegmentType.NONE -> {
                    // ignore
                }

//                SegmentType.TOP_LEFT_CORNER_OUT_BORDER_BOTTOM -> draw(s.x, s.y, ROOM_WALL_END_TOP, out)
//                SegmentType.TOP_RIGHT_CORNER_OUT_BORDER_BOTTOM -> draw(s.x, s.y, ROOM_WALL_END_TOP, out)

//                Segments.STONE -> draw(s.x, s.y, stone, out)
            }
        }
    }

    private fun flip(image: BufferedImage): BufferedImage {
        val newImage = BufferedImage(
            TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB
        )
        val g = newImage.createGraphics()
        g.transform(affineTransform)
        g.drawImage(image, 0, 0, null)
        g.dispose()
        return newImage
    }

}