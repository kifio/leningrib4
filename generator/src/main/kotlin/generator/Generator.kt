package generator

import builders.*
import mappers.AdditionalSegmentsMapper
import model.Exit
import model.LevelMap
import model.Segment
import model.WorldMap
import java.util.function.Predicate
import kotlin.random.Random

class Generator {

    enum class Side {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }

    fun generateLevel(
        x: Int, y: Int,
        worldMap: WorldMap,
        levelConfig: Config
    ): LevelMap {

        val map = LevelMap(mutableSetOf(), mutableListOf(), levelConfig)
        val exitsBuilder = ExitsBuilder(levelConfig)

        map.apply {

            val exits = exitsBuilder.getExits(x, y, Side.LEFT, worldMap)
                .plus(exitsBuilder.getExits(x, y, Side.RIGHT, worldMap))
                .plus(exitsBuilder.getExits(x, y, Side.TOP, worldMap))
                .plus(exitsBuilder.getExits(x, y, Side.BOTTOM, worldMap))

            addExits(exits)

            val bordersBuilder =
                BordersBuilder(levelConfig, exits)

            addSegments(
                bordersBuilder.buildBorder(
                    Side.BOTTOM, worldMap.getNeighbour(x, y, Side.BOTTOM)
                )
            )
            addSegments(
                bordersBuilder.buildBorder(
                    Side.TOP, worldMap.getNeighbour(x, y, Side.TOP)
                )
            )
            addSegments(
                bordersBuilder.buildBorder(
                    Side.LEFT, worldMap.getNeighbour(x, y, Side.LEFT)
                )
            )
            addSegments(
                bordersBuilder.buildBorder(
                    Side.RIGHT, worldMap.getNeighbour(x, y, Side.RIGHT)
                )
            )

            buildRooms(levelConfig, exits, this)

//            map.getSegments().removeIf { it.y > 1 }

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

    private fun buildRooms(levelConfig: Config,
                           exits: List<Exit>,
                           map: LevelMap) {

        val outerBordersHeight = 2  // height of top and bottom borders on screen
        val roomsSpace = levelConfig.levelHeight - outerBordersHeight

        map.rooms = RoomsBuilder(levelConfig).buildRooms(
            roomsSpace,
            exits
        )

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