package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.koronakablami.utils.BaseActor

class EnemyBoss(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private var healthPoints = 30
    private var originalX = x
    private var originalY = y
    var spawnTime = MathUtils.random(60f, 180f)
    var defeated = 0
    var active = false

    var body: BaseActor

    init {
        println("initializing boss...")
        /*loadImage("enemyBoss1a")*/
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height * .3f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())
        setBoundaryRectangle()

        val tentacles = Tentacles(x, y, s)
        tentacles.loadImage("enemyBoss1b")
        tentacles.width = Gdx.graphics.width.toFloat()
        tentacles.height = Gdx.graphics.height * .5f
        tentacles.centerAtPosition(width / 2, height / 2)
        addActor(tentacles)

        body = BaseActor(x, y, s)
        body.loadImage("enemyBoss1a")
        body.setBoundaryRectangle()
        body.width = width
        body.height = height
        addActor(body)
        body.centerAtPosition(width / 2, height / 2)

        /*debug = true*/
    }

    fun activate() {
        println("activating boss...")
        active = true
        addAction(Actions.moveTo(0f, Gdx.graphics.height - height, 5f))
    }

    fun hit() {
        if (active) {
            println("damaging boss! $healthPoints")
            healthPoints -= 1
            if (healthPoints <= 0)
                defeated()
        }
    }

    private fun defeated() {
        if (actions.size == 0) {
            println("defeating boss!")
            addAction(Actions.sequence(
                    Actions.moveTo(originalX, originalY, 5f),
                    Actions.run {
                        defeated += 1
                        reset()
                    }
            ))
        }
    }

    private fun die() {
        if (actions.size == 0) {
            println("killing boss!")
            addAction(Actions.sequence(
                    Actions.fadeOut(.5f),
                    Actions.run { reset() }
            ))
        }
    }

    private fun reset() {
        println("resetting boss!")
        active = false
        setPosition(originalX, originalY)
        color.a = 1f
        healthPoints = 30
        spawnTime = MathUtils.random(60f, 180f)
    }
}
