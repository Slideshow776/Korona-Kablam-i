package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Application.ApplicationType.Android
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame

class Player(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    init {
        loadAnimation(BaseGame.textureAtlas!!.findRegion("player"))
        width = Gdx.graphics.width * .125f
        height = Gdx.graphics.height * .25f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())

        setBoundaryPolygon(8)

        setAcceleration(Gdx.graphics.height / 1f) // pixels/seconds
        setMaxSpeed(Gdx.graphics.height / 4f)
        setDeceleration(Gdx.graphics.height / 1f)
    }

    override fun act(dt: Float) {
        super.act(dt)

        applyPhysics(dt)

        if (x < 0 || x + width > getWorldBounds().width) { // stops the player from going off screen
            setSpeed(0f)
            boundToWorld()
        }

        if (Gdx.app.type == Android) {
            if (Gdx.input.accelerometerX > 1.5f)
                accelerateAtAngle(180f)
            else if (Gdx.input.accelerometerX < -1.5f)
                accelerateAtAngle(0f)
        } else { // desktop controls
            if (Gdx.input.isKeyPressed(Keys.A))
                accelerateAtAngle(180f)
            else if (Gdx.input.isKeyPressed(Keys.D))
                accelerateAtAngle(0f)
        }
    }

    fun shoot() {
        val laser = Laser(0f, 0f, this.stage)
        laser.centerAtActor(this)
        laser.setPosition(laser.x, laser.y + height / 2)
    }
}