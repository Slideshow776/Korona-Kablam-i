package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame

class Laser2(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    init {
        loadImage("laser2")
        width = Gdx.graphics.width * .05f
        height = Gdx.graphics.height * 1.1f
        setOrigin(width / 2f, height)
        setBoundaryRectangle()
        disableCollision = true
        color.a = 0f

        /*debug = true*/
    }

    fun appear(leftEye: Boolean) {
        if (leftEye && !BaseGame.gameOver) BaseGame.laser1Sound!!.play(BaseGame.audioVolume * .5f)
        else if (!BaseGame.gameOver)BaseGame.laser2Sound!!.play(BaseGame.audioVolume * .5f)

        disableCollision = false
        val colorSaturation = .7f
        addAction(Actions.forever(Actions.sequence(
                Actions.color(Color(1f, 1f, 1f, 1f), .25f),
                Actions.color(Color(colorSaturation, colorSaturation, colorSaturation, 1f), .25f)
        )))
    }

    fun disappearAfterDuration(leftEye: Boolean, duration: Float = 0f) {
        addAction(Actions.sequence(
                Actions.delay(duration),
                Actions.run {
                    disableCollision = true
                    actions.clear()
                    color.a = 0f
                    if (leftEye) BaseGame.laser1Sound!!.stop()
                    else BaseGame.laser2Sound!!.stop()
                }
        ))
    }
}
