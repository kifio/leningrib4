package kifio.leningrib.model.items

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Array
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.screens.GameScreen

enum class BottleState {
    FULL, OPENED, HALF, NA_DNE, EMPTY
}

class Bottle(x: Float, y: Float) : Actor() {

    private var elapsedTime = 0f
    var state = BottleState.FULL

    private val bottleRegionSize = 56

    val regionSize = GameScreen.tileSize.toFloat()
    private val animations = HashMap<BottleState, Animation<TextureRegion>>()

    init {
        setX(x)
        setY(y)

        val bottleTexture = ResourcesManager.getTexture("bottle")
        val bottleSmolTexture = ResourcesManager.getTexture("bottle_smol")
        val regions = Array<TextureRegion>()
        val values = BottleState.values()

        for (i in values.indices) {
            regions.add(TextureRegion(bottleTexture, 0, bottleRegionSize * i, bottleRegionSize, bottleRegionSize))
            regions.add(TextureRegion(bottleSmolTexture, 0, bottleRegionSize * i, bottleRegionSize, bottleRegionSize))
            animations[values[i]] = Animation<TextureRegion>(0.2f, regions).apply {
                playMode = Animation.PlayMode.LOOP
            }
            regions.clear()
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        batch?.draw(getTextureRegion(Gdx.graphics.deltaTime), x, y, regionSize, regionSize)
    }

    private fun getTextureRegion(deltaTime: Float): TextureRegion? {
        elapsedTime += deltaTime
        return animations[state]?.getKeyFrame(elapsedTime)
    }
}