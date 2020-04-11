package kifio.leningrib.model.items

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Array
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.Forester
import kifio.leningrib.screens.GameScreen
import kotlin.math.E

enum class BottleState {
    CLOSED, OPENED, FULL, ALMOST_FULL,  HALF, NA_DNE, EMPTY
}

class Bottle(x: Float, y: Float) : Actor() {

    var state = BottleState.CLOSED

    private var elapsedTime = 0F
    private val drinkers = mutableSetOf<Forester>()

    private val bottleRegionSize = 56

    private val regionSize = GameScreen.tileSize.toFloat()
    private val animations = HashMap<BottleState, Animation<TextureRegion>>()

    init {
        setX(x)
        setY(y + GameScreen.tileSize / 2f)

        // FIXME add states to textures
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
        return animations[state]?.getKeyFrame(elapsedTime)
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (drinkers.isNotEmpty()) {

            state = when {
                elapsedTime <= 1f -> {
                    BottleState.OPENED
                }
                elapsedTime <= 2.5f -> {
                    BottleState.FULL
                }
                elapsedTime <= 5f -> {
                    BottleState.ALMOST_FULL
                }
                elapsedTime <= 7.5f -> {
                    BottleState.HALF
                }
                elapsedTime <= 10f -> {
                    BottleState.NA_DNE
                }
                else -> {
                    BottleState.EMPTY
                }
            }

            elapsedTime += (delta * 4)
        }
        if (state == BottleState.EMPTY) {
            drinkers.clear()
        }
    }

    fun isEmpty(): Boolean {
        return state  == BottleState.EMPTY;
    }

    fun addDrinker(f: Forester) = drinkers.add(f)

    fun hasDrinker(f: Forester) = drinkers.contains(f)
}