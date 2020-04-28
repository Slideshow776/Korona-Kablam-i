package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame

class Enemy(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private var body: BaseActor
    private var tentacles: Tentacles
    private var flagRemove = false

    init {
        width = Gdx.graphics.width * .3f
        height = Gdx.graphics.height * .25f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())

        setSpeed(Gdx.graphics.height / 1f) // pixels / seconds
        setMotionAngle(270f)
        setBoundaryPolygon(8)

        tentacles = Tentacles(x, y, s)
        addActor(tentacles)
        tentacles.centerAtPosition(width / 2, height / 2)

        body = BaseActor(x, y, s)
        val bodyImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 0 until 18) // average human blinking rate is 15-20 times per minute
            bodyImages.add(BaseGame.textureAtlas!!.findRegion("enemy1a"))
        bodyImages.add(BaseGame.textureAtlas!!.findRegion("enemy2a"))
        body.loadAnimation(bodyImages, .25f, true)
        body.width = width
        body.height = height
        addActor(body)
        body.centerAtPosition(width / 2, height / 2)
    }

    override fun act(dt: Float) {
        super.act(dt)
        applyPhysics(dt)
        if (y < 0f - height || flagRemove)
            remove()
    }

    fun die() {
        body.remove()
        tentacles.remove()
        setSpeed(0f)
        disableCollision = true
        val animationDuration = 1f

        val a = BaseActor(x - width / 2, y, stage)
        a.loadImage("enemy3a")
        a.width = width
        a.height = height
        a.centerAtPosition(width / 4, height / 2)
        addActor(a)
        a.addAction(Actions.parallel(
                Actions.moveBy(Gdx.graphics.width * MathUtils.random(-.1f, .1f), Gdx.graphics.height * MathUtils.random(-.1f, .1f), animationDuration),
                Actions.rotateBy(MathUtils.random(-360f, 360f), animationDuration),
                Actions.fadeOut(animationDuration),
                Actions.sequence(
                        Actions.delay(animationDuration),
                        Actions.run { flagRemove = true }
                )
        ))

        val b = BaseActor(x + width / 2, y, stage)
        b.loadImage("enemy3b")
        b.width = width
        b.height = height
        b.centerAtPosition(width / 4, height / 2)
        addActor(b)
        b.addAction(Actions.parallel(
                Actions.moveBy(Gdx.graphics.width * MathUtils.random(-.1f, .1f), Gdx.graphics.height * MathUtils.random(-.1f, .1f), animationDuration),
                Actions.rotateBy(MathUtils.random(0f, 90f), animationDuration),
                Actions.fadeOut(animationDuration)
        ))
    }
}
