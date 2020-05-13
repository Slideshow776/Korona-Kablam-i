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
    private var resetPosition = 0f // top offscreen
    private var leftEye: BaseActor
    private var rightEye: BaseActor
    private var effects: Array<BossBloodEffect>
    private var effectIndex = 0
    private var time = 0f
    private var tentaclesAttacking = 0

    var body: BaseActor
    var tentacles: Array<EnemyBossTentacles>
    var spawnTime = MathUtils.random(60f, 180f)
    var defeated = 0
    var active = false

    init {
        // parent
        println("initializing boss...")
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height.toFloat()
        setPosition(0f, 0f)
        effects = Array<BossBloodEffect>()

        // body
        body = BaseActor(x, y, s)
        body.loadImage("enemyBoss1a")
        body.setBoundaryRectangle()
        body.width = Gdx.graphics.width.toFloat()
        body.height = Gdx.graphics.height * .45f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())
        body.setBoundaryRectangle()
        resetPosition = Gdx.graphics.height.toFloat() * 1.3f
        body.setPosition(0f, resetPosition)
        body.color.a = 1f
        /*body.debug = true*/

        // tentacles
        tentacles = Array<EnemyBossTentacles>()
        val numTentacles = 15
        if (numTentacles > originalHealthPoints)
            Gdx.app.error("EnemyBoss", "numTentacles is bigger than originalHealthPoints!")
        for (i in 0..numTentacles) {
            val tentacle = EnemyBossTentacles(x, y, s)
            tentacle.loadImage("tentacle1")
            tentacle.color = Color(MathUtils.random(.25f, 1f), MathUtils.random(.25f, 1f), MathUtils.random(.25f, 1f), 1f)
            tentacle.width = Gdx.graphics.width * MathUtils.random(.1f, .3f)
            tentacle.height = Gdx.graphics.height * MathUtils.random(.15f, .3f)
            tentacle.originalHeight = tentacle.height
            tentacle.setPosition(((body.width / numTentacles) * i) - tentacle.width / 2, 0f)
            tentacle.addAction(Actions.sequence( // mix up the movements so they're not uniform
                    Actions.delay(MathUtils.random(0f, 5f)),
                    Actions.run { tentacle.runShader = true }
            ))
            tentacle.speedVariationMultiplier = MathUtils.random(.8f, 1.2f)
            addEffects(originalHealthPoints / numTentacles)
            addActor(tentacle)
            tentacles.add(tentacle)
            /*tentacle.debug = true*/
        }

        // eyes
        leftEye = BaseActor(x, y, s)
        leftEye.loadImage("enemyBoss1c")
        leftEye.width = Gdx.graphics.width * .08f
        leftEye.height = leftEye.width * .75f
        leftEye.setOrigin(Align.center)
        body.addActor(leftEye)
        leftEye.centerAtPosition(2 * body.width / 8, body.height / 2)

        rightEye = BaseActor(x, y, s)
        rightEye.loadImage("enemyBoss1c")
        rightEye.width = leftEye.width
        rightEye.height = leftEye.height
        rightEye.setOrigin(Align.center)
        body.addActor(rightEye)
        rightEye.centerAtPosition(6 * body.width / 8, body.height / 2)

        /*debug = true*/
    }

    override fun act(dt: Float) {
        super.act(dt)
        time += dt

        for (effect in effects)
            effect.y = body.y

        for (tentacle in tentacles)
            tentacle.y = body.y - tentacle.height * .95f

        if (tentaclesAttacking < 3 && active && time > 10f) {
            val chosenTentacle = tentacles[MathUtils.random(0, (tentacles.size - 1))]
            if (chosenTentacle.actions.size == 0 && !chosenTentacle.attacking) {
                chosenTentacle.addAction(Actions.delay(MathUtils.random(0f, 3f)))
                /*if (tentaclesAttacking > 3)
                    return*/
                chosenTentacle.attacking = true
                tentaclesAttacking++
                chosenTentacle.addAction(Actions.sequence(
                        Actions.sizeTo(chosenTentacle.width, Gdx.graphics.height * .9f - body.height, 5f),
                        Actions.run {
                            chosenTentacle.attacking = false
                            tentaclesAttacking--
                        },
                        Actions.sizeTo(chosenTentacle.width, chosenTentacle.height, 5f)
                ))
            }
        }
    }

    fun activate() {
        println("activating boss...")
        active = true
        BaseGame.bossAppearSound!!.play(BaseGame.audioVolume)
        body.addAction(Actions.moveTo(0f, Gdx.graphics.height - body.height, 5f))
        time = 0f
    }

    fun hit(hitPositionX: Float) {
        if (active && healthPoints >= 0f) {
            println("damaging boss! $healthPoints")
            healthPoints -= 1

            if (healthPoints == originalHealthPoints / 2)
                BaseGame.bossHurtSound!!.play(BaseGame.audioVolume * 1.5f)

            // blood effect
            if (effectIndex < originalHealthPoints) {
                val effect = effects.get(effectIndex)
                effect.setPosition(hitPositionX, body.y - Gdx.graphics.height * .005f) // by trial and error...
                effect.start()
                effectIndex++
            }

            // make angry eyes
            if (leftEye.rotation != -20f && leftEye.actions.size == 0)
                leftEye.addAction(Actions.rotateTo(-20f, 5f))
            if (rightEye.rotation != 20f && rightEye.actions.size == 0)
                rightEye.addAction(Actions.rotateTo(20f, 5f))

            for (tentacle in tentacles)
                tentacle.velocityXMultiplier = 50f
            if (healthPoints <= 0)
                defeated()
        }
    }

    private fun defeated() {
        if (body.actions.size == 0) {
            println("defeating boss!")
            BaseGame.bossDefeatedSound!!.play(BaseGame.audioVolume)
            // make sad eyes
            leftEye.addAction(Actions.rotateTo(40f, 1f))
            rightEye.addAction(Actions.rotateTo(-40f, 1f))
            for (tentacle in tentacles)
                tentacle.defeatedMultiplier = .5f
            body.addAction(Actions.sequence(
                    Actions.moveTo(0f, resetPosition, 5f),
                    Actions.run {
                        defeated += 1
                        reset()
                    }
            ))
        }
    }

    private fun die() {
        if (body.actions.size == 0) {
            println("killing boss!")
            body.addAction(Actions.sequence(
                    Actions.fadeOut(.5f),
                    Actions.run { reset() }
            ))
        }
    }

    private fun reset() {
        println("resetting boss!")
        active = false
        body.setPosition(0f, resetPosition)
        body.color.a = 1f
        healthPoints = 30
        spawnTime = MathUtils.random(60f, 180f)
        for (effect in effects)
            effect.stop()
        effectIndex = 0
        leftEye.rotation = 0f
        rightEye.rotation = 0f
        for (tentacle in tentacles) {
            tentacle.defeatedMultiplier = 1f
            tentacle.attacking = false
            tentacle.clearActions()
            tentacle.addAction(Actions.sizeTo(tentacle.width, tentacle.originalHeight, 1f))
        }

        time = 0f
        tentaclesAttacking = 0
    }

    // Pre-initialized pooled effects in order to change their drawing order
    private fun addEffects(index: Int) {
        for (i in 0 until index) {
            val effect = BossBloodEffect()
            effect.setScale(Gdx.graphics.height * .00025f)
            effect.stop()
            addActor(effect)
            effects.add(effect)
        }
    }
}
