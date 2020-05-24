package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame

class Laser(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    init {
        loadImage("laser1")
        width = Gdx.graphics.width * .03f
        height = Gdx.graphics.height * .07f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())

        addAction(Actions.delay(.6f))
        addAction(Actions.after(Actions.fadeOut(.25f)))
        addAction(Actions.after(Actions.sequence(
                Actions.run { BaseGame.miss = true },
                Actions.removeActor()))
        )

        setSpeed(Gdx.graphics.height / 1f) // pixels / seconds
        setMaxSpeed(Gdx.graphics.height / 1f)
        setDeceleration(0f)
        setMotionAngle(90f)
        setBoundaryRectangle()
    }

    override fun act(dt: Float) {
        super.act(dt)
        applyPhysics(dt)
    }
}