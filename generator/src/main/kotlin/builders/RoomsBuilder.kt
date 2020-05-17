package builders

import com.sun.org.apache.xpath.internal.operations.Bool
import generator.Config
import model.Exit
import model.Room
import kotlin.random.Random

private const val LOOP_STEP = 2
private const val ROOMS_COUNT = 6

class RoomsBuilder(private val Config: Config) {

    // rooms count should not be odd
    internal fun buildRooms(
        levelHeight: Int,
        outs: List<Exit>,
        isFirst: Boolean
    ): MutableList<Room> {
        val initialRoomSize = levelHeight / ROOMS_COUNT
        val heights = IntArray(ROOMS_COUNT) { initialRoomSize }

        for (i in 0 until heights.size - 1 step LOOP_STEP) {
            val noise = 1
            if (Random.nextBoolean()) {
                heights[i] += noise
                heights[i + 1] -= noise
            } else {
                heights[i] -= noise
                heights[i + 1] += noise
            }
        }

//        if (Random.nextBoolean()) {
//            heights[ROOMS_COUNT - 1] -= 1
//            heights[ROOMS_COUNT - 2] += 2
//        } else {
//            heights[ROOMS_COUNT - 1] += 1
//        }

        val rooms = ArrayList<Room>(ROOMS_COUNT)

        for (i in 0 until ROOMS_COUNT) {
            val y: Int

            if (i == 0) {
                if (isFirst) {
                    y = 2
                    heights[0] -= 2
                } else {
                    y = 0
                }
            } else {
                y = rooms[i - 1].y + rooms[i - 1].height
            }

            val room = Room(y, heights[i], Config.levelWidth)
            rooms.add(room)
        }

        return rooms
    }
}
