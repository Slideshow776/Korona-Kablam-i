package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.koronakablami.utils.BaseActor

class Explosions(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private var flagRemove = false

    init {
        width = Gdx.graphics.width * .3f
        height = Gdx.graphics.height * .25f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())

        // explosions effect
        val effect = ExplosionsEffect()
        effect.setPosition(width / 4, height / 3) // by trial and error...
        effect.setScale(Gdx.graphics.height * .00025f)
        addActor(effect)
        effect.start()

        for (i in 4..15)
            createRandomExplosion(s)

        addAction(Actions.sequence( // delayed remove()
                Actions.delay(1.5f),
                Actions.run { flagRemove = true }
        ))
    }

    override fun act(dt: Float) {
        super.act(dt)

        if (flagRemove)
            remove()
    }

    private fun createRandomExplosion(s: Stage) {
        val actor = BaseActor(x, y, s)
        actor.loadImage("explosion")
        actor.width = width
        actor.height = height
        actor.setOrigin(Align.center)
        actor.setPosition(x + Gdx.graphics.width * MathUtils.random(-.15f, -.015f), y + Gdx.graphics.height * MathUtils.random(-.015f, .015f))
        actor.setScale(0f)
        actor.addAction(Actions.sequence(
                Actions.scaleTo(MathUtils.random(1f, 2.5f), MathUtils.random(1f, 2.5f), MathUtils.random(.08f, .5f)),
                Actions.scaleTo(0f, 0f, MathUtils.random(.08f, .5f))
        ))
        actor.addAction(Actions.rotateBy(MathUtils.random(-360f, 360f), 10f))
        addActor(actor)
    }
}
