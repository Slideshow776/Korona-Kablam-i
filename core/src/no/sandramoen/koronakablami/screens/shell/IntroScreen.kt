package no.sandramoen.koronakablami.screens.shell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import no.sandramoen.koronakablami.screens.gameplay.LevelScreen
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame
import no.sandramoen.koronakablami.utils.BaseScreen
import no.sandramoen.koronakablami.utils.GameUtils

class IntroScreen : BaseScreen() {
    private lateinit var touchToSkipLabel: Label
    private lateinit var bottomLabel: Label
    private lateinit var topSceneTable: Table
    private var screenWidth = 0f
    private var topSceneMaxHeight = 0f
    private var time = 0f
    private lateinit var blackOverlay: BaseActor

    override fun initialize() {
        screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()

        topSceneTable = Table()
        topSceneMaxHeight = (screenHeight * .96f) * 5 / 6
        createTopActor("introBackground0", 60f).addAction(Actions.color(Color(.11f, .541f, .0f, 1f), 20f))
        createTopActor("introCity3", 150f)
        createTopActor("introCity2", 120f)
        createTopActor("introCity1", 90f)
        createTopActor("introCity0", 60f)

        val bottomSceneTable = Table()
        val bottomSceneMaxHeight = (screenHeight * .96f) * 1 / 6
        bottomLabel = Label("", BaseGame.labelStyle)
        bottomLabel.setFontScale(.5f)
        bottomLabel.color = Color.ORANGE
        bottomLabel.setWrap(true)
        bottomLabel.width = screenWidth * .9f
        bottomLabel.setAlignment(Align.center)
        bottomSceneTable.add(bottomLabel).width(screenWidth * .9f)
        /*bottomSceneTable.debug = true*/
        bottomScene()

        touchToSkipLabel = Label("Touch to skip!", BaseGame.labelStyle)
        touchToSkipLabel.setFontScale(.25f)
        GameUtils.pulseLabel(touchToSkipLabel)

        val sceneTable = Table()
        sceneTable.add(topSceneTable).width(screenWidth).height(topSceneMaxHeight).row()
        sceneTable.add(bottomSceneTable).width(screenWidth).height(bottomSceneMaxHeight)
        /*sceneTable.debug = true*/

        val mainTable = Table()
        mainTable.setFillParent(true)
        mainTable.add(sceneTable).fill().expand().row()
        mainTable.add(touchToSkipLabel).padTop(screenHeight * .02f).padBottom(screenHeight * .02f)
        mainStage.addActor(mainTable)

        // black transition overlay
        blackOverlay = BaseActor(0f, 0f, mainStage)
        blackOverlay.loadImage("whitePixel")
        blackOverlay.color = Color.BLACK
        blackOverlay.touchable = Touchable.childrenOnly
        blackOverlay.setSize(screenWidth, screenHeight)
        blackOverlay.addAction(Actions.fadeOut(2f))
        uiTable.add(blackOverlay)

        GameUtils.setMusicVolumeAndPlay(BaseGame.cityAmbientMusic, BaseGame.audioVolume * 1.2f)
        GameUtils.setMusicVolumeAndPlay(BaseGame.introMusic, BaseGame.audioVolume * .9f)
    }

    override fun update(dt: Float) {
        time += dt
        if (!BaseGame.introMusic!!.isPlaying)
            fadeToLevelScreen()
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (time > 1.5f) {
            // BaseGame.setActiveScreen(IntroScreen()) // TODO: change this to LevelScreen
            fadeToLevelScreen()

            // for debug purposes only
            /*GameUtils.setMusicVolumeAndPlay(BaseGame.cityAmbientMusic, BaseGame.audioVolume * 1.1f)
            GameUtils.setMusicVolumeAndPlay(BaseGame.introMusic, BaseGame.audioVolume * .9f)*/
        }
        return false
    }

    override fun keyDown(keycode: Int): Boolean { // desktop controls
        if (keycode == Input.Keys.ENTER || keycode == Input.Keys.ESCAPE || keycode == Input.Keys.SPACE) {
            if (time > 1.5f) {
                // BaseGame.setActiveScreen(IntroScreen()) // TODO: change this to LevelScreen
                fadeToLevelScreen()

                // for debug purposes only
                /*GameUtils.setMusicVolumeAndPlay(BaseGame.cityAmbientMusic, BaseGame.audioVolume * 1.1f)
            GameUtils.setMusicVolumeAndPlay(BaseGame.introMusic, BaseGame.audioVolume * .9f)*/
            }
        }
        return false
    }

    private fun bottomScene() {
        bottomLabel.addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.run { slowText(bottomLabel, "The year is 2020...") },
                Actions.run { bottomLabel.addAction(Actions.after(Actions.delay(4.5f))) },
                Actions.run { slowText(bottomLabel, "A new Corona Virus is wreaking havoc upon the world...") },
                Actions.run { bottomLabel.addAction(Actions.after(Actions.delay(4.5f))) },
                Actions.run { slowText(bottomLabel, "As a last-ditch effort to save humanity, nanorobots were dispatched using 5G technology to exterminate the threat...") }
        ))
    }

    private fun slowText(label: Label, text: String) {
        var renderingText = ""
        for (i in text.indices) {
            label.addAction(Actions.after(Actions.sequence(
                    Actions.run {
                        renderingText += text[i]
                        label.setText(renderingText)
                        BaseGame.typeWriterSound!!.play(BaseGame.audioVolume * .5f)
                    },
                    Actions.delay(.085f)
            )))
        }
    }

    private fun createTopActor(name: String, speed: Float): BaseActor {
        val topActor = BaseActor(0f, 0f, uiStage)
        topActor.loadImage(name)
        topActor.width = screenWidth * 4
        topActor.height = topSceneMaxHeight
        topActor.addAction(Actions.moveTo(-topActor.width + screenWidth, 0f, speed))
        topSceneTable.add(topActor)
        return topActor
    }

    private fun fadeToLevelScreen() {
        BaseGame.introMusic!!.stop()
        BaseGame.cityAmbientMusic!!.stop()
        blackOverlay.addAction(Actions.sequence(
                Actions.fadeIn(1f),
                Actions.run { BaseGame.setActiveScreen(LevelScreen()) }
        ))
    }
}
