package model

/**
 * x, y - coordinates in level coordinate system, not in screen coordinates system.
 * opened - toggle, for setting availability of exit. it should be manipulated by client code, not from library.
 */
data class Exit(val x: Int, val y: Int, var opened: Boolean = false)