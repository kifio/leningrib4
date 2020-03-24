package model

import generator.Config
import generator.Generator.Side

class LevelMap(
    private val segments: MutableSet<Segment>,
    private val exits: MutableList<Exit>,
    private val Config: Config
) {

    lateinit var rooms: List<Room>

    fun getSegments(): MutableSet<Segment> = segments

    fun getEnters(side: Side) = when(side) {
        Side.RIGHT -> exits.filter { it.x == 0 }
        Side.BOTTOM -> exits.filter { it.y == Config.levelHeight - 1 }
        Side.LEFT -> exits.filter { it.x == Config.levelWidth - 1 }
        Side.TOP -> exits.filter { it.y == 0 }
    }

    fun getNeighborSide(side: Side) = when(side) {
        Side.LEFT -> segments.filter { it.x == Config.levelWidth - 1 && it.y > 0 && it.y < Config.levelHeight - 1 }
        Side.TOP -> segments.filter { it.y == 0 }
        Side.RIGHT -> segments.filter { it.x == 0 && it.y > 0 && it.y < Config.levelHeight - 1 }
        Side.BOTTOM -> segments.filter { it.y == Config.levelHeight - 1 }
    }

    fun addSegments(segments: List<Segment>) = this.segments.addAll(segments)

    fun addSegment(segment: Segment) = this.segments.add(segment)

    fun addExits(exits: List<Exit>) = this.exits.addAll(exits)
}