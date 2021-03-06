package generator

import builders.*
import mappers.AdditionalSegmentsMapper
import model.*
import kotlin.random.Random

class Generator {

    enum class Side {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }

    fun getFirstLevel(levelConfig: Config, firstRoomHeight: Int): LevelMap {
        val map = LevelMap(mutableSetOf(), mutableListOf(), levelConfig)

        map.apply {
            val exits = listOf(Exit(4, levelConfig.levelHeight - 1))

            addExits(exits)

            val bordersBuilder = BordersBuilder(levelConfig, exits, true)

            addSegments(bordersBuilder.buildBorder(Side.BOTTOM, null))
            addSegments(bordersBuilder.buildBorder(Side.TOP, null))
            addSegments(bordersBuilder.buildBorder(Side.LEFT, null))
            addSegments(bordersBuilder.buildBorder(Side.RIGHT, null))

            val heights = arrayOf(firstRoomHeight, 7, 7, 7)

            rooms.add(Room(1, heights[0], levelConfig.levelWidth))
            rooms.add(Room(heights[0] + 1, heights[1], levelConfig.levelWidth))
            rooms.add(Room(heights[0] + 1 + heights[1], heights[2], levelConfig.levelWidth))
            rooms.add(Room(heights[0] + 1 + heights[1] + heights[2], heights[3], levelConfig.levelWidth))

            updateRoomBorders(this, arrayOf(2, Int.MAX_VALUE, 0, 2))

            val additionalSegmentsMapper = AdditionalSegmentsMapper(levelConfig)
            val additionalSegments = getSegments().filter {
                (it.y == 0 || it.y == levelConfig.levelHeight - 1)
                        && (it.x > 0 && it.x < levelConfig.levelWidth - 1)
            }.map {
                additionalSegmentsMapper.convert(it)
            }

            addSegments(additionalSegments)

        }
        return map
    }

    fun generateLevel(
            x: Int, y: Int, enterX: Int?,
            worldMap: WorldMap,
            levelConfig: Config
    ): LevelMap {

        val map = LevelMap(mutableSetOf(), mutableListOf(), levelConfig)
        val exitsBuilder = ExitsBuilder(levelConfig)

        map.apply {

            val bottomNeighbour = worldMap.getBottomNeighbour(x, y)
            val topNeighbour = worldMap.getTopNeighbour(x, y)
            val leftNeighbour = worldMap.getLeftNeighbour(x, y)
            val rightNeighbour = worldMap.getRightNeighbour(x, y)

            val exits = exitsBuilder.getExits(x, y, Side.TOP, topNeighbour)

            if (enterX != null) {
                exits.add(Exit(enterX, 0, false))
            }

            addExits(exits)

            val bordersBuilder = BordersBuilder(levelConfig, exits, false)

            if (y == 0) {
                addSegments(bordersBuilder.buildBorder(Side.BOTTOM, bottomNeighbour))
            } else {
                addSegment(Segment(0, 0, SegmentType.LEFT_COMMON_BOTTOM))
                addSegment(Segment(levelConfig.levelWidth - 1, 0, SegmentType.RIGHT_COMMON_BOTTOM))
            }

            addSegments(bordersBuilder.buildBorder(Side.TOP, topNeighbour))
            addSegments(bordersBuilder.buildBorder(Side.LEFT, leftNeighbour))
            addSegments(bordersBuilder.buildBorder(Side.RIGHT, rightNeighbour))

            rooms.addAll(RoomsBuilder(levelConfig).buildRooms(levelConfig.levelHeight, exits, y == 0))

            val treesForRemoving = arrayOfNulls<Int?>(rooms.size)
            for (i in 0 until rooms.size) {
                if (i == 0) {
                    treesForRemoving[i] = getRandomNotOddNumber(1, rooms[i].treesPositions.size - 1)
                } else {
                    do {
                        treesForRemoving[i] = getRandomNotOddNumber(1, rooms[i].treesPositions.size - 1)
                    } while (treesForRemoving[i - 1] == treesForRemoving[i])
                }
            }

            updateRoomBorders(this, treesForRemoving)

            val additionalSegmentsMapper = AdditionalSegmentsMapper(levelConfig)
            val additionalSegments = getSegments().filter {
                (it.y == 0 || it.y == levelConfig.levelHeight - 1)
                        && (it.x >= 0 && it.x < levelConfig.levelWidth)
            }.map {
                additionalSegmentsMapper.convert(it)
            }

            addSegments(additionalSegments)
        }
        return map
    }

    private fun updateRoomBorders(map: LevelMap, treesForRemoving: Array<Int?>) {

        val roomBordersBuilder = RoomBordersBuilder()

        for (index in 0 until map.rooms.size - 1) {

            val room = map.rooms[index]
            if (room.treesPositions.isEmpty()) {
                continue
            }

            val treeForRemoving = treesForRemoving[index] ?: continue

            for (i in room.treesPositions.indices) {
                val tx = room.treesPositions[i]
                val ty = room.y + room.height - 2

                if (treeForRemoving == i + 1) {
                    if (i == 0) {
                        roomBordersBuilder.update(isStart = true, isEnd = true, x = tx, y = ty)
                    } else {
                        roomBordersBuilder.update(isStart = false, isEnd = true, x = tx, y = ty)
                    }
                } else if (treeForRemoving == i - 1) {
                    if (i == room.treesPositions.size - 1) {
                        roomBordersBuilder.update(isStart = true, isEnd = false, x = tx, y = ty)
                    } else {
                        // Возможно тут ошибка
                        roomBordersBuilder.update(isStart = true, isEnd = false, x = tx, y = ty)
                    }
                } else if (treeForRemoving != i) {
                    if (i == 0) {
                        roomBordersBuilder.update(isStart = true, isEnd = false, x = tx, y = ty)
                    } else if (i == room.treesPositions.size - 1) {
                        roomBordersBuilder.update(isStart = false, isEnd = false, x = tx, y = ty)
                    } else {
                        roomBordersBuilder.update(isStart = false, isEnd = false, x = tx, y = ty)
                    }
                }

                map.addSegment(roomBordersBuilder.topLeftSegment)
                map.addSegment(roomBordersBuilder.bottomLeftSegment)
                map.addSegment(roomBordersBuilder.topRightSegment)
                map.addSegment(roomBordersBuilder.bottomRightSegment)
            }
        }
    }

    companion object {
        fun getRandomNotOddNumber(from: Int, to: Int): Int {
            val outCoordinate = Random.nextInt(from, to)
            return when {
                outCoordinate % 2 != 0 -> outCoordinate
                outCoordinate == from -> outCoordinate + 1
                else -> outCoordinate - 1
            }
        }
    }
}