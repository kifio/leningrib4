package kifio.leningrib.model.actors

import com.badlogic.gdx.math.Rectangle
import kifio.leningrib.model.items.Bottle
import kifio.leningrib.screens.GameScreen

class ForesterStateMachine {

    companion object {
        const val MAXIMUM_STOP_TIME = 3f
    }

    enum class MovingState {
        PATROL, PURSUE, STOP, SCARED, DISABLED
    }

    fun updateState(currentState: MovingState,
                    isPlayerNoticed: Boolean,
                    isPlayerPursued: Boolean,
                    isStrong: Boolean,
                    isInvisible: Boolean,
                    stopTime: Float,
                    bottle: Bottle? = null): MovingState {

        when (currentState) {

            MovingState.PATROL -> {
                return if (isPlayerNoticed) {
                    if (isStrong) {
                        MovingState.SCARED
                    } else {
                        MovingState.PURSUE
                    }
                } else {
                    MovingState.PATROL
                }
            }

            MovingState.PURSUE -> {
                return if (isStrong) {
                    MovingState.SCARED
                } else if (isInvisible || !isPlayerPursued) {
                    MovingState.STOP
                } else {
                    MovingState.PURSUE
                }
            }

            MovingState.SCARED -> {
                return if (isPlayerPursued) {
                    if (isStrong) {
                        MovingState.SCARED
                    } else if (isInvisible) {
                        MovingState.STOP
                    } else {
                        MovingState.PURSUE
                    }
                } else {
                    MovingState.PURSUE
                }
            }

            MovingState.STOP -> {
                return if (stopTime > MAXIMUM_STOP_TIME || isPlayerNoticed) {
                    MovingState.PATROL
                } else {
                    MovingState.STOP
                }
            }

            MovingState.DISABLED -> {
                return if (stopTime > MAXIMUM_STOP_TIME) {
                    MovingState.PATROL
                } else {
                    MovingState.DISABLED
                }
            }
        }
    }
}