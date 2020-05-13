package builders

import generator.Config
import generator.Generator
import generator.Generator.Side
import kotlin.random.Random
import model.*
import javax.sound.sampled.LineEvent

class ExitsBuilder(private val levelConfig: Config) {

    private val exitsOnSide = mutableListOf<Int>()

    // Returns all exits on current side
    internal fun getExits(x: Int, y: Int, side: Side, neighbour: LevelMap?): MutableList<Exit> {
        val enters = neighbour?.getEnters(side)
        if (enters != null) return mapEntersToExits(enters, side).toMutableList()
        generateSide(getExitsCount(side, x, y), getSideSize(side))
        return createExitsFromLine(side).toMutableList()
    }

    // Generate exits positions on the line by some rules
    private fun generateSide(count: Int, sideSize: Int) {
        exitsOnSide.clear()
        when (count) {
            0 -> {
                return
            }
            1 -> {
                exitsOnSide.add(Random.nextInt(1, sideSize - 1))
            }
            else -> {
                exitsOnSide.add(3)
                exitsOnSide.add(Generator.getRandomNotOddNumber(sideSize / 2, sideSize - 1))
                exitsOnSide.sort()
            }
        }
    }

    private fun getSideSize(side: Side) =
            if (side == Side.TOP || side == Side.BOTTOM) levelConfig.levelWidth else levelConfig.levelHeight

    private fun getExitsCount(side: Side, x: Int, y: Int): Int {
        return if (side == Side.LEFT) {
            0
        } else if (side == Side.RIGHT) {
            2
        } else if (side == Side.TOP) {
            1
        } else {    // Side.BOTTOM
            if (x == 0 && y == 0) 1 else 0
        }
    }

    // Transform exits from neighbour to enters on the current level
    private fun mapEntersToExits(exits: List<Exit>, side: Side) =
            if (side == Side.TOP || side == Side.BOTTOM) {
                exits.map { Exit(it.x, (levelConfig.levelHeight - it.y)) }
            } else {
                exits.map { Exit((levelConfig.levelWidth - it.x), it.y) }
            }

    // Transform list of Int to list of Exit instances, depends on the side
    private fun createExitsFromLine(side: Side) =
            when (side) {
                Side.TOP -> exitsOnSide.map { Exit(it, levelConfig.levelHeight - 1) }
                Side.BOTTOM -> exitsOnSide.map { Exit(it, 0) }
                Side.LEFT -> exitsOnSide.map { Exit(0, it) }
                Side.RIGHT -> exitsOnSide.map { Exit(levelConfig.levelWidth - 1, it) }
            }
}