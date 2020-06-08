package kifio.leningrib

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import kifio.leningrib.model.ResourcesManager
import java.util.concurrent.ThreadLocalRandom

class LUTController {

    companion object {
        private const val INTENSITY = "intensity"
        private const val VERTEX_PATH = "shaders/lookup_vertex.glsl"
        private const val FRAGMENT_PATH = "shaders/lookup_fragment.glsl"
    }

    private var intensity = 0f
    var lutTexture: Texture? = null

    private var index = -1
    private var mushroomsCount = 0
    private var accumulatedTime: Float = 0f
    private var effectTime = 5

    var shader: ShaderProgram? = null

    fun setup() {
        val vertexShader = Gdx.files.internal(VERTEX_PATH).readString()
        val fragmentShader = Gdx.files.internal(FRAGMENT_PATH).readString()
        ShaderProgram.pedantic = false
        shader = ShaderProgram(vertexShader, fragmentShader)
        shader = ShaderProgram(vertexShader, fragmentShader)
    }

    fun updateLut(delta: Float, mushroomsCount: Int): Int {
        index = -1
        if (mushroomsCount > 0) {
            val foo: Boolean = mushroomsCount - this.mushroomsCount == 1

            if (foo) {
                this.mushroomsCount = mushroomsCount
                index = setNextLut()
                accumulatedTime = (effectTime / 2f) - delta
                intensity = 0.5f
            }

            accumulatedTime += delta

            if (lutTexture == null || intensity >= 1f) {
                return index
            }

            intensity = mushroomsCount / 50f
            return index
        } else {
            return index
        }
    }

    private fun setNextLut(): Int {
        lutTexture = ResourcesManager.getLut(ResourcesManager.getLutName(30))
        return index
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
        index = -1
    }
}