package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame

class ScoreLabel(x: Float, y: Float, s: Stage, label: String) : BaseActor(x, y, s) {
    var flagRemove = false

    init {
        width = Gdx.graphics.width * .3f
        height = Gdx.graphics.height * .25f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())

        setSpeed(Gdx.graphics.height / 1f) // pixels / seconds
        setMotionAngle(270f)
        setBoundaryPolygon(8)

        val score = Label("$label", BaseGame.labelStyle)
        score.color.a = 0f
        score.setOrigin(Align.center)

        val table = Table()
        table.add(score)
        table.setFillParent(true)

        addActor(table)
        score.addAction(Actions.sequence(
                Actions.fadeIn(.3f),
                Actions.fadeOut(.3f)
        ))
        addAction(Actions.sequence( // delayed remove()
                Actions.delay(2f),
                Actions.run { flagRemove = true }
        ))
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (flagRemove)
            remove()
    }
}
