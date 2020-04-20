package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Application.ApplicationType.Android
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame

class Player(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private var engine: Engine

    init {
        width = Gdx.graphics.width * .125f
        height = Gdx.graphics.height * .25f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())

        setAcceleration(Gdx.graphics.height / 1f) // pixels/seconds
        setMaxSpeed(Gdx.graphics.height / 4f)
        setDeceleration(Gdx.graphics.height / 1f)
        setBoundaryPolygon(8)

        // engine
        engine = Engine(x, y, s)
        addActor(engine)
        engine.centerAtPosition(width / 2, 0f)

        // engine effect
        val exhaustEffect = ExhaustEffect()
        exhaustEffect.setPosition(width / 2, Gdx.graphics.height * .02f)
        exhaustEffect.setScale(Gdx.graphics.height * .00025f)
        addActor(exhaustEffect)
        exhaustEffect.start()

        // spaceship body
        val spaceship = BaseActor(x, y, s)
        spaceship.loadImage(BaseGame.textureAtlas!!.findRegion("player1a"))
        spaceship.width = width
        spaceship.height = height
        addActor(spaceship)
        spaceship.centerAtPosition(width / 2, height / 2)

    }

    override fun act(dt: Float) {
        super.act(dt)

        applyPhysics(dt)

        if (x < 0 || x + width > getWorldBounds().width) { // stops the player from going off screen
            setSpeed(0f)
            boundToWorld()
        }

        if (Gdx.app.type == Android) {
            when {
                Gdx.input.accelerometerX > 1.5f -> moveLeft()
                Gdx.input.accelerometerX < -1.5f -> moveRight()
                else -> moveForward()
            }
        } else { // desktop controls
            when {
                Gdx.input.isKeyPressed(Keys.A) -> moveLeft()
                Gdx.input.isKeyPressed(Keys.D) -> moveRight()
                else -> moveForward()
            }

        }
    }

    fun shoot() {
        val laser = Laser(0f, 0f, this.stage)
        laser.centerAtActor(this)
        laser.setPosition(laser.x, laser.y + height / 2)
    }

    private fun moveLeft() {
        accelerateAtAngle(180f)
        addAction(Actions.rotateTo(5f, 1f))
        engine.addAction(Actions.rotateTo(22f, .8f))
        engine.addAction(Actions.moveTo(Gdx.graphics.width * .02f, engine.y, .8f))
    }

    private fun moveRight() {
        accelerateAtAngle(0f)
        addAction(Actions.rotateTo(-5f, 1f))
        engine.addAction(Actions.rotateTo(-22f, .8f))
        engine.addAction(Actions.moveTo(Gdx.graphics.width * -.01f, engine.y, .8f))
    }

    private fun moveForward() {
        addAction(Actions.rotateTo(0f, 1f))
        engine.addAction(Actions.rotateTo(0f, .8f))
        /*engine.centerAtPosition(width / 2, 0f)*/
        engine.addAction(Actions.moveTo(width / 2 - engine.width / 2, engine.y, .8f))
    }
}