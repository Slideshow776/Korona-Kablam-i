package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.koronakablami.utils.BaseActor

/*
* Assumes all parallax background images fill the whole screen, and moves from top to bottom
* */
class Parallax(x: Float, y: Float, s: Stage, texture: String, speed: Float): BaseActor(x, y, s) {
    init {
        loadImage(texture)
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height.toFloat()
        setSpeed(speed)
        setMotionAngle(270f)
    }

    override fun act(dt: Float) {
        super.act(dt)
        applyPhysics(dt)

        // if moved completely past bottom edge of the screen move to top
        if (y + height < 0)
            moveBy(0f, 2 * height)
    }
}
