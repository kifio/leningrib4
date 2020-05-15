package kifio.leningrib.model.actors

class ForesterStateMachine {

    companion object {
        const val MAXIMUM_STOP_TIME = 3f
    }

    enum class MovingState {
        PATROL, PURSUE, STOP, SCARED, DISABLED, RUN_TO_BOTTLE, DRINKING, DRUNK
    }

    fun updateState(currentState: MovingState,
                    isPlayerNoticed: Boolean,
                    isPlayerPursued: Boolean,
                    isPlayerStrong: Boolean,
                    isPlayerInvisible: Boolean,
                    isBottleNoticed: Boolean,
                    isDrinking: Boolean,
                    stopTime: Float): MovingState {

        when (currentState) {

            MovingState.PATROL -> {
                return if (isBottleNoticed) {
                      MovingState.RUN_TO_BOTTLE
                } else if (isPlayerNoticed) {
                    if (isPlayerStrong) {
                        MovingState.SCARED
                    } else {
                        MovingState.PURSUE
                    }
                } else {
                    MovingState.PATROL
                }
            }

            MovingState.PURSUE -> {
                return if (isBottleNoticed) {
                    MovingState.RUN_TO_BOTTLE
                } else if (isPlayerStrong) {
                    MovingState.SCARED
                } else if (isPlayerInvisible || !isPlayerPursued) {
                    MovingState.PATROL
                } else {
                    MovingState.PURSUE
                }
            }

            MovingState.SCARED -> {
                return if (isPlayerPursued) {
                    if (isPlayerStrong) {
                        MovingState.SCARED
                    } else if (isPlayerInvisible) {
                        MovingState.STOP
                    } else {
                        MovingState.PURSUE
                    }
                } else if (isBottleNoticed) {
                    MovingState.RUN_TO_BOTTLE
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

            MovingState.RUN_TO_BOTTLE -> {
                return if (isDrinking) {
                    MovingState.DRINKING
                } else {
                    MovingState.RUN_TO_BOTTLE
                }
            }

            MovingState.DRINKING -> {
                return if (isDrinking) {
                    MovingState.DRINKING
                } else {
                    MovingState.DRUNK
                }
            }

            MovingState.DRUNK -> {
                return if (stopTime > MAXIMUM_STOP_TIME || isPlayerNoticed) {
                    MovingState.PATROL
                } else {
                    MovingState.DRUNK
                }
            }
        }
    }
}