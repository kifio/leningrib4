package mappers

import generator.SegmentType
import mappers.SideMapper
import model.Segment

internal class HorizontalSideMapper : SideMapper() {

    init {
        segmentsMap[SegmentType.TOP_RIGHT_CORNER_COMMON_TOP] = SegmentType.TOP_LEFT_CORNER_COMMON_TOP
        segmentsMap[SegmentType.TOP_RIGHT_CORNER_COMMON_BOTTOM] = SegmentType.TOP_LEFT_CORNER_COMMON_BOTTOM
        segmentsMap[SegmentType.TOP_RIGHT_CORNER_OUT_BORDER_TOP] = SegmentType.TOP_RIGHT_CORNER_OUT_BORDER_TOP

        segmentsMap[SegmentType.RIGHT_COMMON_TOP] = SegmentType.LEFT_COMMON_TOP
        segmentsMap[SegmentType.RIGHT_COMMON_BOTTOM] = SegmentType.LEFT_COMMON_BOTTOM

        segmentsMap[SegmentType.RIGHT_BEGIN_TOP] = SegmentType.LEFT_BEGIN_TOP
        segmentsMap[SegmentType.RIGHT_BEGIN_BOTTOM] = SegmentType.LEFT_BEGIN_BOTTOM

        segmentsMap[SegmentType.RIGHT_END_TOP] = SegmentType.LEFT_END_TOP
        segmentsMap[SegmentType.RIGHT_END_BOTTOM] = SegmentType.LEFT_END_BOTTOM

        segmentsMap[SegmentType.LEFT_COMMON_TOP] = SegmentType.RIGHT_COMMON_TOP
        segmentsMap[SegmentType.LEFT_COMMON_BOTTOM] = SegmentType.RIGHT_COMMON_BOTTOM

        segmentsMap[SegmentType.LEFT_BEGIN_TOP] = SegmentType.RIGHT_BEGIN_TOP
        segmentsMap[SegmentType.LEFT_BEGIN_BOTTOM] = SegmentType.RIGHT_BEGIN_BOTTOM

        segmentsMap[SegmentType.LEFT_END_TOP] = SegmentType.RIGHT_END_TOP
        segmentsMap[SegmentType.LEFT_END_BOTTOM] = SegmentType.RIGHT_END_BOTTOM

        segmentsMap[SegmentType.BOTTOM_RIGHT_CORNER_COMMON_TOP] = SegmentType.BOTTOM_LEFT_CORNER_COMMON_TOP
        segmentsMap[SegmentType.BOTTOM_RIGHT_CORNER_COMMON_BOTTOM] = SegmentType.BOTTOM_LEFT_CORNER_COMMON_BOTTOM
        segmentsMap[SegmentType.BOTTOM_RIGHT_CORNER_OUT_BORDER_TOP] = SegmentType.BOTTOM_RIGHT_CORNER_OUT_BORDER_TOP
        segmentsMap[SegmentType.BOTTOM_RIGHT_CORNER_OUT_BORDER_BOTTOM] = SegmentType.BOTTOM_RIGHT_CORNER_OUT_BORDER_BOTTOM

        segmentsMap[SegmentType.ROOM_WALL_START_TOP] = SegmentType.NONE
        segmentsMap[SegmentType.ROOM_WALL_START_BOTTOM] = SegmentType.NONE

        segmentsMap[SegmentType.ROOM_WALL_END_TOP] = SegmentType.NONE
        segmentsMap[SegmentType.ROOM_WALL_END_BOTTOM] = SegmentType.NONE

        segmentsMap[SegmentType.ROOM_WALL_COMMON_TOP] = SegmentType.NONE
        segmentsMap[SegmentType.ROOM_WALL_COMMON_BOTTOM] = SegmentType.NONE
    }

    override fun convert(segment: Segment): Segment {
        return convertSegment(segment, 0, segment.y)
    }

}