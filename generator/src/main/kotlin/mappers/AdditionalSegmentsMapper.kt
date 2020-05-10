package mappers

import generator.Config
import generator.SegmentType
import mappers.SideMapper
import model.Segment

internal class AdditionalSegmentsMapper(private val levelConfig: Config) : SideMapper() {

    init {
        segmentsMap[SegmentType.BOTTOM_COMMON_RIGHT_BOTTOM] = SegmentType.BOTTOM_COMMON_RIGHT_TOP
        segmentsMap[SegmentType.BOTTOM_COMMON_LEFT_BOTTOM] = SegmentType.BOTTOM_COMMON_LEFT_TOP
        segmentsMap[SegmentType.BOTTOM_ROOM_OUT_LEFT_BORDER_BOTTOM] = SegmentType.BOTTOM_ROOM_OUT_LEFT_BORDER_TOP
        segmentsMap[SegmentType.BOTTOM_ROOM_OUT_RIGHT_BORDER_BOTTOM] = SegmentType.BOTTOM_ROOM_OUT_RIGHT_BORDER_TOP

        segmentsMap[SegmentType.BOTTOM_LEFT_CORNER_COMMON_BOTTOM] = SegmentType.BOTTOM_LEFT_CORNER_COMMON_TOP
        segmentsMap[SegmentType.BOTTOM_RIGHT_CORNER_COMMON_BOTTOM] = SegmentType.BOTTOM_RIGHT_CORNER_COMMON_TOP

        segmentsMap[SegmentType.LEFT_COMMON_TOP] = SegmentType.LEFT_COMMON_BOTTOM
        segmentsMap[SegmentType.RIGHT_COMMON_TOP] = SegmentType.RIGHT_COMMON_BOTTOM

        segmentsMap[SegmentType.LEFT_COMMON_BOTTOM] = SegmentType.LEFT_COMMON_TOP
        segmentsMap[SegmentType.RIGHT_COMMON_BOTTOM] = SegmentType.RIGHT_COMMON_TOP

        segmentsMap[SegmentType.TOP_COMMON_RIGHT_TOP] = SegmentType.TOP_COMMON_RIGHT_BOTTOM
        segmentsMap[SegmentType.TOP_COMMON_LEFT_TOP] = SegmentType.TOP_COMMON_LEFT_BOTTOM
        segmentsMap[SegmentType.TOP_ROOM_OUT_LEFT_BORDER_TOP] = SegmentType.TOP_ROOM_OUT_LEFT_BORDER_BOTTOM
        segmentsMap[SegmentType.TOP_ROOM_OUT_RIGHT_BORDER_TOP] = SegmentType.TOP_ROOM_OUT_RIGHT_BORDER_BOTTOM
        segmentsMap[SegmentType.ROOM_WALL_COMMON_TOP] = SegmentType.ROOM_WALL_COMMON_BOTTOM
        segmentsMap[SegmentType.ROOM_WALL_END_TOP] = SegmentType.ROOM_WALL_END_BOTTOM
        segmentsMap[SegmentType.ROOM_WALL_START_TOP] = SegmentType.ROOM_WALL_START_BOTTOM
      }

    override fun convert(segment: Segment): Segment {
        val targetY = if (segment.y == 0) 1 else levelConfig.levelHeight - 2
        return convertSegment(segment, segment.x, targetY)
    }

}