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
        private val exits: List<Exit>,
        isTutorial: Boolean
) {

    private val verticalSideMapper = VerticalSideMapper()
    private val horizontalSideMapper = HorizontalSideMapper()

    private val verticalSide = mutableListOf<Segment>()
    private val horizontalSide = mutableListOf<Segment>()

    private val bottomSegments = HorizontalSegments(
            SegmentType.BOTTOM_COMMON_LEFT_BOTTOM,
            SegmentType.BOTTOM_COMMON_RIGHT_BOTTOM,
            SegmentType.BOTTOM_ROOM_OUT_LEFT_BORDER_BOTTOM,
            SegmentType.BOTTOM_ROOM_OUT_RIGHT_BORDER_BOTTOM,
            SegmentType.BOTTOM_LEFT_CORNER_COMMON_BOTTOM,
            SegmentType.BOTTOM_RIGHT_CORNER_COMMON_BOTTOM,
            SegmentType.BOTTOM_LEFT_CORNER_OUT_BORDER_BOTTOM,
            SegmentType.BOTTOM_RIGHT_CORNER_OUT_BORDER_BOTTOM)

    private val topSegments = if (isTutorial) {
        HorizontalSegments(
                SegmentType.TOP_COMMON_LEFT_TOP,
                SegmentType.TOP_COMMON_RIGHT_TOP,
                SegmentType.TOP_ROOM_OUT_LEFT_BORDER_TOP,
                SegmentType.TOP_ROOM_OUT_RIGHT_BORDER_TOP,
                SegmentType.TOP_LEFT_CORNER_COMMON_TOP,
                SegmentType.TOP_RIGHT_CORNER_COMMON_TOP,
                SegmentType.TOP_LEFT_CORNER_OUT_BORDER_TOP,
                SegmentType.TOP_RIGHT_CORNER_OUT_BORDER_TOP)
    } else {
        HorizontalSegments(
                SegmentType.ROOM_WALL_COMMON_TOP,
                SegmentType.ROOM_WALL_COMMON_TOP,
                SegmentType.ROOM_WALL_END_TOP,
                SegmentType.ROOM_WALL_START_TOP,
                SegmentType.ROOM_WALL_COMMON_TOP,
                SegmentType.ROOM_WALL_COMMON_TOP,
                SegmentType.ROOM_WALL_END_TOP,
                SegmentType.ROOM_WALL_START_TOP)
    }

    internal fun buildBorder(side: Side, neighbour: LevelMap?): List<Segment> =
            if (neighbour != null) {
                if (side == Side.LEFT) {
                    horizontalSideMapper.mapSideBySide(neighbour.getNeighborSide(side), side)
                } else if (side == Side.BOTTOM) {
                    verticalSideMapper.mapSideBySide(neighbour.getNeighborSide(side), side)
                } else {
                    generateSide(side)
                }
            } else {
                generateSide(side)
            }

    private fun generateSide(side: Side): List<Segment> {
        val list = LinkedList<Segment>()
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
        return list
    }

    private fun buildHorizontalSide(
            side: Side,
            y: Int,
            segments: HorizontalSegments
    ) {

        horizontalSide.clear()
        for (i in 0 until levelConfig.levelWidth) {
            horizontalSide.add(Segment(i, y, if (i % 2 != 0) segments.commonRight else segments.commonLeft))
        }

        val exitCoordinates = exits.getExitsOnSide(side)
        var exitsBorderLeft: Int? = null
        var exitsBorderRight: Int? = null
        var exitSize = 0

        if (exitCoordinates.isNotEmpty()) {
            exitSize = 2
            val exit = IntArray(exitSize)
            exit[0] = exitCoordinates.first().x

            if (exit[0] % 2 == 0) {
                exit[1] = exit[0] - 1
            } else {
                exit[1] = exit[0] + 1
            }

            horizontalSide.removeIf { exit.contains(it.x) }
            exitsBorderLeft = exit.min()?.minus(1)
            exitsBorderRight = exit.max()?.plus(1)
        }

        // TODO: можно оптимизировать
        for (i in horizontalSide.indices) {
            val segment = horizontalSide[i]
            if (segment.x == 0) {
                if (segment.x == exitsBorderLeft) {
                    horizontalSide[i].setValue(segments.exitLeftCornerBorder)
                } else {
                    horizontalSide[i].setValue(segments.commonLeftCorner)
                }
            } else if (segment.x == horizontalSide.size + exitSize - 1) {
                if (segment.x == exitsBorderRight) {
                    horizontalSide[i].setValue(segments.exitRightCornerBorder)
                } else {
                    horizontalSide[i].setValue(segments.commonRightCorner)
                }
            } else {
                if (segment.x == exitsBorderLeft) {
                    horizontalSide[i].setValue(segments.roomLeftBorder)
                } else if (segment.x == exitsBorderRight) {
                    horizontalSide[i].setValue(segments.roomRightBorder)
                }
            }
        }

        horizontalSide
    }

    private fun buildLeftSide() {
        verticalSide.clear()
        verticalSide.add(Segment(0, 1, SegmentType.BOTTOM_LEFT_CORNER_COMMON_TOP))

        // Generate begin of the wall
        verticalSide.add(Segment(0, 2, SegmentType.LEFT_BEGIN_BOTTOM))
        verticalSide.add(Segment(0, 3, SegmentType.LEFT_BEGIN_TOP))

        // Generate main part of the wall
        for (i in 4 until levelConfig.levelHeight) {
            verticalSide.add(Segment(0, i,
                    if (i % 2 == 0) SegmentType.LEFT_COMMON_BOTTOM else SegmentType.LEFT_COMMON_TOP))
        }
    }

    private fun buildRightSide() {
        verticalSide.clear()

        // Generate bottom corner
        val x = levelConfig.levelWidth - 1
        verticalSide.add(Segment(x, 1, SegmentType.BOTTOM_RIGHT_CORNER_COMMON_TOP))

        // Generate main part of the wall
        for (i in 2 until levelConfig.levelHeight) {
            verticalSide.add(Segment(x, i,
                    if (i % 2 == 0) SegmentType.RIGHT_COMMON_BOTTOM else SegmentType.RIGHT_COMMON_TOP))
        }
    }

    private fun buildHorizontalSide(y: Int, segments: HorizontalSegments) {

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
            val roomRightBorder: SegmentType,
            val commonLeftCorner: SegmentType,
            val commonRightCorner: SegmentType,
            val exitLeftCornerBorder: SegmentType,
            val exitRightCornerBorder: SegmentType
    )
}