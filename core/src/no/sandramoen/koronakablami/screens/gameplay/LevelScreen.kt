package no.sandramoen.koronakablami.screens.gameplay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import no.sandramoen.koronakablami.actors.*
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame
import no.sandramoen.koronakablami.utils.BaseScreen
import no.sandramoen.koronakablami.utils.GameUtils

class LevelScreen : BaseScreen() {

    var width = 0f // will be initialized upon initialize()
    var height = 0f // will be initialized upon initialize()

    lateinit var player: Player

    private var playerMayShoot = false
    private var gameOver = true
    private var pause = true
    private var titleAnimationPlaying = true
    private var score = 0L
    private var playNewHighScoreSoundOnce = true

    private var enemyTimer = 0f
    private var enemySpeed = 100f
    private var enemySpawnInterval = 3f

    private lateinit var scoreLabel: Label
    private lateinit var overlayScoreLabel: Label
    private lateinit var title0: BaseActor
    private lateinit var title1: BaseActor
    private lateinit var title2: BaseActor
    private lateinit var title3: TitleActor
    private lateinit var title4: BaseActor
    private lateinit var highScoreLabel: Label
    private lateinit var touchToStartLabel: Label

    override fun initialize() {
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height.toFloat()

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

        title0 = createTitleFragment("title0", .39f)
        title1 = createTitleFragment("title1", .14f)
        val titleStack = Stack()
        title2 = createTitleFragment("title2", .34f)
        title3 = TitleActor(width / 2 - width * .49f, height / 2, mainStage)
        title4 = createTitleFragment("title4", .1f)
        titleStack.add(title3)
        titleStack.add(title4)

        titleStack.toBack()
        title2.toFront()
        title2.zIndex = 99

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

        uiTable.add(scoreLabel).expandY().top().padTop(height * .01f).colspan(4).row()
        uiTable.add(title0)
        uiTable.add(title1)
        uiTable.add(title2)
        uiTable.add(titleStack).row()
        uiTable.add(highScoreLabel).colspan(4).row()
        uiTable.add(overlayScoreLabel).colspan(4).row()
        uiTable.add(touchToStartLabel).colspan(4).padTop(height * .01f).padBottom(height * .15f).row()
        uiTable.add().expandY()

        /*uiTable.debug = true*/
        uiStage.addActor(uiTable)

        startTitleAnimation()
    }

    override fun update(dt: Float) {
        if (titleAnimationPlaying)
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

        if (gameOver || pause)
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
                    if (MathUtils.random(1, 15) == 1) { // TODO: correct the probability here
                        val rna = RNA(0f, 0f, mainStage)
                        rna.centerAtActor(temp)
                        rna.setSpeed(temp.getSpeed())
                    }
                    temp.die()
                    laser.remove()
                    addToScore(100)
                    scoreLabel.setText("Score: $score")
                    checkHighScore()
                }
            }
        }

        for (rna: BaseActor in BaseActor.getList(mainStage, RNA::class.java.canonicalName)) {
            if (player.overlaps(rna)) {
                addToScore(200)
                BaseGame.pickupSound!!.play(BaseGame.audioVolume)
                rna.remove()
            }
        }
    }

    override fun keyDown(keycode: Int): Boolean { // desktop controls
        if (keycode == Keys.SPACE && playerMayShoot)
            player.shoot()
        else if (keycode == Keys.ENTER && gameOver && !titleAnimationPlaying) {
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
        if (gameOver && !titleAnimationPlaying) {
            gameOver = false
            pause = false
            playerMayShoot = true
            renderOverlay()
        }
        return false
    }

    private fun renderOverlay() {
        scoreLabel.isVisible = !scoreLabel.isVisible
        title0.isVisible = !title0.isVisible
        title1.isVisible = !title1.isVisible
        title2.isVisible = !title2.isVisible
        title3.isVisible = !title3.isVisible
        title4.isVisible = !title4.isVisible
        highScoreLabel.isVisible = !scoreLabel.isVisible
        overlayScoreLabel.isVisible = !scoreLabel.isVisible
        touchToStartLabel.isVisible = !scoreLabel.isVisible
        if (gameOver) { // viewing the menu
            startTitleAnimation()
            overlayScoreLabel.setText("Score: $score")
        } else { // playing
            score = 0
            scoreLabel.setText("Score: $score")
            player.isVisible = true
            player.setPosition(Gdx.graphics.width / 2f - player.width / 2f, Gdx.graphics.height * .03f)

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
        playNewHighScoreSoundOnce = true
        GameUtils.saveGameState()
        renderOverlay()
    }

    private fun checkHighScore() {
        if (score > BaseGame.highScore) {
            BaseGame.highScore = score
            overlayScoreLabel.color = Color.ORANGE
            scoreLabel.addAction(Actions.color(Color.ORANGE, 1f))
            if (playNewHighScoreSoundOnce && score >= 400L)
                BaseGame.newHighScoreSound!!.play(BaseGame.audioVolume)
            playNewHighScoreSoundOnce = false
            highScoreLabel.setText("New High Score: ${BaseGame.highScore}")
            highScoreLabel.color = Color.PURPLE
        }
    }

    private fun createTitleFragment(name: String, widthPercent: Float): BaseActor {
        val actor = BaseActor(0f, 0f, uiStage)
        actor.loadImage(name)
        actor.width = width * widthPercent
        actor.height = Gdx.graphics.height * .20f * (width / height)
        return actor
    }

    private fun startTitleAnimation() {
        // setup
        touchToStartLabel.isVisible = false
        titleAnimationPlaying = true
        title3.isAnimated = false

        // animation
        val animationSpeed = 1.0f // use this to change the total animation speed (bigger is slower, lesser is faster)
        title1.addAction(Actions.sequence( // "KA"
                Actions.delay(.25f * animationSpeed),
                Actions.parallel(
                        Actions.sequence(
                                /*Actions.moveBy(0f, title1.height * -.4f),*/
                                Actions.moveBy(0f, title1.height * -.07f, 1.1f * animationSpeed, Interpolation.exp10In)
                        ),
                        Actions.sizeTo(title1.width, title1.height * 1.5f, 1.1f * animationSpeed, Interpolation.exp10In)
                ),
                Actions.delay(.125f),
                Actions.parallel(
                        Actions.sizeTo(title1.width, title1.height * .5f, .1f * animationSpeed),
                        Actions.moveBy(0f, title1.height * .13f, .1f * animationSpeed)
                ),
                Actions.delay(.5f),
                Actions.parallel(
                        Actions.sizeTo(title1.width, title1.height, .5f * animationSpeed),
                        Actions.moveBy(0f, title1.height * -.06f, .5f * animationSpeed)
                )
        ))
        title2.addAction(Actions.sequence( // "BLAM"
                Actions.delay(1.575f + .25f * animationSpeed),
                Actions.sizeTo(title2.width * 1.2f, title2.width * 1.2f, .2f * animationSpeed, Interpolation.swingOut),
                Actions.run { // "i!"
                    title3.isAnimated = true
                    title3.addAction(Actions.fadeIn(2f * animationSpeed))
                    title4.addAction(Actions.fadeIn(2f * animationSpeed))
                },
                Actions.sizeTo(title2.width, title2.height, .5f * animationSpeed, Interpolation.bounceOut),
                Actions.run {
                    titleAnimationPlaying = false
                    touchToStartLabel.isVisible = true
                }
        ))
    }

    private fun addToScore(score: Int) {
        this.score += score
        scoreLabel.setText("Score: ${this.score}")
    }
}
