package no.sandramoen.koronakablami.screens.shell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.koronakablami.actors.ShockwaveBackground
import no.sandramoen.koronakablami.screens.gameplay.LevelScreen
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame
import no.sandramoen.koronakablami.utils.BaseScreen
import no.sandramoen.koronakablami.utils.GameUtils

class SplashScreen : BaseScreen() {
    private lateinit var shock: ShockwaveBackground

    override fun initialize() {
        // image with effect
        shock = ShockwaveBackground(0f, 0f, "images/excluded/splash.jpg", mainStage)
        shock.addListener { e: Event ->
            if (GameUtils.isTouchDownEvent(e)) {
                val x = (Gdx.input.x.toFloat() - 0) / (Gdx.graphics.width - 0)
                val y = (Gdx.input.y.toFloat() - 0) / (Gdx.graphics.height - 0)
                shock.start(x, y) // x and y are normalized
            }
            false
        }

        // black overlay
        val background = BaseActor(0f, 0f, mainStage)
        background.loadImage("whitePixel")
        background.color = Color.BLACK
        background.touchable = Touchable.childrenOnly
        background.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        background.addAction(
                Actions.sequence(
                        Actions.fadeIn(0f),
                        Actions.fadeOut(2f),
                        Actions.delay(2f),
                        Actions.fadeIn(2f)
                ))
        background.addAction(Actions.after(Actions.run {
            dispose()
            BaseGame.setActiveScreen(LevelScreen())
        }))
    }

    override fun update(dt: Float) {}

    override fun dispose() {
        super.dispose()
        BaseGame.splashTexture!!.dispose()
        shock.shaderProgram.dispose()
        shock.remove()
    }
}
