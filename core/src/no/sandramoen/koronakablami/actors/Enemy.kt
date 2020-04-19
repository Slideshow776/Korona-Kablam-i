package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame

class Enemy(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    init {
        width = Gdx.graphics.width * .3f
        height = Gdx.graphics.height * .25f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())

        setSpeed(Gdx.graphics.height / 1f) // pixels / seconds
        setMotionAngle(270f)
        setBoundaryPolygon(8)

        val tentacles = Tentacles(x, y, s)
        addActor(tentacles)
        tentacles.centerAtPosition(width / 2, height / 2)

        val body = BaseActor(x, y, s)
        val bodyImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 0 until 18) // average human blinking rate is 15-20 times per minute
            bodyImages.add(BaseGame.textureAtlas!!.findRegion("enemy1a"))
        bodyImages.add(BaseGame.textureAtlas!!.findRegion("enemy2a"))
        body.loadAnimation(bodyImages, .25f, true)
        //body.loadImage(BaseGame.textureAtlas!!.findRegion("enemy1a"))
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
