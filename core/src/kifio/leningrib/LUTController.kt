package kifio.leningrib

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import kifio.leningrib.model.ResourcesManager
import java.util.Collections.min
import java.util.concurrent.ThreadLocalRandom

class LUTController {

    companion object {
        private const val INTENSITY = "intensity"
        private const val VERTEX_PATH = "shaders/lookup_vertex.glsl"
        private const val FRAGMENT_PATH = "shaders/lookup_fragment.glsl"
    }

    private var intensity = 0f
    var lutTexture: Texture? = null

    private var active = false
    private var mushroomsCount = 0
    private var accumulatedTime: Float = 0f
    private var effectTime = 5

    var shader: ShaderProgram? = null

    fun setup() {
        val vertexShader = Gdx.files.internal(VERTEX_PATH).readString()
        val fragmentShader = Gdx.files.internal(FRAGMENT_PATH).readString()
        ShaderProgram.pedantic = false
        shader = ShaderProgram(vertexShader, fragmentShader)
        if (shader?.isCompiled != true) {
            Gdx.app.log("kifio_shader", shader?.log)
        }
        shader = ShaderProgram(vertexShader, fragmentShader)
    }

    fun updateLut(delta: Float, mushroomsCount: Int) {

        if (!active) {
            active = true
            val index = ThreadLocalRandom.current().nextInt(0, ResourcesManager.LUTS_COUNT)
            lutTexture = ResourcesManager.getTexture(ResourcesManager.getLutName(index))
        } else {

            if (mushroomsCount - this.mushroomsCount == 3) {
                this.mushroomsCount = mushroomsCount
                val index = ThreadLocalRandom.current().nextInt(0, ResourcesManager.LUTS_COUNT)
                lutTexture = ResourcesManager.getTexture(ResourcesManager.getLutName(index))
                accumulatedTime = (effectTime / 2f) - delta
                intensity = 0.5f
            }

            accumulatedTime += delta

            if (lutTexture == null || intensity >= 1f) {
                return
            }

            intensity = accumulatedTime / effectTime
        }
    }

    fun onDraw() {
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1)
        lutTexture?.bind()
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        shader?.setUniformf(INTENSITY, intensity)
        shader?.setUniformi("u_texture2", 1)
    }

    fun stop() {
        lutTexture = null
    }
}