package mappers

import generator.*
import mappers.SideMapper
import model.Segment

// Map horizontal sides, top
internal class VerticalSideMapper : SideMapper() {

    init {
        segmentsMap[SegmentType.TOP_COMMON_LEFT_TOP] =
            SegmentType.BOTTOM_COMMON_LEFT_BOTTOM
        segmentsMap[SegmentType.TOP_COMMON_RIGHT_TOP] =
            SegmentType.BOTTOM_COMMON_RIGHT_BOTTOM

        segmentsMap[SegmentType.TOP_RIGHT_CORNER_COMMON_TOP] =
            SegmentType.BOTTOM_RIGHT_CORNER_COMMON_BOTTOM
        segmentsMap[SegmentType.TOP_LEFT_CORNER_COMMON_TOP] =
            SegmentType.BOTTOM_LEFT_CORNER_COMMON_BOTTOM

        segmentsMap[SegmentType.TOP_RIGHT_CORNER_OUT_BORDER_TOP] =
            SegmentType.BOTTOM_RIGHT_CORNER_OUT_BORDER_BOTTOM
        segmentsMap[SegmentType.TOP_LEFT_CORNER_OUT_BORDER_TOP] =
            SegmentType.BOTTOM_LEFT_CORNER_OUT_BORDER_BOTTOM

        segmentsMap[SegmentType.TOP_ROOM_OUT_RIGHT_BORDER_TOP] =
            SegmentType.BOTTOM_ROOM_OUT_RIGHT_BORDER_BOTTOM
        segmentsMap[SegmentType.TOP_ROOM_OUT_LEFT_BORDER_TOP] =
            SegmentType.BOTTOM_ROOM_OUT_LEFT_BORDER_BOTTOM

        segmentsMap[SegmentType.LEFT_COMMON_TOP] =
            SegmentType.LEFT_COMMON_BOTTOM
        segmentsMap[SegmentType.RIGHT_COMMON_TOP] =
            SegmentType.RIGHT_COMMON_BOTTOM

        segmentsMap[SegmentType.LEFT_BEGIN_TOP] =
            SegmentType.RIGHT_BEGIN_BOTTOM
        segmentsMap[SegmentType.RIGHT_BEGIN_TOP] =
            SegmentType.LEFT_BEGIN_BOTTOM

        segmentsMap[SegmentType.LEFT_END_TOP] =
            SegmentType.RIGHT_END_BOTTOM
        segmentsMap[SegmentType.RIGHT_END_TOP] =
            SegmentType.LEFT_END_BOTTOM

        segmentsMap[SegmentType.LEFT_END_BOTTOM] =
            SegmentType.RIGHT_END_BOTTOM
        segmentsMap[SegmentType.RIGHT_END_BOTTOM] =
            SegmentType.LEFT_END_BOTTOM

    }

    override fun convert(segment: Segment): Segment {
        return convertSegment(segment, segment.x, 0)
    }
}