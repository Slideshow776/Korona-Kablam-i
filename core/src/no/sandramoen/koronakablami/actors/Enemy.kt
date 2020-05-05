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
    private var playedScream = false

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

        if (flagRemove)
            remove()

        if (y < (Gdx.graphics.height * .1f) - height)
            scream()
    }

    fun die() {
        body.remove()
        tentacles.remove()
        setSpeed(0f)
        disableCollision = true

        val animationDuration = 1f
        val a = BaseActor(x - width / 2, y, stage) // left half
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
                        Actions.delay(animationDuration * 2),
                        Actions.run { flagRemove = true }
                )
        ))

        val b = BaseActor(x + width / 2, y, stage) // right half
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

        // blood effect
        val effect = BloodEffect()
        effect.setPosition(width / 4, height / 3) // by trial and error...
        effect.setScale(Gdx.graphics.height * .00025f)
        this.addActor(effect)
        effect.start()
    }

    private fun scream() {
        if (!playedScream && !BaseGame.gameOver) {
            BaseGame.screamSound!!.play(BaseGame.audioVolume)
            body.loadImage("enemy4a")
            body.width = width
            body.height = height
            body.centerAtPosition(width / 2, height / 2)

            addAction(Actions.sequence(
                    Actions.moveBy(Gdx.graphics.width * .01f, 0f, .05f),
                    Actions.moveBy(Gdx.graphics.width * -.02f, 0f, .05f),
                    Actions.moveBy(Gdx.graphics.width * .02f, 0f, .05f),
                    Actions.moveBy(Gdx.graphics.width * -.02f, 0f, .05f),
                    Actions.moveBy(Gdx.graphics.width * .02f, 0f, .05f),
                    Actions.moveBy(Gdx.graphics.width * -.01f, 0f, .05f),
                    Actions.delay(2f),
                    Actions.run { flagRemove = true }
            ))
        } else if (actions.size == 0) {
            addAction(Actions.sequence(
                    Actions.delay(2f),
                    Actions.run { flagRemove = true }
            ))
        }
        playedScream = true
    }
}
