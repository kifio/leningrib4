package kifio.leningrib.model.actors.launch

import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.StaticActor
import kifio.leningrib.screens.GameScreen

class LaunchScreenTree(x: Float, y: Float) : StaticActor(
        ResourcesManager.getRegion(ResourcesManager.LAUNCH_TREES)
) {

    init {
        this.x = x
        this.y = y
        this.width = GameScreen.tileSize * 2F
        this.height = GameScreen.tileSize * 2F
    }
}