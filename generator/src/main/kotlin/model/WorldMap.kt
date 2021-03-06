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

    fun addFirstLevel(Config: Config, firstRoomHeight: Int): LevelMap {
        val level = generator.getFirstLevel(Config, firstRoomHeight)
        levels[Pair(0, 0)] = level
        return level
    }

    fun addLevel(x: Int,
                 y: Int,
                 enterX: Int?,
                 Config: Config): LevelMap {
        val level = generator.generateLevel(x, y, enterX,this, Config)
        levels[Pair(x, y)] = level
        return level
    }

    fun getWidth() = (levels.keys.map { it.first }.max() ?: 0) + 1
    fun getHeight() = (levels.keys.map { it.second }.max() ?: 0) + 1
    fun getLevels() = levels
    fun getLevel(x: Int, y: Int) = levels.get(Pair(x, y))

    fun getLeftNeighbour(x: Int, y: Int) = levels[Pair(x - 1, y)]
    fun getRightNeighbour(x: Int, y: Int) = levels[Pair(x + 1, y)]
    fun getTopNeighbour(x: Int, y: Int) = levels[Pair(x, y + 1)]
    fun getBottomNeighbour(x: Int, y: Int) = levels[Pair(x, y - 1)]
}