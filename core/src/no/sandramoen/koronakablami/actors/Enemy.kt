package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame

class Enemy(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    init {
        loadAnimation(BaseGame.textureAtlas!!.findRegion("enemy"))
        width = Gdx.graphics.width * .3f
        height = Gdx.graphics.height * .25f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())

        setSpeed(Gdx.graphics.height / 1f) // pixels / seconds
        setMotionAngle(270f)
        setBoundaryPolygon(8)
    }

    override fun act(dt: Float) {
        super.act(dt)
        applyPhysics(dt)
        if (y < 0f - height)
            remove()
    }
}
