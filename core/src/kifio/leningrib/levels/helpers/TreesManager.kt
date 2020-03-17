package kifio.leningrib.levels.helpers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Array
import com.sun.org.apache.xpath.internal.operations.Bool
import generator.Config
import generator.SegmentType
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.TreePart
import kifio.leningrib.screens.GameScreen
import model.LevelMap
import java.util.*

class TreesManager {
    private val trees = Array<Actor>()
    private val outerTrees = Array<Actor>()
    private val obstacleTrees = Array<Actor>()

    fun buildTrees(levelMap: LevelMap, constantsConfig: Config) {
        for (s in levelMap.getSegments()) {
            val tree = getActorFromCell(s.getValue(), s.x * GameScreen.tileSize, s.y * GameScreen.tileSize)
            if (tree != null) {

                if (!isNotObstacle(s.getValue())) {
                    obstacleTrees.add(tree)
                }

                trees.add(tree)
                if (s.x == 0 || s.y == 0 || s.x == constantsConfig.levelWidth - 1 || s.y == constantsConfig.levelHeight - 1) {
                    outerTrees.add(tree)
                }
            }
        }
    }

    fun getTrees() = trees

    fun getObstacleTrees() = obstacleTrees

    val outerBordersTrees: Array<out Actor>
        get() = outerTrees

    companion object {

        private fun getActorFromCell(segmentType: SegmentType, x: Int, y: Int): Actor? {
            val region: TextureRegion = ResourcesManager.getRegion(segmentType.name) ?: return null
            return TreePart(region, x.toFloat(), y.toFloat(), GameScreen.tileSize, GameScreen.tileSize)
        }

        private fun isNotObstacle(value: SegmentType): Boolean {
            return value == SegmentType.BOTTOM_COMMON_LEFT_TOP
                    || value == SegmentType.BOTTOM_COMMON_RIGHT_TOP
                    || value == SegmentType.BOTTOM_LEFT_CORNER_OUT_BORDER_TOP
                    || value == SegmentType.BOTTOM_RIGHT_CORNER_OUT_BORDER_TOP
                    || value == SegmentType.TOP_COMMON_LEFT_BOTTOM
                    || value == SegmentType.TOP_COMMON_RIGHT_BOTTOM
        }
    }
}