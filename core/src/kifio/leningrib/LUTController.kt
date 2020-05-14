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

//    private var active = true
    private var intensity = 0f
    private var increaseIntensity = true
    private var accumulatedTime: Float = 0f

    var shader: ShaderProgram? = null
    var lutTexture: Texture? = null

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

    fun updateLut(delta: Float) {
//         if (!active) return
        accumulatedTime += delta

        if (accumulatedTime > 5) {
            accumulatedTime = 0f
            increaseIntensity = !increaseIntensity
            val index = ThreadLocalRandom.current().nextInt(0, 5)
            lutTexture = ResourcesManager.getTexture("lut_$index")
        }

        if (lutTexture != null) {
            intensity = (accumulatedTime % 5) / 5
            if (!increaseIntensity) {
                intensity = 1f - intensity
            }
        }

        Gdx.app.log("kifio_shader", "intensity: $intensity")
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
//        active = false
    }
}