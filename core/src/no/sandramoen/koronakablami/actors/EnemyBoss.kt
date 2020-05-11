package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame

class EnemyBoss(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private var originalHealthPoints = 60
    private var healthPoints = 60
    private var originalX = x
    private var originalY = y
    private var body: BaseActor
    private var leftEye: BaseActor
    private var rightEye: BaseActor
    private var tentacles: Array<EnemyBossTentacles>
    private var effects: Array<BossBloodEffect>

    var spawnTime = MathUtils.random(17f, 117f)// 60f, 180f)
    var defeated = 0
    var active = false

    init {
        println("initializing boss...")
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height * .3f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())
        setBoundaryRectangle()
        effects = Array<BossBloodEffect>()

        // tentacles
        tentacles = Array<EnemyBossTentacles>()
        for (i in 0..30) {
            val tentacle = EnemyBossTentacles(x, y, s)
            tentacle.loadImage("tentacle1")
            tentacle.color = Color(MathUtils.random(.3f, 1f), MathUtils.random(.3f, 1f), MathUtils.random(.3f, 1f), 1f)
            tentacle.width = Gdx.graphics.width.toFloat() / MathUtils.random(4f, 7f)
            tentacle.height = Gdx.graphics.height * MathUtils.random(.4f, .6f)
            tentacle.centerAtPosition(MathUtils.random(0f, width), height / MathUtils.random(1.5f, 2.5f))
            tentacle.zIndex = 1
            addActor(tentacle)
            tentacles.add(tentacle)
        }

        // body
        body = BaseActor(x, y, s)
        body.loadImage("enemyBoss1a")
        body.setBoundaryRectangle()
        body.width = width
        body.height = height
        addActor(body)
        body.centerAtPosition(width / 2, height / 2)

        // eyes
        leftEye = BaseActor(x, y, s)
        leftEye.loadImage("enemyBoss1c")
        leftEye.setBoundaryRectangle()
        leftEye.width = Gdx.graphics.width * .08f
        leftEye.height = leftEye.width * .75f
        leftEye.setOrigin(Align.center)
        addActor(leftEye)
        leftEye.centerAtPosition(2 * width / 8, height / 2)

        rightEye = BaseActor(x, y, s)
        rightEye.loadImage("enemyBoss1c")
        rightEye.setBoundaryRectangle()
        rightEye.width = leftEye.width
        rightEye.height = leftEye.height
        rightEye.setOrigin(Align.center)
        addActor(rightEye)
        rightEye.centerAtPosition(6 * width / 8, height / 2)

        /*debug = true*/
    }

    fun activate() {
        println("activating boss...")
        active = true
        BaseGame.bossAppearSound!!.play(BaseGame.audioVolume)
        addAction(Actions.moveTo(0f, Gdx.graphics.height - height, 5f))
    }

    fun hit(hitPositionX: Float) {
        if (active && healthPoints >= 0f) {
            println("damaging boss! $healthPoints")
            healthPoints -= 1

            if (healthPoints == originalHealthPoints / 2)
                BaseGame.bossHurtSound!!.play(BaseGame.audioVolume * 1.5f)

            // blood effect
            val effect = BossBloodEffect()
            effect.setPosition(hitPositionX, 0f - Gdx.graphics.height * .005f) // by trial and error...
            effect.setScale(Gdx.graphics.height * .00025f)
            effect.toBack()
            this.addActor(effect)
            effect.start()
            effects.add(effect)

            // make angry eyes
            if (leftEye.rotation != -20f && leftEye.actions.size == 0)
                leftEye.addAction(Actions.rotateTo(-20f, 5f))
            if (rightEye.rotation != 20f && rightEye.actions.size == 0)
                rightEye.addAction(Actions.rotateTo(20f, 5f))

            for (tentacle in tentacles)
                tentacle.velocityXMultiplier = 100f
            if (healthPoints <= 0)
                defeated()
        }
    }

    private fun defeated() {
        if (actions.size == 0) {
            println("defeating boss!")
            BaseGame.bossDefeatedSound!!.play(BaseGame.audioVolume)
            // make sad eyes
            leftEye.addAction(Actions.rotateTo(40f, 1f))
            rightEye.addAction(Actions.rotateTo(-40f, 1f))
            for (tentacle in tentacles)
                tentacle.defeatedMultiplier = .25f
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
        spawnTime = MathUtils.random(17f, 117f)// 60f, 180f)
        for (effect in effects) effect.remove()
        leftEye.rotation = 0f
        rightEye.rotation = 0f
        for (tentacle in tentacles)
            tentacle.defeatedMultiplier = 1f
    }
}
