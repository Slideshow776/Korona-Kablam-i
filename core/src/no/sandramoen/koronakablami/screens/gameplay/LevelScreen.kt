package no.sandramoen.koronakablami.screens.gameplay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
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
    private lateinit var title: BaseActor
    private lateinit var highScoreLabel: Label
    private lateinit var touchToStartLabel: Label

    override fun initialize() {
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height.toFloat()
        // ratio = width / height

        BaseGame.levelMusic!!.isLooping = true
        BaseGame.levelMusic!!.volume = BaseGame.audioVolume
        BaseGame.levelMusic!!.play()

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

        Parallax(0f, 0f, mainStage, "bloodCellsBackground0", height * .15f)
        Parallax(0f, height, mainStage, "bloodCellsBackground0", height * .15f)
        Parallax(0f, 0f, mainStage, "platelets0", height * .12f)
        Parallax(0f, height, mainStage, "platelets0", height * .12f)
        Parallax(0f, 0f, mainStage, "whiteBloodCells0", height * .05f)
        Parallax(0f, height, mainStage, "whiteBloodCells0", height * .05f)
        Parallax(0f, 0f, mainStage, "bloodCellsBackground1", height * .2f)
        Parallax(0f, height, mainStage, "bloodCellsBackground1", height * .2f)
        Parallax(0f, 0f, mainStage, "bloodCellsBackground2", height * .25f)
        Parallax(0f, height, mainStage, "bloodCellsBackground2", height * .25f)

        BaseActor.setWorldBounds(width, height)
        player = Player(0f, 0f, mainStage)
        player.setPosition(Gdx.graphics.width / 2f - player.width / 2f, Gdx.graphics.height * .03f)
        player.isVisible = false

        Parallax(0f, 0f, mainStage, "bloodCellsBackground3", height * .1f)
        Parallax(0f, height, mainStage, "bloodCellsBackground3", height * .1f)

        scoreLabel = Label("Score: $score", BaseGame.labelStyle)
        scoreLabel.setFontScale(.5f)
        scoreLabel.isVisible = false

        title = BaseActor(0f, 0f, uiStage)
        title.loadImage("title")
        title.width = width * .98f
        title.height = Gdx.graphics.height * .20f * (width / height)

        highScoreLabel = Label("High Score: ${BaseGame.highScore}", BaseGame.labelStyle)
        highScoreLabel.setFontScale(.55f)
        highScoreLabel.color = Color.ORANGE

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
        uiTable.add(title).row()
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
                BaseGame.explosionsSound!!.play(BaseGame.audioVolume)
                val explosion = Explosions(0f, 0f, mainStage)
                explosion.centerAtPosition(player.x + player.width + width * .04f, player.y + player.height / 2)
                setGameOver()
            }
            for (laser: BaseActor in BaseActor.getList(mainStage, Laser::class.java.canonicalName)) {
                if (enemy.overlaps(laser)) {
                    BaseGame.explosionsSound!!.play(BaseGame.audioVolume)
                    val temp = enemy as Enemy
                    val explosion = Explosions(0f, 0f, mainStage)
                    explosion.centerAtActor(temp)
                    temp.die()
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
        } else if ((keycode == Keys.BACK || keycode == Keys.ESCAPE) && gameOver)
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
        title.isVisible = !title.isVisible
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
            highScoreLabel.color = Color.ORANGE
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