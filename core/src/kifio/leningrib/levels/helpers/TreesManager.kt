package kifio.leningrib.levels.helpers

import com.badlogic.gdx.graphics.g2d.TextureRegion
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
    private val bottomBorderNonObstaclesTrees = Array<TreePart>()
    private val topBorderNonObstaclesTrees = Array<TreePart>()
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
                val treeType = s.getValue()

                when {
                    isTopLineIgnoredTree(treeType) -> {
                        topBorderNonObstaclesTrees.add(tree)
                    }
                    isBottomLineIgnoredTree(treeType) -> {
                        bottomBorderNonObstaclesTrees.add(tree)
                    }
                    else -> {
                        obstacleTrees.add(tree)
                    }
                }

                if (s.x == 0 || s.y == 0 || s.x == LGCGame.LEVEL_WIDTH - 1/* || s.y == LGCGame.getLevelHeight() - 1*/) {
                    outerTrees.add(tree)
                } else {
                    innerBorderTrees.add(tree)
                }
            }
        }
    }

    fun getObstacleTrees() = obstacleTrees

    fun getBottomBorderNonObstaclesTrees() = bottomBorderNonObstaclesTrees

    fun getTopBorderNonObstaclesTrees() = topBorderNonObstaclesTrees

    fun getOuterBordersTrees() = outerTrees

    fun getInnerBordersTrees() = innerBorderTrees

    companion object {

        private fun getActorFromCell(segmentType: SegmentType, x: Int, y: Int): TreePart? {
            val region: TextureRegion = ResourcesManager.getRegion(segmentType.name) ?: return null
            return TreePart(region, x.toFloat(), y.toFloat(), GameScreen.tileSize)
        }

        private fun isBottomLineIgnoredTree(value: SegmentType): Boolean {
            return value == SegmentType.BOTTOM_COMMON_LEFT_TOP
                    || value == SegmentType.BOTTOM_COMMON_RIGHT_TOP
                    || value == SegmentType.BOTTOM_LEFT_CORNER_COMMON_TOP
                    || value == SegmentType.BOTTOM_RIGHT_CORNER_COMMON_TOP
        }

        private fun isTopLineIgnoredTree(value: SegmentType): Boolean {
            return value == SegmentType.TOP_COMMON_LEFT_BOTTOM
                    || value == SegmentType.TOP_COMMON_RIGHT_BOTTOM
        }
    }
}