package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.pow
import kotlin.math.sqrt
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame

class EnemyBoss(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private var originalHealthPoints = 30
    private var healthPoints = 30
    private var originalShieldPoints = 30
    private var resetPosition = 0f // top offscreen
    private var leftEye: BaseActor
    private var rightEye: BaseActor
    private var bossBloodEffects: Array<BossBloodEffect>
    private var bossBloodEffectIndex = 0
    private var time = 0f
    private var tentaclesAttacking = 0
    private var leftEyeShouldShoot = false
    private var rightEyeShouldShoot = false
    private var laserDuration = 2f
    private var laserSpeed = 30f
    private var hitsTaken = 0

    var body: BaseActor
    var shield: EnemyBossShield
    var shieldPoints = 60
    var tentacles: Array<EnemyBossTentacles>
    var spawnTime = MathUtils.random(7, 7) // for testing

    // var spawnTime = MathUtils.random(60, 180)
    var numDefeated = 0
    var active = false
    var defeated = false
    var player: Player?
    var leftLaser: Laser2
    var rightLaser: Laser2
    var leftEyeIsCharging = false
    var rightEyeIsCharging = false
    var numTentaclesThatShouldAttack = 2

    init {
        // parent
        println("initializing boss...")
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height.toFloat()
        setPosition(0f, 0f)
        bossBloodEffects = Array<BossBloodEffect>()

        // body
        body = BaseActor(x, y, s)
        body.loadImage("enemyBoss1a")
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
            /*tentacle.addAction(Actions.sequence( // mix up the movements so they're not uniform
                    Actions.delay(MathUtils.random(0f, 5f)),
                    Actions.run { tentacle.runShader = true }
            ))*/
            tentacle.speedVariationMultiplier = MathUtils.random(.8f, 1.2f)
            addEffects(originalHealthPoints / numTentacles)
            tentacle.setBoundaryRectangle( // hitbox is 25% less on each side of the actor's box
                    tentacle.width / 4,
                    tentacle.y,
                    3 * tentacle.width / 4,
                    tentacle.height
            )
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

        // shield
        shield = EnemyBossShield(0f, 0f, s)
        shield.setPosition(0f, resetPosition - Gdx.graphics.height * .2f)

        // laser
        player = null
        leftLaser = Laser2(0f, 0f, s)
        rightLaser = Laser2(0f, 0f, s)

        /*leftLaser.debug = true
        rightLaser.debug = true*/

        /*debug = true*/
    }

    override fun act(dt: Float) {
        super.act(dt)

        if (active) time += dt

        // bleeding effect
        for (effect in bossBloodEffects)
            effect.y = body.y

        // tentacles
        for (tentacle in tentacles)
            tentacle.y = body.y - tentacle.height * .95f
        if (tentaclesAttacking < numTentaclesThatShouldAttack && active && time > 7f) {
            val chosenTentacle = tentacles[MathUtils.random(0, (tentacles.size - 1))]
            if (chosenTentacle.actions.size == 0 && !chosenTentacle.attacking) {
                chosenTentacle.addAction(Actions.delay(MathUtils.random(0f, 3f)))
                if (tentaclesAttacking > numTentaclesThatShouldAttack)
                    return
                chosenTentacle.attacking = true
                tentaclesAttacking++
                chosenTentacle.addAction(Actions.sequence(
                        Actions.sizeTo(chosenTentacle.width, Gdx.graphics.height * .9f - body.height, MathUtils.random(5.25f, 6.5f)),
                        Actions.run {
                            chosenTentacle.attacking = false
                            tentaclesAttacking--
                        },
                        Actions.sizeTo(chosenTentacle.width, chosenTentacle.height, MathUtils.random(5.25f, 6.5f))
                ))
            }
        }

        // eye shooting
        if (numDefeated >= 3 && time > 7)
            shoot()
    }

    fun activate() {
        println("activating boss...")
        active = true
        BaseGame.bossAppearSound!!.play(BaseGame.audioVolume)
        body.addAction(Actions.moveTo(0f, Gdx.graphics.height - body.height, 5f))
        shield.addAction(Actions.moveTo(0f, Gdx.graphics.height - body.height - Gdx.graphics.height * .162f, 5f))

        body.addAction(Actions.parallel(
                Actions.sequence(
                        Actions.delay(5f),
                        Actions.run {
                            leftEyeIsCharging = false
                            rightEyeIsCharging = false
                            leftEyeShouldShoot = true
                            rightEyeShouldShoot = true
                        }
                )
        ))

        time = 0f

        // make the boss progressively harder...
        if (numDefeated == 0) disableShield()
        else enableShield()

        if (numDefeated >= 3) {
            if (laserDuration < 2)
                laserDuration += .4f
            if (laserSpeed > 5)
                laserSpeed -= .8f
        }

        if (numDefeated % 10 == 0 && numDefeated > 0)
            numTentaclesThatShouldAttack++
        if (numDefeated > 0) {
            healthPoints = originalHealthPoints + numDefeated * 10
            shieldPoints = originalShieldPoints + numDefeated * 10
        }
    }

    fun hit(hitPositionX: Float) {
        if (active && healthPoints >= 0f) {
            println("damaging boss! $healthPoints")
            healthPoints -= 1

            // make surprised eyes
            if (healthPoints == originalHealthPoints / 2) {
                BaseGame.bossHurtSound!!.play(BaseGame.audioVolume * 1.5f)
                leftEye.addAction(Actions.sequence(
                        Actions.scaleBy(.35f, .35f, .3f),
                        Actions.scaleBy(-.35f, -.35f, .3f)
                ))
                rightEye.addAction(Actions.sequence(
                        Actions.scaleBy(.35f, .35f, .3f),
                        Actions.scaleBy(-.35f, -.35f, .3f)
                ))
            }

            // blood effect
            if (bossBloodEffectIndex < originalHealthPoints) {
                val effect = bossBloodEffects.get(bossBloodEffectIndex)
                effect.setPosition(hitPositionX, body.y - Gdx.graphics.height * .005f) // by trial and error...
                effect.start()
                bossBloodEffectIndex++
            }

            checkIfAngryEyes()

            for (tentacle in tentacles)
                tentacle.velocityXMultiplier = 50f
            if (healthPoints <= 0)
                defeated()
        }
    }

    fun hitShield() {
        println("damaging shield! $shieldPoints")
        if (shieldPoints > 0) {
            BaseGame.hitShieldSound!!.play(BaseGame.audioVolume)
            shieldPoints--
            shield.addAction(Actions.sequence(
                    Actions.alpha(1f, .125f),
                    Actions.alpha(.4f, .125f),
                    Actions.alpha(.8f, .125f)
            ))
            checkIfAngryEyes()
        } else {
            shield.disableCollision = true
            shield.clearActions()
            shield.addAction(Actions.alpha(0f, 2f))
        }
    }

    fun reset() { // all of this happens to the boss off-screen
        println("resetting boss!, $numDefeated")
        active = false
        defeated = false
        time = 0f
        tentaclesAttacking = 0
        hitsTaken = 0
        spawnTime = MathUtils.random(7, 7) // for testing
        // spawnTime = MathUtils.random(60, 180)

        body.clearActions()
        body.setPosition(0f, resetPosition)
        body.color.a = 1f

        healthPoints = originalHealthPoints
        shieldPoints = originalShieldPoints
        shield.color.a = .8f
        shield.disableCollision = false
        shield.setPosition(0f, resetPosition - Gdx.graphics.height * .2f)

        for (effect in bossBloodEffects)
            effect.stop()
        bossBloodEffectIndex = 0

        leftEye.clearActions()
        leftEye.addAction(Actions.rotateTo(0f))
        rightEye.clearActions()
        rightEye.addAction(Actions.rotateTo(0f))

        leftLaser.isVisible = false
        leftLaser.disableCollision = true
        leftEyeIsCharging = false
        rightLaser.isVisible = false
        rightLaser.disableCollision = true
        rightEyeIsCharging = false

        for (tentacle in tentacles) {
            tentacle.defeatedMultiplier = 1f
            tentacle.attacking = false
            tentacle.clearActions()
            tentacle.addAction(Actions.sizeTo(tentacle.width, tentacle.originalHeight, 1f))
        }
    }

    private fun shoot() {
        if (player != null && !defeated) {
            if (leftEyeShouldShoot) {
                println("boss is shooting from left")
                leftEyeShouldShoot = false
                leftLaser.isVisible = true
                firingLaser(leftEye, leftLaser, calculateLaserRotation(leftLaser), true)
            }
            if (rightEyeShouldShoot) {
                println("boss is shooting from right")
                rightEyeShouldShoot = false
                rightLaser.isVisible = true
                firingLaser(rightEye, rightLaser, calculateLaserRotation(rightLaser), false)
            }
        }
    }

    private fun defeated() {
        if (body.actions.size == 0) {
            println("defeating boss!")
            numDefeated += 1
            defeated = true
            leftLaser.isVisible = false
            rightLaser.isVisible = false
            rightEyeIsCharging = false
            leftEyeIsCharging = false
            BaseGame.bossDefeatedSound!!.play(BaseGame.audioVolume)

            // make sad eyes, (assumes angry eyes)
            leftEye.clearActions()
            leftEye.addAction(Actions.parallel(
                    Actions.rotateTo(25f, 1f),
                    Actions.scaleTo(1f, 1f, 1f)
            ))
            rightEye.clearActions()
            rightEye.addAction(Actions.parallel(
                    Actions.rotateTo(-25f, 1f),
                    Actions.scaleTo(1f, 1f, 1f)
            ))

            for (tentacle in tentacles)
                tentacle.defeatedMultiplier = .5f
            body.addAction(Actions.sequence(
                    Actions.moveTo(0f, resetPosition, 5f),
                    Actions.run { reset() }
            ))
            shield.addAction(Actions.moveTo(0f, resetPosition - Gdx.graphics.height * .2f, 5f))
        }
    }

    private fun addEffects(index: Int) {
        // Pre-initialized pooled effects in order to change their drawing order
        for (i in 0 until index) {
            val effect = BossBloodEffect()
            effect.setScale(Gdx.graphics.height * .00025f)
            effect.stop()
            addActor(effect)
            bossBloodEffects.add(effect)
        }
    }

    private fun enableShield() {
        shield.color.a = .8f
        shield.disableCollision = false
        shield.startShieldBehaviour()
    }

    private fun disableShield() {
        shield.color.a = 0f
        shield.disableCollision = true
        shield.clearActions()
    }

    private fun calculateLaserRotation(laser: Laser2): Float {
        val a = abs((laser.y + laser.height) - player!!.y)
        val b = abs((laser.x) - (player!!.x))
        val c = sqrt(a.pow(2) + b.pow(2))
        val thetaInRadians = asin(b / c)
        var thetaInDegrees = Math.toDegrees(thetaInRadians.toDouble()).toFloat()
        if (player!!.x < laser.x) thetaInDegrees *= -1
        return thetaInDegrees
    }

    private fun firingLaser(eye: BaseActor, laser: Laser2, thetaInDegrees: Float, isLeft: Boolean) {
        eye.addAction(Actions.parallel(
                Actions.sequence(
                        Actions.run {
                            if (isLeft) leftEyeIsCharging = true
                            else rightEyeIsCharging = true
                        },
                        Actions.delay(10f), // fixed frequency to match particle effects
                        Actions.run {
                            if (isLeft) {
                                leftEyeIsCharging = false
                            } else {
                                rightEyeIsCharging = false
                            }
                            laser.appear()

                            /**/
                            // both eyes pick a side and fires toward the center
                            if (isLeft && MathUtils.randomBoolean()) { // shooting right
                                laser.rotation = 27f
                                laser.addAction(Actions.rotateBy(-65f, laserSpeed))
                            } else if (isLeft) { // shooting left
                                laser.rotation = -10f
                                laser.addAction(Actions.rotateBy(65f, laserSpeed))
                            }
                            if (!isLeft && MathUtils.randomBoolean()) { // shooting left
                                laser.rotation = -27f
                                laser.addAction(Actions.rotateBy(65f, laserSpeed))
                            } else if (!isLeft) { // shooting right
                                laser.rotation = 12f
                                laser.addAction(Actions.rotateBy(-65f, laserSpeed))
                            }

                            // laser.addAction(Actions.rotateTo(thetaInDegrees, laserSpeed))
                            /**/
                            laser.disappearAfterDuration(laserDuration)
                            laser.setPosition(
                                    eye.x + eye.width / 2 - laser.width / 2,
                                    (Gdx.graphics.height - (body.height / 2)) - laser.height
                            )
                        },
                        Actions.parallel(
                                Actions.sequence(
                                        Actions.delay(MathUtils.random(3f, 8f)),
                                        Actions.run {
                                            if (isLeft) {
                                                leftEyeShouldShoot = true
                                            }
                                        }
                                ),
                                Actions.sequence(
                                        Actions.delay(MathUtils.random(3f, 8f)),
                                        Actions.run {
                                            if (!isLeft) {
                                                rightEyeShouldShoot = true
                                            }
                                        }
                                )
                        )

                ),
                Actions.sequence( // make charging eyes
                        Actions.scaleBy(.5f, .5f, 10f),
                        Actions.scaleBy(-.5f, -.5f, 1f)
                )
        ))
    }

    private fun checkIfAngryEyes() {
        hitsTaken++
        // make angry eyes
        if (hitsTaken == 12) {
            if (leftEye.rotation != -20f)
                leftEye.addAction((Actions.rotateTo(-20f, 5f)))
            if (rightEye.rotation != 20f) {
                rightEye.addAction(Actions.rotateTo(20f, 5f))
            }
        }
    }
}
