package mappers

import generator.Generator.Side
import generator.SegmentType
import model.Segment
import java.lang.IllegalArgumentException

internal abstract class SideMapper() {

    protected val segmentsMap = mutableMapOf<SegmentType, SegmentType>()

    internal fun mapSideBySide(neighborSide: List<Segment>, side: Side) =
        neighborSide.map { convert(it) }

    abstract fun convert(segment: Segment): Segment

    protected fun convertSegment(segment: Segment, targetX: Int, targetY: Int): Segment {
        return Segment(
            targetX, targetY, segmentsMap.get(segment.getValue())
                ?: throw IllegalArgumentException("Can't map segment: ${segment.getValue()}")
        )
    }
}