package no.sandramoen.koronakablami.screens.gameplay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import no.sandramoen.koronakablami.actors.Enemy
import no.sandramoen.koronakablami.actors.Laser
import no.sandramoen.koronakablami.actors.Player
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame
import no.sandramoen.koronakablami.utils.BaseScreen

class LevelScreen : BaseScreen() {

    var width = 0f // will be initialized upon initialize()
    var height = 0f // will be initialized upon initialize()
    // var ratio = 0f // will be initialized upon initialize()

    lateinit var player: Player

    private var playerMayShoot = true
    private var gameOver = false
    private var score = 0

    private var enemyTimer = 0f
    private var enemySpeed = 100f
    private var enemySpawnInterval = 3f

    private lateinit var scoreLabel: Label

    override fun initialize() {
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height.toFloat()
        // ratio = width / height

        BaseActor.setWorldBounds(width, height)
        player = Player(Gdx.graphics.width / 2f, Gdx.graphics.height * .025f, mainStage)

        scoreLabel = Label("Score: $score", BaseGame.labelStyle)
        scoreLabel.setFontScale(.5f)
        val uiTable = Table()
        uiTable.setFillParent(true)
        uiTable.add(scoreLabel).expand().top().padTop(height * .01f)
        // uiTable.debug = true
        uiStage.addActor(uiTable)
    }

    override fun update(dt: Float) {
        playerMayShoot = BaseActor.count(mainStage, Laser::class.java.canonicalName) <= 2

        enemyTimer += dt
        if (enemyTimer > enemySpawnInterval) {
            val enemy = Enemy(0f, 0f, mainStage)
            enemy.setPosition(MathUtils.random(0f, width - enemy.width), height)
            enemy.setSpeed(enemySpeed)

            enemyTimer = 0f
            enemySpawnInterval -= .1f
            enemySpeed += 10

            if (enemySpawnInterval < .5f)
                enemySpawnInterval = .5f

            if (enemySpeed > 400f)
                enemySpeed = 400f
        }

        for (enemy: BaseActor in BaseActor.getList(mainStage, Enemy::class.java.canonicalName)) {
            if (player.overlaps(enemy)) {
                playerMayShoot = false
                player.remove()
                gameOver = true
            }
            for (laser: BaseActor in BaseActor.getList(mainStage, Laser::class.java.canonicalName)) {
                if (enemy.overlaps(laser)) {
                    enemy.remove()
                    laser.remove()
                    score += 100
                    scoreLabel.setText("Score: $score")
                }
            }
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.SPACE && playerMayShoot)
            player.shoot()
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (playerMayShoot)
            player.shoot()
        return false
    }
}