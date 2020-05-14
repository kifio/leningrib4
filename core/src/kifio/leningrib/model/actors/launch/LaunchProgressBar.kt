package kifio.leningrib.model.actors.launch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.WideAssetActor

class LaunchProgressBar(camera: OrthographicCamera): WideAssetActor(
        ResourcesManager.getRegion(ResourcesManager.LAUNCH_PROGRESS_BACKGROUND), camera
) {

    private val progressForegroundTexture = ResourcesManager.getTexture(ResourcesManager.LAUNCH_PROGRESS_FOREGROUND)
    private var progressForegroundRegion: TextureRegion? = null
    private var progress: Float = 0F

    init {
        this.y = (Gdx.graphics.height * 0.35f)
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        this.progressForegroundRegion = TextureRegion(progressForegroundTexture,
                (this.progressForegroundTexture.width * this.progress).toInt(),
                this.progressForegroundTexture.height)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        this.progressForegroundRegion?.let {
            batch.draw(it, x, y, width * progress, height)
        }
    }
}