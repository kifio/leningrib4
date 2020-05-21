package kifio.leningrib.levels.helpers

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import generator.Config
import generator.SegmentType
import kifio.leningrib.LGCGame
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.TreePart
import kifio.leningrib.screens.GameScreen
import model.LevelMap

class TreesManager {

    private val outerTrees = Array<TreePart>()
    private val obstacleTrees = Array<TreePart>()
    private val innerBorderTrees = Array<TreePart>()

    fun updateTrees(levelMap: LevelMap, config: Config, index: Int) {

        for (s in levelMap.getSegments()) {

            val x = s.x * GameScreen.tileSize

            val y = if (index == 0)
                (s.y + config.levelHeight * index) * GameScreen.tileSize
            else
                (s.y - index + config.levelHeight * index) * GameScreen.tileSize
            val tree = getActorFromCell(s.getValue(), x, y)

            if (tree != null) {
                if (s.y == 0 && index > 0) continue;

                obstacleTrees.add(tree)

                if (s.x == 0 || s.y == 0 || s.x == LGCGame.LEVEL_WIDTH - 1/* || s.y == LGCGame.getLevelHeight() - 1*/) {
                    outerTrees.add(tree)
                } else {
                    innerBorderTrees.add(tree)
                }
            }
        }
    }

    fun getObstacleTrees() = obstacleTrees

    fun getOuterBordersTrees() = outerTrees

    fun getInnerBordersTrees() = innerBorderTrees

    fun getFreeNeighbour(x: Float, y: Float): Vector2? {
        return if (obstacleTrees.find {
                    it.position.epsilonEquals(x, y)
                } != null) {
            null
        } else {
            Vector2(x, y)
        }
    }

    companion object {

        private fun getActorFromCell(segmentType: SegmentType, x: Int, y: Int): TreePart? {
            val region: TextureRegion = ResourcesManager.getRegion(segmentType.name) ?: return null
            return TreePart(region, 1f, x.toFloat(), y.toFloat(), GameScreen.tileSize)
        }
    }
}