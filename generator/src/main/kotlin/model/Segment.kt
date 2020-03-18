package model

import generator.SegmentType

data class Segment(val x: Int, val y: Int, private var value: SegmentType) {

    fun getValue() = value

    internal fun setValue(value: SegmentType) {
        this.value = value
    }
}