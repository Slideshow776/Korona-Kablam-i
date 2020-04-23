package no.sandramoen.koronakablami.screens.gameplay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import no.sandramoen.koronakablami.actors.*
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame
import no.sandramoen.koronakablami.utils.BaseScreen
import no.sandramoen.koronakablami.utils.GameUtils

class LevelScreen : BaseScreen() {

    var width = 0f // will be initialized upon initialize()
    var height = 0f // will be initialized upon initialize()
    // var ratio = 0f // will be initialized upon initialize()

    lateinit var player: Player

    private var playerMayShoot = false
    private var gameOver = true
    private var pause = true
    private var score = 0L

    private var enemyTimer = 0f
    private var enemySpeed = 100f
    private var enemySpawnInterval = 3f

    private lateinit var scoreLabel: Label
    private lateinit var overlayScoreLabel: Label
    private lateinit var highScoreLabel: Label
    private lateinit var touchToStartLabel: Label

    override fun initialize() {
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height.toFloat()
        // ratio = width / height

        Parallax(0f, 0f, mainStage, "background4", height * .032f)
        Parallax(0f, height, mainStage, "background4", height * .032f)
        Parallax(0f, 0f, mainStage, "background3", height * .049f)
        Parallax(0f, height, mainStage, "background3", height * .049f)
        Parallax(0f, 0f, mainStage, "background2", height * .066f)
        Parallax(0f, height, mainStage, "background2", height * .066f)
        Parallax(0f, 0f, mainStage, "background1", height * .083f)
        Parallax(0f, height, mainStage, "background1", height * .083f)
        Parallax(0f, 0f, mainStage, "background0", height * .1f)
        Parallax(0f, height, mainStage, "background0", height * .1f)

        BaseActor.setWorldBounds(width, height)
        player = Player(0f, 0f, mainStage)
        player.setPosition(Gdx.graphics.width / 2f - player.width / 2f, Gdx.graphics.height * .03f)
        player.isVisible = false

        scoreLabel = Label("Score: $score", BaseGame.labelStyle)
        scoreLabel.setFontScale(.5f)
        scoreLabel.isVisible = false

        highScoreLabel = Label("High Score: ${BaseGame.highScore}", BaseGame.labelStyle)
        highScoreLabel.setFontScale(.55f)
        highScoreLabel.color = Color.YELLOW

        overlayScoreLabel = Label("Score: $score", BaseGame.labelStyle)
        overlayScoreLabel.setFontScale(.45f)
        overlayScoreLabel.isVisible = false

        touchToStartLabel = Label("Touch to start!", BaseGame.labelStyle)
        touchToStartLabel.setFontScale(.25f)
        touchToStartLabel.addAction(Actions.forever(Actions.sequence(
                Actions.alpha(1f, .5f),
                Actions.alpha(.5f, .5f)
        )))

        val uiTable = Table()
        uiTable.setFillParent(true)

        uiTable.add(scoreLabel).expandY().top().padTop(height * .01f).row()
        uiTable.add(highScoreLabel).row()
        uiTable.add(overlayScoreLabel).row()
        uiTable.add(touchToStartLabel).padBottom(height * .15f).row()
        uiTable.add().expandY()

        /*uiTable.debug = true*/
        uiStage.addActor(uiTable)
    }

    override fun update(dt: Float) {
        if (pause)
            return

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

        if (gameOver)
            return

        playerMayShoot = BaseActor.count(mainStage, Laser::class.java.canonicalName) <= 2

        for (enemy: BaseActor in BaseActor.getList(mainStage, Enemy::class.java.canonicalName)) {
            if (player.overlaps(enemy)) {
                setGameOver()
            }
            for (laser: BaseActor in BaseActor.getList(mainStage, Laser::class.java.canonicalName)) {
                if (enemy.overlaps(laser)) {
                    enemy.remove()
                    laser.remove()
                    score += 100
                    scoreLabel.setText("Score: $score")
                    checkAndSaveHighScore()
                }
            }
        }

    }

    override fun keyDown(keycode: Int): Boolean { // desktop controls
        if (keycode == Keys.SPACE && playerMayShoot)
            player.shoot()
        else if (keycode == Keys.ENTER && gameOver) {
            gameOver = false
            pause = false
            playerMayShoot = true
            renderOverlay()
        }
        else if ((keycode == Keys.BACK || keycode == Keys.ESCAPE) && gameOver)
            Gdx.app.exit()
        else if (keycode == Keys.BACK || keycode == Keys.ESCAPE)
            setGameOver()
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (playerMayShoot)
            player.shoot()
        if (gameOver) {
            gameOver = false
            pause = false
            playerMayShoot = true
            renderOverlay()
        }
        return false
    }

    private fun renderOverlay() {
        scoreLabel.isVisible = !scoreLabel.isVisible
        highScoreLabel.isVisible = !scoreLabel.isVisible
        overlayScoreLabel.isVisible = !scoreLabel.isVisible
        touchToStartLabel.isVisible = !scoreLabel.isVisible
        if (gameOver) {
            overlayScoreLabel.setText("Score: $score")
        } else {
            score = 0
            scoreLabel.setText("Score: $score")
            player.isVisible = true

            for (enemy: BaseActor in BaseActor.getList(mainStage, Enemy::class.java.canonicalName))
                enemy.remove()
            enemyTimer = 0f
            enemySpeed = 100f
            enemySpawnInterval = 3f

            overlayScoreLabel.color = Color.WHITE
            scoreLabel.addAction(Actions.color(Color.WHITE, 1f))
            highScoreLabel.setText("High Score: ${BaseGame.highScore}")
            highScoreLabel.color = Color.YELLOW
            touchToStartLabel.setText("Touch to restart!")
        }
    }

    private fun setGameOver() {
        gameOver = true
        playerMayShoot = false
        player.isVisible = false
        renderOverlay()
    }

    private fun checkAndSaveHighScore() {
        if (score > BaseGame.highScore) {
            BaseGame.highScore = score
            GameUtils.saveGameState()
            overlayScoreLabel.color = Color.RED
            scoreLabel.addAction(Actions.color(Color.RED, 1f))
            highScoreLabel.setText("New High Score: ${BaseGame.highScore}")
            highScoreLabel.color = Color.PURPLE
        }
    }
}