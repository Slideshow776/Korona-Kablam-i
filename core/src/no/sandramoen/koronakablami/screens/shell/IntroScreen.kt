package no.sandramoen.koronakablami.screens.shell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame
import no.sandramoen.koronakablami.utils.BaseScreen

class IntroScreen : BaseScreen() {
    private lateinit var touchToSkipLabel: Label
    private lateinit var bottomLabel: Label

    override fun initialize() {
        val screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()

        val topSceneTable = Table()
        val topSceneMaxHeight = (screenHeight * .96f) * 5 / 6
        /*val topActor = BaseActor(0f, 0f, uiStage)
        topActor.loadImage("whitePixel")
        topActor.width = screenWidth
        topActor.height = topSceneMaxHeight
        topActor.color = Color.ORANGE
        topSceneTable.add(topActor)*/
        /*val topLabel = Label("I'm on top!", BaseGame.labelStyle)
        topSceneTable.add(topLabel)*/

        val bottomSceneTable = Table()
        val bottomSceneMaxHeight = (screenHeight * .96f) * 1 / 6
        bottomLabel = Label("", BaseGame.labelStyle)
        bottomLabel.setFontScale(.5f)
        bottomLabel.color = Color.ORANGE
        bottomLabel.setWrap(true)
        bottomLabel.width = screenWidth * .9f
        bottomLabel.setAlignment(Align.center)
        bottomSceneTable.add(bottomLabel).width(screenWidth * .9f)//.padLeft(screenWidth * .1f).padRight(screenWidth * .05f)
        /*bottomSceneTable.debug = true*/
        bottomScene()

        touchToSkipLabel = Label("Touch to skip!", BaseGame.labelStyle)
        touchToSkipLabel.setFontScale(.25f)
        touchToSkipLabel.addAction(Actions.forever(Actions.sequence(
                Actions.alpha(1f, .5f),
                Actions.alpha(.5f, .5f)
        )))

        val sceneTable = Table()
        sceneTable.add(topSceneTable).width(screenWidth).height(topSceneMaxHeight).row()
        sceneTable.add(bottomSceneTable).width(screenWidth).height(bottomSceneMaxHeight)
        sceneTable.debug = true

        val mainTable = Table()
        mainTable.setFillParent(true)
        mainTable.add(sceneTable).fill().expand().row()
        mainTable.add(touchToSkipLabel).padTop(screenHeight * .02f).padBottom(screenHeight * .02f)
        mainStage.addActor(mainTable)

        // black transition overlay
        val blackOverlay = BaseActor(0f, 0f, mainStage)
        blackOverlay.loadImage("whitePixel")
        blackOverlay.color = Color.BLACK
        blackOverlay.touchable = Touchable.childrenOnly
        blackOverlay.setSize(screenWidth, screenHeight)
        blackOverlay.addAction(Actions.fadeOut(2f))

        uiTable.add(blackOverlay)
    }

    override fun update(dt: Float) {}

    private fun bottomScene() {
        val bottomScene = BaseActor(0f, 0f, mainStage) // placeholder for queuing actions...
        bottomScene.addAction(Actions.sequence(
                Actions.delay(1f),
                Actions.run { slowText(bottomLabel, "The year is 2020...") },
                Actions.run { bottomLabel.addAction(Actions.after(Actions.delay(3f))) },
                Actions.run { slowText(bottomLabel, "A new Corona Virus is wreaking havoc upon society...") },
                Actions.run { bottomLabel.addAction(Actions.after(Actions.delay(3f))) },
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
                        BaseGame.typeWriterSound!!.play()
                    },
                    Actions.delay(.08f)
            )))
        }
    }
}
