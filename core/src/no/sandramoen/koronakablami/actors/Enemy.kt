package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame

class Enemy(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    init {
        /*val images: Array<TextureAtlas.AtlasRegion> = Array()
        images.add(BaseGame.textureAtlas!!.findRegion("enemy"))
        loadAnimation(images, .5f, true)*/
        width = Gdx.graphics.width * .3f
        height = Gdx.graphics.height * .25f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())

        setSpeed(Gdx.graphics.height / 1f) // pixels / seconds
        setMotionAngle(270f)
        setBoundaryPolygon(8)

        val tentacles = Tentacles(x, y, s)
        addActor(tentacles)
        tentacles.centerAtPosition(width / 2, height / 2)

        val body = BaseActor(x, y, s)
        body.loadImage(BaseGame.textureAtlas!!.findRegion("enemy2a"))
        body.width = width
        body.height = height
        addActor(body)
        body.centerAtPosition(width / 2, height / 2)
    }

    override fun act(dt: Float) {
        super.act(dt)
        applyPhysics(dt)
        if (y < 0f - height)
            remove()
    }
}
