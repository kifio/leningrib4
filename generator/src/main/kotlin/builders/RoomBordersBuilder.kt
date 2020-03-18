package builders

import generator.Config
import generator.Generator.Side
import model.*
import generator.SegmentType
import mappers.HorizontalSideMapper
import mappers.VerticalSideMapper
import java.lang.IllegalStateException
import java.util.*

class RoomBordersBuilder {

    lateinit var topLeftSegment: Segment
    lateinit var bottomLeftSegment: Segment
    lateinit var topRightSegment: Segment
    lateinit var bottomRightSegment: Segment

    fun update(
        isStart: Boolean,
        isEnd: Boolean,
        x: Int,
        y: Int
    ) {
        if (isStart) {
            bottomLeftSegment = Segment(x, y, SegmentType.ROOM_WALL_START_BOTTOM)
            topLeftSegment = Segment(x, y + 1, SegmentType.ROOM_WALL_START_TOP)
        } else {
            bottomLeftSegment = Segment(x, y, SegmentType.ROOM_WALL_COMMON_BOTTOM)
            topLeftSegment = Segment(x, y + 1, SegmentType.ROOM_WALL_COMMON_TOP)
        }

        if (isEnd) {
            bottomRightSegment = Segment(x + 1, y, SegmentType.ROOM_WALL_END_BOTTOM)
            topRightSegment = Segment(x + 1, y + 1, SegmentType.ROOM_WALL_END_TOP)
        } else {
            bottomRightSegment = Segment(x + 1, y, SegmentType.ROOM_WALL_COMMON_BOTTOM)
            topRightSegment = Segment(x + 1, y + 1, SegmentType.ROOM_WALL_COMMON_TOP)
        }
    }
}