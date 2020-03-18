package builders

import generator.Config
import model.Exit
import model.Room
import kotlin.random.Random

private const val LOOP_STEP = 2
private const val ROOMS_COUNT = 5

class RoomsBuilder(private val Config: Config) {

    // rooms count should not be odd
    internal fun buildRooms(
        levelHeight: Int,
        outs: List<Exit>
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

        val rooms = ArrayList<Room>(ROOMS_COUNT)

        for (i in 0 until ROOMS_COUNT) {
            val y = if (i == 0) { 1 } else {
                rooms[i - 1].borderY + 2
            }

            val room = Room(y, heights[i], Config.levelWidth)

            for (out in outs) {
                if (room.borderY == out.y) {
                    room.borderY += 1
                    break
                } else if (room.borderY == out.y - 1) {
                    room.borderY -= 2
                    break
                } else if (room.borderY == out.y - 2) {
                    room.borderY -= 1
                    break
                }
            }

            rooms.add(room)
        }
        return rooms
    }
}
