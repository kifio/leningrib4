package kifio.leningrib.model.actors.launch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.StaticActor
import kifio.leningrib.screens.GameScreen

class LaunchProgressBar() : StaticActor(
        ResourcesManager.getRegion(ResourcesManager.LAUNCH_PROGRESS_BACKGROUND)
) {

    private val progressForegroundTexture = ResourcesManager.getTexture(ResourcesManager.LAUNCH_PROGRESS_FOREGROUND)
    private var progressForegroundRegion: TextureRegion? = null
    private var progress: Float = 0F

    init {

        val logoOffset = 40 * Gdx.graphics.density
        this.width = Gdx.graphics.width - (2 * logoOffset)
        this.height = (this.width / region.regionWidth) * region.regionHeight

        this.x = logoOffset
        this.y = (Gdx.graphics.height * 0.35f)
    }

    fun setProgress(progress: Int) {
        this.progress = progress / 100F
        this.progressForegroundRegion = TextureRegion(progressForegroundTexture,
                (this.progressForegroundTexture.width * this.progress).toInt(),
                this.progressForegroundTexture.height)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        this.progressForegroundRegion?.let {
            batch.draw(it, x, y, width * progress, height * progress)
        }
    }
}