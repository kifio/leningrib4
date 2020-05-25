package kifio.leningrib

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import generator.Config
import kifio.leningrib.levels.CommonLevel
import kifio.leningrib.levels.FirstLevel
import kifio.leningrib.levels.Level
import kifio.leningrib.model.ResourcesManager
import kifio.leningrib.model.actors.game.Player
import kifio.leningrib.platform.StoreInterface
import kifio.leningrib.screens.BaseScreen
import kifio.leningrib.screens.GameScreen
import kifio.leningrib.screens.LaunchScreen
import model.LevelMap
import model.WorldMap
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom

class LGCGame(val store: StoreInterface) : Game() {

    companion object {

        const val LEVEL_WIDTH = 10
        const val ANIMATION_DURATION = 0.3f
        const val ANIMATION_DURATION_LONG = 0.6f
        const val PREFERENCES_NAME = "kifio.leningrib"

        const val FIRST_LEVEL_PASSED = "FIRST_LEVEL_PASSED"

        private const val PLAYER_BOTTLES_COUNT = "PLAYER_BOTTLES_COUNT"
        private const val PLAYER_GUMS_COUNT = "PLAYER_GUMS_COUNT"

        const val MUSIC = "music"
        const val SOUNDS = "sounds"
        private const val WAS_LAUNCHED = "is_first_launch"

        private var firstLevelPassed = false
        private var shouldShowBottleDialog = false

        private var prefs: Preferences? = null
        private val currentDate = SimpleDateFormat("yyyy-MM-dd ", Locale.ENGLISH).format(Date())

        fun isFirstLevelPassed() = firstLevelPassed

        fun setFirstLevelPassed(passed: Boolean) {
            firstLevelPassed = passed
            prefs?.putBoolean(FIRST_LEVEL_PASSED, passed)
            prefs?.flush()
        }

        fun getPreferences() = prefs

        fun getLevelAndPlayer(worldMap: WorldMap): Pair<Player, Level> {
            val levelMap: LevelMap
            val level: Level
            val player: Player
            if (isFirstLevelPassed()) {
                val config = Config(LEVEL_WIDTH, CommonLevel.LEVEL_HEIGHT)
                levelMap = worldMap.addLevel(0, 0, null, config)
                val room = levelMap.rooms[0]
                val x = 3f
                val y = ThreadLocalRandom.current().nextInt(room.y + 1, room.y + room.height - 3).toFloat()
                player = Player(x * GameScreen.tileSize, y * GameScreen.tileSize)
                level = CommonLevel(player, levelMap)
            } else {
                val firstRoomHeight = (Gdx.graphics.height / GameScreen.tileSize) - 2
                val config = Config(LEVEL_WIDTH, firstRoomHeight + 20)
                levelMap = worldMap.addFirstLevel(config, firstRoomHeight)
                player = FirstLevel.getPlayer()
                level = FirstLevel(player, levelMap)
            }
            return Pair(player, level)
        }

        fun shouldShowBottleDialog(): Boolean {
            return shouldShowBottleDialog
        }

        fun keepCurrentDate() {
            shouldShowBottleDialog = false
            prefs?.putBoolean(currentDate, true)
            prefs?.flush()
        }

        // TODO: Migrate to in app purchases
        fun getBottlesCount() = getCount(PLAYER_BOTTLES_COUNT)
        fun getGumsCount() = getCount(PLAYER_GUMS_COUNT)
        fun setBottlesCount(count: Int) = setCount(PLAYER_BOTTLES_COUNT, count)
        fun setGumsCount(count: Int) = setCount(PLAYER_GUMS_COUNT, count)

        private fun setCount(item: String, count: Int) {
            prefs?.putInteger(item, count)
            prefs?.flush()
        }

        private fun getCount(item: String) = prefs?.getInteger(item) ?: 0

        fun savePurchasedSku(skuList: List<String>) {
            for (sku in skuList) {
                prefs?.putBoolean(sku, true)
            }
            prefs?.flush()
        }

        fun getConsumedSkuList(): List<String> {
            val skuList = mutableListOf<String>()
            for (sku in StoreInterface.SKU_LIST) {
                prefs?.contains(sku)
                skuList.add(sku)
            }
            return skuList
        }
    }

