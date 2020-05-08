package kifio.leningrib.model.items

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Array
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.MovableActor
import kifio.leningrib.model.actors.game.Forester
import kifio.leningrib.screens.GameScreen

enum class BottleState {
    CLOSED, OPENED, ALMOST_FULL,  HALF, NA_DNE, EMPTY
}

private const val DRINKING_TIME = 5F + 1F

class Bottle(x: Float, y: Float) : Actor() {

    var state = BottleState.CLOSED

    private var elapsedTime = 0F
    private var animTime = 0F
    private val drinkers = mutableSetOf<MovableActor>()

    private val regionSize = GameScreen.tileSize.toFloat()
    private val animations = HashMap<BottleState, Animation<TextureRegion>>()

    init {
        setX(x)
        setY(y + GameScreen.tileSize / 2f)

        val bottleTexture = ResourcesManager.getTexture("bottle")
        val bottleSmolTexture = ResourcesManager.getTexture("bottle_smol")
        val regions = Array<TextureRegion>()
        val values = BottleState.values()

        for (i in values.indices) {
            val bottleRegionSize = bottleTexture.width
            regions.add(TextureRegion(bottleTexture, 0, bottleRegionSize * i, bottleRegionSize, bottleRegionSize))
            regions.add(TextureRegion(bottleSmolTexture, 0, bottleRegionSize * i, bottleRegionSize, bottleRegionSize))
            animations[values[i]] = Animation(0.4f, regions).apply {
                playMode = Animation.PlayMode.LOOP
            }
            regions.clear()
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        Gdx.app.log("kifio", "elapsedTime: $elapsedTime")
        if (elapsedTime >= (DRINKING_TIME - 1F)) {
            val animTime = DRINKING_TIME - elapsedTime
            batch.color.apply { setColor(r, g, b, 1f - (animTime)) }
        }
        batch.draw(getTextureRegion(), x, y, GameScreen.tileSize.toFloat(), GameScreen.tileSize.toFloat())
    }

    private fun getTextureRegion(): TextureRegion? {
        return animations[state]?.getKeyFrame(animTime)
    }

    override fun act(delta: Float) {
        super.act(delta)
        animTime += delta
        if (drinkers.isNotEmpty()) {

            state = when {
                elapsedTime <= 1f -> {
                    BottleState.OPENED
                }
                elapsedTime <= 0.25F * (DRINKING_TIME - 1F) -> {
                    BottleState.ALMOST_FULL
                }
                elapsedTime <= 0.5F * (DRINKING_TIME - 1F) -> {
                    BottleState.HALF
                }
                elapsedTime <= 0.75F * (DRINKING_TIME - 1F) -> {
                    BottleState.NA_DNE
                }
                elapsedTime <= (DRINKING_TIME - 1F) -> {
                    BottleState.EMPTY
                }
                else -> {
                    BottleState.EMPTY
                }
            }

            elapsedTime += (delta)
        }
        if (elapsedTime >= DRINKING_TIME) {
            drinkers.clear()
        }
    }

    fun isEmpty(): Boolean {
        return state  == BottleState.EMPTY;
    }

    fun isRemovable() = elapsedTime >= DRINKING_TIME

    fun addDrinker(f: MovableActor) = drinkers.add(f)

    fun hasDrinker(f: MovableActor) = drinkers.contains(f)
}