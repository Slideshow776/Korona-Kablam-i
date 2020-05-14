package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.koronakablami.utils.BaseActor

class EnemyBossShield(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    init {
        loadImage("shield1")
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height * .15f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())
        setBoundaryRectangle()
        startShieldBehaviour()
    }

    fun startShieldBehaviour() {
        addAction(Actions.forever(Actions.parallel(
                Actions.sequence(
                        Actions.alpha(1f, 1f),
                        Actions.alpha(.7f, 1f)
                ),
                Actions.sequence(
                        Actions.moveBy(0f, Gdx.graphics.height * .005f, 1.5f),
                        Actions.moveBy(0f, Gdx.graphics.height * -.01f, 3f),
                        Actions.moveBy(0f, Gdx.graphics.height * .005f, 1.5f)
                )
        )))
    }
}