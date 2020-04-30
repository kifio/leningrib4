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

    fun getFirstLevel(worldMap: WorldMap, levelConfig: Config): LevelMap {
        val map = LevelMap(mutableSetOf(), mutableListOf(), levelConfig)
        val exitsBuilder = ExitsBuilder(levelConfig)

        map.apply {
            val exits = exitsBuilder.getExits(0, 0, Side.TOP, null)

            addExits(exits)

            val bordersBuilder = BordersBuilder(levelConfig, exits)

            addSegments(bordersBuilder.buildBorder(Side.BOTTOM, null))
            addSegments(bordersBuilder.buildBorder(Side.TOP, null))
            addSegments(bordersBuilder.buildBorder(Side.LEFT, null))
            addSegments(bordersBuilder.buildBorder(Side.RIGHT, null))

            rooms.add(Room(1, 8, levelConfig.levelWidth))
    		rooms.add(Room(8, 7, levelConfig.levelWidth))
	    	rooms.add(Room(15, 7, levelConfig.levelWidth))
		    rooms.add(Room(22, 8, levelConfig.levelWidth))

            updateRoomBorders(this)

            val additionalSegmentsMapper = AdditionalSegmentsMapper(levelConfig)
            val additionalSegments = getSegments().filter {
                (it.y == 0 || it.y == levelConfig.levelHeight - 1)
                        && (it .x > 0 && it.x < levelConfig.levelWidth - 1)
            }.map {
                additionalSegmentsMapper.convert(it)
            }

            addSegments(additionalSegments)

        }

        return map

    }

    fun generateLevel(
        x: Int, y: Int,
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

            val exits = exitsBuilder.getExits(x, y, Side.LEFT, leftNeighbour)
                .plus(exitsBuilder.getExits(x, y, Side.RIGHT, rightNeighbour))
                .plus(exitsBuilder.getExits(x, y, Side.TOP, topNeighbour))
                .plus(exitsBuilder.getExits(x, y, Side.BOTTOM, bottomNeighbour))

            addExits(exits)

            val bordersBuilder = BordersBuilder(levelConfig, exits)

            addSegments(bordersBuilder.buildBorder(Side.BOTTOM, bottomNeighbour))
            addSegments(bordersBuilder.buildBorder(Side.TOP, topNeighbour))
            addSegments(bordersBuilder.buildBorder(Side.LEFT, leftNeighbour))
            addSegments(bordersBuilder.buildBorder(Side.RIGHT, rightNeighbour))

            val outerBordersHeight = 2  // height of top and bottom borders on screen
            val roomsSpace = levelConfig.levelHeight - outerBordersHeight

            rooms.addAll(RoomsBuilder(levelConfig).buildRooms(roomsSpace, exits))

            updateRoomBorders(this)

            val additionalSegmentsMapper = AdditionalSegmentsMapper(levelConfig)
            val additionalSegments = getSegments().filter {
                (it.y == 0 || it.y == levelConfig.levelHeight - 1)
                        && (it .x > 0 && it.x < levelConfig.levelWidth - 1)
            }.map {
                additionalSegmentsMapper.convert(it)
            }

            addSegments(additionalSegments)
        }
        return map
    }

    private fun updateRoomBorders(map: LevelMap) {

        val roomBordersBuilder = RoomBordersBuilder()

        for (room in map.rooms) {
            if (room.treesPositions.isEmpty()) {
                continue
            }

            val treeForRemoving = getRandomNotOddNumber(1, room.treesPositions.size - 1)

            for (i in room.treesPositions.indices) {
                val tx = room.treesPositions[i]
                val ty = room.borderY

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