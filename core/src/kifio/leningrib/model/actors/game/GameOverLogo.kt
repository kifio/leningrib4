package kifio.leningrib.model.actors.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import kifio.leningrib.Utils
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.StaticActor
import kifio.leningrib.model.actors.WideAssetActor
import kifio.leningrib.screens.GameScreen

class GameOverLogo(private val camera: Camera) : WideAssetActor(ResourcesManager.getRegion(ResourcesManager.GAME_OVER)) {

    init {
        region?.let { region ->
            val logoOffset = 40 * Gdx.graphics.density
            this.width = Gdx.graphics.width - (2 * logoOffset)
            this.height = (this.width / region.regionWidth) * region.regionHeight
            this.x = logoOffset
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        this.y = (camera.position.y - Gdx.graphics.height / 2f) + (Gdx.graphics.height * 0.55f)
    }
}