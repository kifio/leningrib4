package builders

import generator.Config
import generator.Generator.Side
import model.*
import generator.SegmentType
import mappers.HorizontalSideMapper
import mappers.VerticalSideMapper
import java.lang.IllegalStateException
import java.util.*

class BordersBuilder(
    private val levelConfig: Config,
    private val exits: List<Exit>
) {

    private val verticalSideMapper = VerticalSideMapper()
    private val horizontalSideMapper = HorizontalSideMapper()

    private val verticalSide = mutableListOf<Segment>()
    private val horizontalSide = mutableListOf<Segment>()
    val list = LinkedList<Segment>()

    private val bottomSegments = HorizontalSegments(
        SegmentType.BOTTOM_COMMON_LEFT_BOTTOM,
        SegmentType.BOTTOM_COMMON_RIGHT_BOTTOM,
        SegmentType.BOTTOM_ROOM_OUT_LEFT_BORDER_BOTTOM,
        SegmentType.BOTTOM_ROOM_OUT_RIGHT_BORDER_BOTTOM)

    private val topSegments = HorizontalSegments(
        SegmentType.TOP_COMMON_LEFT_TOP,
        SegmentType.TOP_COMMON_RIGHT_TOP,
        SegmentType.TOP_ROOM_OUT_LEFT_BORDER_TOP,
        SegmentType.TOP_ROOM_OUT_RIGHT_BORDER_TOP)

    internal fun buildBorder(side: Side, neighbour: LevelMap?): List<Segment> =
        if (neighbour != null) {
            if (side == Side.LEFT) {
                horizontalSideMapper.mapSideBySide(neighbour.getNeighborSide(side), side)
            } else if (side == Side.BOTTOM) {
                verticalSideMapper.mapSideBySide(neighbour.getNeighborSide(side), side)
            } else {
                generateSide(side)
                list
            }
        } else {
            generateSide(side)
            list
        }

    private fun generateSide(side: Side) {
        list.clear()
        if (side == Side.LEFT) {
            buildLeftSide()
            list.addAll(verticalSide)
        } else if (side == Side.RIGHT) {
            buildRightSide()
            list.addAll(verticalSide)
        } else if (side == Side.BOTTOM) {
            buildHorizontalSide(side, 0, bottomSegments)
            list.addAll(horizontalSide)
        } else if (side == Side.TOP) {
            buildHorizontalSide(side, levelConfig.levelHeight - 1, topSegments)
            list.addAll(horizontalSide)
        }
    }

    private fun buildHorizontalSide(
        side: Side,
        y: Int,
        segments: HorizontalSegments
    ) {
        buildHorizontalSide(y, segments)
        val exits = exits.getExitsOnSide(side)

        if (exits.isNotEmpty()) {
            val exit = IntArray(2)

            exit[0] = exits.first().x

            if (exit[0] % 2 == 0) {
                exit[1] = exit[0] - 1
            } else {
                exit[1] = exit[0] + 1
            }

            horizontalSide.removeIf { exit.contains(it.x) }

            val exitsBorderLeft = exit.min()?.minus(1)
            val exitsBorderRight = exit.max()?.plus(1)

            for (i in horizontalSide.indices) {
                val segment = horizontalSide[i]
                if (segment.x == exitsBorderLeft) {
                    horizontalSide[i].setValue(segments.roomLeftBorder)
                } else if (segment.x == exitsBorderRight) {
                    horizontalSide[i].setValue(segments.roomRightBorder)
                }
            }
        }
    }

    private fun buildLeftSide() {
        verticalSide.clear()

        // Generate bottom corner
        if (exits.contains(1, 0)) {
            verticalSide.add(Segment(0, 0, SegmentType.BOTTOM_LEFT_CORNER_OUT_BORDER_BOTTOM))
        } else {
            verticalSide.add(Segment(0, 0, SegmentType.BOTTOM_LEFT_CORNER_COMMON_BOTTOM))
        }

        verticalSide.add(Segment(0, 1, SegmentType.BOTTOM_LEFT_CORNER_OUT_BORDER_TOP))

        // Generate begin of the wall
        verticalSide.add(Segment(0, 2, SegmentType.LEFT_BEGIN_BOTTOM))
        verticalSide.add(Segment(0, 3, SegmentType.LEFT_BEGIN_TOP))

        // Generate main part of the wall
        for (i in 4 until levelConfig.levelHeight - 2) {
            verticalSide.add(Segment(0, i,
                if (i % 2 == 0) SegmentType.LEFT_COMMON_BOTTOM else SegmentType.LEFT_COMMON_TOP))
        }

        // Generate top corner
        if (exits.contains(1, levelConfig.levelHeight - 1)) {
            verticalSide.add(Segment(0, levelConfig.levelHeight - 1, SegmentType.TOP_LEFT_CORNER_OUT_BORDER_TOP))
        } else {
            verticalSide.add(Segment(0, levelConfig.levelHeight - 1, SegmentType.TOP_LEFT_CORNER_COMMON_TOP))
        }

        verticalSide.add(Segment(0, levelConfig.levelHeight - 2, SegmentType.TOP_LEFT_CORNER_COMMON_BOTTOM))
    }

    private fun buildRightSide() {
        verticalSide.clear()

        // Generate bottom corner
        val x = levelConfig.levelWidth - 1
        if (exits.contains(levelConfig.levelWidth - 2, 0)) {
            verticalSide.add(Segment(x, 0, SegmentType.BOTTOM_RIGHT_CORNER_OUT_BORDER_BOTTOM))
        } else {
            verticalSide.add(Segment(x, 0, SegmentType.BOTTOM_RIGHT_CORNER_COMMON_BOTTOM))
        }

        verticalSide.add(Segment(x, 1, SegmentType.BOTTOM_RIGHT_CORNER_COMMON_TOP))

        // Generate main part of the wall
        for (i in 2 until levelConfig.levelHeight - 2) {
            verticalSide.add(Segment(x, i,
                if (i % 2 == 0) SegmentType.RIGHT_COMMON_BOTTOM else SegmentType.RIGHT_COMMON_TOP))
        }

        val rightSideExits = exits.getExitsOnSide(Side.RIGHT)
        val exitsBorders = mutableListOf<Int>()

        // TODO: Do it in one loop
        // Just do not add this coordinates in list
        verticalSide.forEach {
            if (rightSideExits.contains(x, it.y)) {
                exitsBorders.add(it.y - 2)
                exitsBorders.add(it.y + 1)
            }
        }

        // Remove exits and one segment below exit to make wall more consistent
        verticalSide.removeIf { rightSideExits.contains(x, it.y) || rightSideExits.contains(x, it.y + 1) }

        for (i in verticalSide.indices) {
            val segment = verticalSide[i]
            if (exitsBorders.contains(segment.y)) {
                if (exitsBorders.size % 2 == 0) {
                    verticalSide[i].setValue(SegmentType.RIGHT_END_TOP)
                } else {
                    verticalSide[i].setValue(SegmentType.RIGHT_BEGIN_BOTTOM)
                }
                exitsBorders.removeAt(0)
            }
        }

        // Generate top corner
        val y = levelConfig.levelHeight - 1
        if (exits.contains(levelConfig.levelWidth - 2, y)) {
            verticalSide.add(Segment(x, y, SegmentType.TOP_RIGHT_CORNER_OUT_BORDER_TOP))
        } else {
            verticalSide.add(Segment(x, y, SegmentType.TOP_RIGHT_CORNER_COMMON_TOP))
        }

        verticalSide.add(Segment(x, y - 1, SegmentType.TOP_RIGHT_CORNER_COMMON_BOTTOM))
    }

    // from - included, to - excluded
    private fun buildHorizontalSide(
        y: Int,
        segments: HorizontalSegments
    ) {

        horizontalSide.clear()

        var range = levelConfig.levelWidth - 2
        var i = 1
        while (range > 0) {
            horizontalSide.add(Segment(i, y, if (i % 2 != 0) segments.commonRight else segments.commonLeft))
            range -= 1
            i += 1
        }
    }

    private fun List<Exit>.contains(x: Int, y: Int): Boolean {
        return this.find { it.x == x && it.y == y } != null
    }

    private fun List<Exit>.getExitsOnSide(side: Side): List<Exit> {
        return this.filter {
            if (side == Side.LEFT) {
                it.x == 0
            } else if (side == Side.RIGHT) {
                it.x == levelConfig.levelWidth - 1
            } else if (side == Side.BOTTOM) {
                it.y == 0
            } else if (side == Side.TOP) {
                it.y == levelConfig.levelHeight - 1
            } else {
                throw IllegalStateException("Unknown side: ${side.name}")
            }
        }.sortedBy { it.y }
    }

    private data class HorizontalSegments(
        val commonLeft: SegmentType,
        val commonRight: SegmentType,
        val roomLeftBorder: SegmentType,
        val roomRightBorder: SegmentType
    )
}