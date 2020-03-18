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

        segmentsMap[SegmentType.TOP_COMMON_RIGHT_TOP] = SegmentType.TOP_COMMON_RIGHT_BOTTOM
        segmentsMap[SegmentType.TOP_COMMON_LEFT_TOP] = SegmentType.TOP_COMMON_LEFT_BOTTOM
        segmentsMap[SegmentType.TOP_ROOM_OUT_LEFT_BORDER_TOP] = SegmentType.TOP_ROOM_OUT_LEFT_BORDER_BOTTOM
        segmentsMap[SegmentType.TOP_ROOM_OUT_RIGHT_BORDER_TOP] = SegmentType.TOP_ROOM_OUT_RIGHT_BORDER_BOTTOM
      }

    override fun convert(segment: Segment): Segment {
        val targetY = if (segment.y == 0) 1 else levelConfig.levelHeight - 2
        return convertSegment(segment, segment.x, targetY)
    }

}