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

class EnemyBoss(x: Float, y: Float, s: Stage) : BaseActor(x, y, s){
    private var originalHealthPoints = 60
    private var healthPoints = 60
    private var originalX = x
    private var originalY = y
    private var resetPosition = 0f // top offscreen
    private var leftEye: BaseActor
    private var rightEye: BaseActor
    private var tentacles: Array<EnemyBossTentacles>
    private var effects: Array<BossBloodEffect>

    var body: BaseActor
    var spawnTime = MathUtils.random(17f, 117f)// 60f, 180f)
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
        for (i in 0..numTentacles) {
            val tentacle = EnemyBossTentacles(x, y, s)
            tentacle.loadImage("tentacle1")
            tentacle.color = Color(MathUtils.random(.3f, 1f), MathUtils.random(.3f, 1f), MathUtils.random(.3f, 1f), 1f)
            tentacle.width = Gdx.graphics.width * MathUtils.random(.1f, .3f)
            tentacle.height = Gdx.graphics.height * MathUtils.random(.15f, .3f)
            tentacle.setPosition(((body.width / numTentacles) * i) - tentacle.width / 2, 0f)
            addActor(tentacle)
            tentacles.add(tentacle)
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

        for (effect in effects)
            effect.y = body.y

        for (tentacle in tentacles)
            tentacle.y = body.y - tentacle.height * .95f
    }

    fun activate() {
        println("activating boss...")
        active = true
        BaseGame.bossAppearSound!!.play(BaseGame.audioVolume)
        body.addAction(Actions.moveTo(0f, Gdx.graphics.height - body.height, 5f))
    }

    fun hit(hitPositionX: Float) {
        if (active && healthPoints >= 0f) {
            println("damaging boss! $healthPoints")
            healthPoints -= 1

            if (healthPoints == originalHealthPoints / 2)
                BaseGame.bossHurtSound!!.play(BaseGame.audioVolume * 1.5f)

            // blood effect
            val effect = BossBloodEffect()
            effect.setPosition(hitPositionX, body.y - Gdx.graphics.height * .005f) // by trial and error...
            effect.setScale(Gdx.graphics.height * .00025f)
            addActor(effect)
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
        if (body.actions.size == 0) {
            println("defeating boss!")
            BaseGame.bossDefeatedSound!!.play(BaseGame.audioVolume)
            // make sad eyes
            leftEye.addAction(Actions.rotateTo(40f, 1f))
            rightEye.addAction(Actions.rotateTo(-40f, 1f))
            for (tentacle in tentacles)
                tentacle.defeatedMultiplier = .25f
            body.addAction(Actions.sequence(
                    Actions.moveTo(originalX, resetPosition, 5f),
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
        body.setPosition(originalX, resetPosition)
        body.color.a = 1f
        healthPoints = 30
        spawnTime = MathUtils.random(17f, 117f)// 60f, 180f)
        for (effect in effects) effect.remove()
        leftEye.rotation = 0f
        rightEye.rotation = 0f
        for (tentacle in tentacles)
            tentacle.defeatedMultiplier = 1f
    }
}
