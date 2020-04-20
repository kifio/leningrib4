package kifio.leningrib.model.actors.launch

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.WideAssetActor

class LaunchProgressBar: WideAssetActor(
        ResourcesManager.getRegion(ResourcesManager.LAUNCH_PROGRESS_BACKGROUND)
) {

    private val progressForegroundTexture = ResourcesManager.getTexture(ResourcesManager.LAUNCH_PROGRESS_FOREGROUND)
    private var progressForegroundRegion: TextureRegion? = null
    private var progress: Float = 0F

    fun setProgress(progress: Int) {
        this.progress = progress / 100F
        this.progressForegroundRegion = TextureRegion(progressForegroundTexture,
                (this.progressForegroundTexture.width * this.progress).toInt(),
                this.progressForegroundTexture.height)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        this.progressForegroundRegion?.let {
            batch.draw(it, x, y, width * progress, height)
        }
    }
}