package model

import generator.Generator
import generator.Generator.Side
import generator.Config

class WorldMap {

    private val generator = Generator()
    private val levels: MutableMap<Pair<Int, Int>, LevelMap> = mutableMapOf()

    fun addLevel(x: Int, y: Int, level: LevelMap): LevelMap {
        levels[Pair(x, y)] = level
        return level
    }

    fun addLevel(x: Int, y: Int, Config: Config): LevelMap {
        val level = generator.generateLevel(x, y, this, Config)
        levels[Pair(x, y)] = level
        return level
    }

    fun getWidth() = (levels.keys.map { it.first }.max() ?: 0) + 1
    fun getHeight() = (levels.keys.map { it.second }.max() ?: 0) + 1
    fun getLevels() = levels
    fun getLevel(x: Int, y: Int) = levels.get(Pair(x, y))

    fun getNeighbour(x: Int, y: Int, side: Side) = when (side) {
        Side.LEFT -> levels[Pair(x - 1, y)]
        Side.TOP -> levels[Pair(x, y + 1)]
        Side.RIGHT -> levels[Pair(x + 1, y)]
        Side.BOTTOM -> levels[Pair(x, y - 1)]
    }
}