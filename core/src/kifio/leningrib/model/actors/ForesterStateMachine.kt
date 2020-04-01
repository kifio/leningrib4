package kifio.leningrib.model.actors

import com.badlogic.gdx.math.Rectangle
import kifio.leningrib.model.items.Bottle
import kifio.leningrib.screens.GameScreen

class ForesterStateMachine {

    companion object {
        private const val NOTICE_AREA_SIDE = 5
        private const val PURSUE_AREA_SIDE = 9

        const val MAXIMUM_STOP_TIME = 3f

    }

    enum class MovingState {
        PATROL, PURSUE, STOP, SCARED, DISABLED
    }

    private val noticeArea = Rectangle(0F, 0F,
            (NOTICE_AREA_SIDE * GameScreen.tileSize).toFloat(),
            (NOTICE_AREA_SIDE * GameScreen.tileSize).toFloat())

    private val pursueArea = Rectangle(0F, 0F,
            (PURSUE_AREA_SIDE * GameScreen.tileSize).toFloat(),
            (PURSUE_AREA_SIDE * GameScreen.tileSize).toFloat())

    fun updateArea(x: Int, y: Int) {
        noticeArea.setX((x - 2 * GameScreen.tileSize).toFloat())
        noticeArea.setY((y - 2 * GameScreen.tileSize).toFloat())

        pursueArea.setX((x - 4 * GameScreen.tileSize).toFloat())
        pursueArea.setY((y - 4 * GameScreen.tileSize).toFloat())
    }

    fun updateState(currentState: MovingState,
                    px: Float,
                    py: Float,
                    isStrong: Boolean,
                    isInvisible: Boolean,
                    stopTime: Float,
                    bottle: Bottle? = null): MovingState {

        when (currentState) {

            MovingState.PATROL -> {
                if (noticeArea.contains(px, py)) {
                    if (isStrong) {
                        return MovingState.SCARED
                    } else {
                        return MovingState.PURSUE
                    }
                } else {
                    return MovingState.PATROL
                }
            }

            MovingState.PURSUE -> {
                if (isStrong) {
                    return MovingState.SCARED
                } else if (isInvisible || !pursueArea.contains(px, py)) {
                    return MovingState.STOP
                } else {
                    return MovingState.PURSUE
                }
            }

            MovingState.SCARED -> {
                if (noticeArea.contains(px, py)) {
                    if (isStrong) {
                        return MovingState.SCARED
                    } else if (isInvisible) {
                        return MovingState.STOP
                    } else {
                        return MovingState.PURSUE
                    }
                } else {
                    return MovingState.PURSUE
                }
            }

            MovingState.STOP -> {
                if (stopTime > MAXIMUM_STOP_TIME || noticeArea.contains(px, py)) {
                    return MovingState.PATROL
                } else {
                    return MovingState.STOP
                }
            }

            MovingState.DISABLED -> {
                if (stopTime > MAXIMUM_STOP_TIME) {
                    return MovingState.PATROL
                } else {
                    return MovingState.DISABLED
                }
            }
        }
    }

    fun getNoticeArea() = noticeArea
    fun getPursueArea() = pursueArea
}