    val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var music: Music? = null
    private val onCompleteListener: Music.OnCompletionListener = Music.OnCompletionListener {
        if (prefs?.getBoolean(MUSIC) == true) {
            setMusic()
        }
    }

    override fun create() {
        prefs = Gdx.app.getPreferences(PREFERENCES_NAME)
        firstLevelPassed = prefs?.getBoolean(FIRST_LEVEL_PASSED) ?: false

        shouldShowBottleDialog = prefs?.contains(currentDate) != true

        if (prefs?.getBoolean(WAS_LAUNCHED) != true) {
            prefs?.putBoolean(MUSIC, true)
            prefs?.putBoolean(SOUNDS, true)
            prefs?.putBoolean(WAS_LAUNCHED, true)
            prefs?.flush()
        }

        store.setup({
            store.loadPurchases {
                items -> savePurchasedSku(items)
            }
        }, { sku -> savePurchasedSku(listOf(sku)) })

        ResourcesManager.loadSplash()
        GameScreen.tileSize = Gdx.graphics.width / LEVEL_WIDTH
        showLaunchScreen()
    }

    override fun dispose() {
        getScreen()?.dispose()
    }

    private fun showLaunchScreen() {
        ResourcesManager.loadSplash()
        setScreen(LaunchScreen(this))
    }

    fun showGameScreen(gameScreen: GameScreen?) {
        if (music == null && prefs?.getBoolean(MUSIC) == true) {
            setMusic()
        }

        if (gameScreen == null) return
        gameScreen.activate()
        showScreen(gameScreen)
    }

    private fun setMusic() {
        music?.setOnCompletionListener(null)
        music = ResourcesManager.getNextMusic()
        music?.setOnCompletionListener(onCompleteListener)
        music?.play()
    }

    fun resetMusic() {
        if (prefs?.getBoolean(MUSIC) == true) {
            setMusic()
        } else {
            music?.setOnCompletionListener(null)
            music?.stop()
            music?.dispose()
        }
    }

    private fun showScreen(screen: Screen) {
        val currentScreen = getScreen()
        createScreenOutAction(currentScreen, Runnable {
            createScreenInAction(screen)
        })
    }

    private fun createScreenOutAction(screen: Screen, runnable: Runnable) {
        val actor: Actor = (screen as BaseScreen).getTransitionActor()
        actor.color.a = 1f
        val sequenceAction = SequenceAction()
        sequenceAction.addAction(Actions.parallel(Actions.alpha(0f, ANIMATION_DURATION_LONG), Actions.scaleTo(1.5f, 1.5f, ANIMATION_DURATION_LONG, Interpolation.exp5)))
        sequenceAction.addAction(Actions.run(runnable))
        actor.addAction(sequenceAction)
    }

    private fun createScreenInAction(screen: Screen) {
        val current = getScreen()
        setScreen(screen)
        val actor: Actor = (screen as BaseScreen).stage.root
        actor.setOrigin(Gdx.graphics.width / 2f, Gdx.graphics.height / 2f)
        actor.color.a = 0f
        val sequenceAction = SequenceAction()
        sequenceAction.addAction(Actions.scaleTo(1.5f, 1.5f, 0f))
        sequenceAction.addAction(Actions.parallel(Actions.alpha(1f, ANIMATION_DURATION_LONG), Actions.scaleTo(1.0f, 1.0f, ANIMATION_DURATION_LONG, Interpolation.exp5)))
        sequenceAction.addAction(Actions.run { current.dispose() })
        actor.addAction(sequenceAction)
    }
}