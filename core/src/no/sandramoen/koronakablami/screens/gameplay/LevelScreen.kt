package no.sandramoen.koronakablami.screens.gameplay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Array
import no.sandramoen.koronakablami.actors.*
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame
import no.sandramoen.koronakablami.utils.BaseScreen
import no.sandramoen.koronakablami.utils.GameUtils
import kotlin.math.ceil

class LevelScreen : BaseScreen() {

    private var width = 0f // will be initialized upon initialize()
    private var height = 0f // will be initialized upon initialize()

    private lateinit var player: Player
    private var playerMayShoot = false
    private var pause = true
    private var titleAnimationPlaying = true
    private var score = 0f
    private var scoreMotivationalMultiplier = 1f
    private var playNewHighScoreSoundOnce = true
    private var laserHits: Int = 0
    private lateinit var backgrounds: Array<Parallax>

    private var enemyTimer = 0f
    private var enemySpeed = 100f
    private var enemySpawnInterval = 3f

    private lateinit var scoreLabelA: Label
    private lateinit var scoreLabelB: Label
    private lateinit var scoreLabelBGroup: Group
    private lateinit var scoreTable: Table
    private lateinit var overlayScoreLabel: Label
    private lateinit var motivationLabel: Label
    private lateinit var title0: BaseActor
    private lateinit var title1: BaseActor
    private lateinit var title2: BaseActor
    private lateinit var title3: TitleActor
    private lateinit var title4: BaseActor
    private lateinit var highScoreLabel: Label
    private lateinit var touchToStartLabel: Label
    private lateinit var tiltTutorialLabel: Label
    private var tiltTutorialTimer = 0f
    private var tiltTutorialIsActive = false

    override fun initialize() {
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height.toFloat()

        BaseGame.levelMusic!!.isLooping = true
        BaseGame.levelMusic!!.volume = BaseGame.audioVolume
        BaseGame.levelMusic!!.play()

        backgrounds = Array<Parallax>()
        backgrounds.add(Parallax(0f, 0f, mainStage, "background4", height * .032f))
        backgrounds.add(Parallax(0f, height, mainStage, "background4", height * .032f))
        backgrounds.add(Parallax(0f, 0f, mainStage, "background3", height * .049f))
        backgrounds.add(Parallax(0f, height, mainStage, "background3", height * .049f))
        backgrounds.add(Parallax(0f, 0f, mainStage, "background2", height * .066f))
        backgrounds.add(Parallax(0f, height, mainStage, "background2", height * .066f))
        backgrounds.add(Parallax(0f, 0f, mainStage, "background1", height * .083f))
        backgrounds.add(Parallax(0f, height, mainStage, "background1", height * .083f))
        backgrounds.add(Parallax(0f, 0f, mainStage, "background0", height * .1f))
        backgrounds.add(Parallax(0f, height, mainStage, "background0", height * .1f))

        backgrounds.add(Parallax(0f, 0f, mainStage, "bloodCellsBackground0", height * .15f))
        backgrounds.add(Parallax(0f, height, mainStage, "bloodCellsBackground0", height * .15f))
        backgrounds.add(Parallax(0f, 0f, mainStage, "platelets0", height * .12f))
        backgrounds.add(Parallax(0f, height, mainStage, "platelets0", height * .12f))
        backgrounds.add(Parallax(0f, 0f, mainStage, "whiteBloodCells0", height * .05f))
        backgrounds.add(Parallax(0f, height, mainStage, "whiteBloodCells0", height * .05f))
        backgrounds.add(Parallax(0f, 0f, mainStage, "bloodCellsBackground1", height * .2f))
        backgrounds.add(Parallax(0f, height, mainStage, "bloodCellsBackground1", height * .2f))
        backgrounds.add(Parallax(0f, 0f, mainStage, "bloodCellsBackground2", height * .25f))
        backgrounds.add(Parallax(0f, height, mainStage, "bloodCellsBackground2", height * .25f))

        BaseActor.setWorldBounds(width, height)
        player = Player(0f, 0f, mainStage)
        player.setPosition(width / 2f - player.width / 2f, height * .03f)
        player.isVisible = false

        backgrounds.add(Parallax(0f, 0f, mainStage, "bloodCellsBackground3", height * .1f))
        backgrounds.add(Parallax(0f, height, mainStage, "bloodCellsBackground3", height * .1f))

        // user interface, overlay menu
        scoreLabelA = Label("Score: ", BaseGame.labelStyle)
        scoreLabelA.setFontScale(.5f)
        scoreLabelA.isVisible = false
        scoreLabelB = Label("$score", BaseGame.labelStyle)
        scoreLabelB.setFontScale(.5f)
        scoreLabelB.isVisible = false
        scoreLabelBGroup = Group()
        scoreLabelBGroup.addActor(scoreLabelB)
        scoreLabelBGroup.width = scoreLabelB.width
        scoreTable = Table()
        scoreTable.add(scoreLabelA)
        scoreTable.add(scoreLabelBGroup).padTop(height * .042f)//.padRight(width * .1f)

        motivationLabel = Label("", BaseGame.labelStyle)
        motivationLabel.setFontScale(.7f)
        motivationLabel.color = Color(29f / 255, 86f / 255, 172f / 255, 1f)

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

        highScoreLabel = Label("High Score: ${BaseGame.highScore.toLong()}", BaseGame.labelStyle)
        highScoreLabel.setFontScale(.55f)
        highScoreLabel.color = Color.ORANGE

        overlayScoreLabel = Label("Score: ${score.toLong()}", BaseGame.labelStyle)
        overlayScoreLabel.setFontScale(.45f)
        overlayScoreLabel.isVisible = false

        touchToStartLabel = Label("Touch to start!", BaseGame.labelStyle)
        touchToStartLabel.setFontScale(.25f)
        touchToStartLabel.addAction(Actions.forever(Actions.sequence(
                Actions.alpha(1f, .5f),
                Actions.alpha(.5f, .5f)
        )))

        tiltTutorialLabel = Label("Tilt to go left or right!", BaseGame.labelStyle)
        tiltTutorialLabel.setFontScale(.35f)
        tiltTutorialLabel.color.a = 0f

        val uiTable = Table()
        uiTable.setFillParent(true)

        uiTable.add(scoreTable).expandY().top().padTop(height * .01f).colspan(4).width(width * .95f).row()
        uiTable.add(motivationLabel).padTop(height * .01f).colspan(4).row()
        uiTable.add(title0)
        uiTable.add(title1)
        uiTable.add(title2)
        uiTable.add(titleStack).row()
        uiTable.add(highScoreLabel).colspan(4).padTop(height * .01f).row()
        uiTable.add(overlayScoreLabel).colspan(4).padTop(height * .01f).row()
        uiTable.add(touchToStartLabel).colspan(4).padTop(height * .02f).padBottom(height * .15f).row()
        uiTable.add(tiltTutorialLabel).colspan(4).padTop(height * .02f).padBottom(height * .15f).row()
        uiTable.add().expandY()

        /*uiTable.debug = true*/
        uiStage.addActor(uiTable)

        startTitleAnimation()
    }

    override fun update(dt: Float) {
        if (titleAnimationPlaying)
            return

        spawnEnemies(dt)

        if (BaseGame.gameOver || pause)
            return

        playerMayShoot = BaseActor.count(mainStage, Laser::class.java.canonicalName) <= 2
        if (BaseGame.miss) {
            BaseGame.miss = false
            laserHits = 0
            scoreMotivationalMultiplier = 1f
            resetBackgroundSpeed()
        }

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
                    val tempEnemy = enemy as Enemy
                    val explosion = Explosions(0f, 0f, mainStage)
                    explosion.centerAtActor(tempEnemy)
                    if (MathUtils.random(1, 15) == 1) {
                        val rna = RNA(0f, 0f, mainStage)
                        rna.centerAtActor(tempEnemy)
                        rna.setSpeed(tempEnemy.getSpeed())
                    }
                    val tempLabel = ScoreLabel(0f, 0f, mainStage, "+${ceil(100f * scoreMotivationalMultiplier).toLong()}")
                    tempLabel.scaleBy(-.6f)
                    tempLabel.centerAtActor(tempEnemy)
                    tempLabel.setSpeed(tempEnemy.getSpeed())
                    tempEnemy.die()
                    laser.remove()
                    laserHits++
                    addToScore(100f)
                    motivate()
                    scoreLabelB.setText("${score.toLong()}")
                    checkHighScore()
                }
            }
        }

        for (rna: BaseActor in BaseActor.getList(mainStage, RNA::class.java.canonicalName)) {
            if (player.overlaps(rna)) {
                addToScore(200f)
                BaseGame.pickupSound!!.play(BaseGame.audioVolume)
                val tempLabel = ScoreLabel(0f, 0f, mainStage, "+${ceil(200 * scoreMotivationalMultiplier).toLong()}")
                tempLabel.scaleBy(-.6f)
                tempLabel.centerAtActor(rna)
                tempLabel.setSpeed(rna.getSpeed())
                rna.remove()
            }
        }

        tiltTutorial(dt)
    }

    override fun keyDown(keycode: Int): Boolean { // desktop controls
        if (keycode == Keys.SPACE && playerMayShoot)
            player.shoot()
        else if (keycode == Keys.ENTER && BaseGame.gameOver && !titleAnimationPlaying) {
            BaseGame.gameOver = false
            pause = false
            playerMayShoot = true
            renderOverlay()
        } else if ((keycode == Keys.BACK || keycode == Keys.ESCAPE) && BaseGame.gameOver)
            Gdx.app.exit()
        else if (keycode == Keys.BACK || keycode == Keys.ESCAPE)
            setGameOver()
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (playerMayShoot)
            player.shoot()
        if (BaseGame.gameOver && !titleAnimationPlaying) {
            BaseGame.gameOver = false
            pause = false
            playerMayShoot = true
            renderOverlay()
        }
        return false
    }

    private fun renderOverlay() {
        scoreLabelA.isVisible = !scoreLabelA.isVisible
        scoreLabelB.isVisible = !scoreLabelB.isVisible
        title0.isVisible = !title0.isVisible
        title1.isVisible = !title1.isVisible
        title2.isVisible = !title2.isVisible
        title3.isVisible = !title3.isVisible
        title4.isVisible = !title4.isVisible
        highScoreLabel.isVisible = !scoreLabelA.isVisible
        overlayScoreLabel.isVisible = !scoreLabelA.isVisible
        touchToStartLabel.isVisible = !scoreLabelA.isVisible
        if (BaseGame.gameOver) { // viewing the menu
            startTitleAnimation()
            overlayScoreLabel.setText("Score: ${score.toLong()}")
        } else { // playing
            score = 0f
            scoreLabelB.setText("${score.toLong()}")
            centerScoreLabel(false)
            player.isVisible = true
            player.setPosition(Gdx.graphics.width / 2f - player.width / 2f, Gdx.graphics.height * .03f)

            for (enemy: BaseActor in BaseActor.getList(mainStage, Enemy::class.java.canonicalName))
                enemy.remove()
            for (rna: BaseActor in BaseActor.getList(mainStage, RNA::class.java.canonicalName))
                rna.remove()

            enemyTimer = 0f
            enemySpeed = 100f
            enemySpawnInterval = 3f

            overlayScoreLabel.color = Color.WHITE
            scoreLabelA.addAction(Actions.color(Color.WHITE, 1f))
            scoreLabelB.addAction(Actions.color(Color.WHITE, 1f))
            highScoreLabel.setText("High Score: ${BaseGame.highScore.toLong()}")
            highScoreLabel.color = Color.ORANGE
            touchToStartLabel.setText("Touch to restart!")
        }
    }

    private fun setGameOver() {
        BaseGame.gameOver = true
        playerMayShoot = false
        player.isVisible = false
        player.hasMoved = false
        resetTutorial()
        playNewHighScoreSoundOnce = true
        laserHits = 0
        scoreMotivationalMultiplier = 1f
        resetBackgroundSpeed()
        GameUtils.saveGameState()
        renderOverlay()
    }

    private fun checkHighScore() {
        if (score > BaseGame.highScore) {
            BaseGame.highScore = score
            overlayScoreLabel.color = Color.ORANGE
            scoreLabelA.addAction(Actions.color(Color.ORANGE, 1f))
            scoreLabelB.addAction(Actions.color(Color.ORANGE, 1f))
            if (playNewHighScoreSoundOnce && score >= 400L)
                BaseGame.newHighScoreSound!!.play(BaseGame.audioVolume)
            playNewHighScoreSoundOnce = false
            highScoreLabel.setText("New High Score: ${BaseGame.highScore.toLong()}")
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

    private fun addToScore(score: Float) {
        this.score += ceil(score * scoreMotivationalMultiplier)
        scoreLabelB.setText("${this.score.toLong()}")
        scoreLabelBGroup.addAction(Actions.sequence(
                Actions.scaleBy(.15f, .15f, .05f),
                Actions.scaleBy(-.15f, -.15f, .05f)
        ))
        if (this.score.toString()[0].toString().toLong() % this.score == 1f) // bugfix: label moves only once per new digit length
            centerScoreLabel()
    }

    private fun motivate() {
        var motivation = ""
        when (laserHits) {
            3 -> {
                motivation = "Nice!"
                scoreMotivationalMultiplier = 1.05f
            }
            6 -> {
                motivation = "Cool!"
                scoreMotivationalMultiplier = 1.1f
            }
            9 -> {
                motivation = "Wow!"
                scoreMotivationalMultiplier = 1.15f
            }
            12 -> {
                motivation = "No way!"
                scoreMotivationalMultiplier = 1.2f
            }
            18 -> {
                motivation = "Fierce!"
                scoreMotivationalMultiplier = 1.25f
            }
            24 -> {
                motivation = "Awesome!"
                scoreMotivationalMultiplier = 1.3f
            }
            30 -> {
                motivation = "Sayonara!"
                scoreMotivationalMultiplier = 1.35f
            }
            40 -> {
                motivation = "All out of gum!"
                scoreMotivationalMultiplier = 1.4f
            }
            50 -> {
                motivation = "Piece of cake!"
                scoreMotivationalMultiplier = 1.45f
            }
            60 -> {
                motivation = "Yippee Ki Yay!"
                scoreMotivationalMultiplier = 1.5f
            }
            70 -> {
                motivation = "Dodge this"
                scoreMotivationalMultiplier = 1.55f
            }
            80 -> {
                motivation = "Astounding!"
                scoreMotivationalMultiplier = 1.6f
            }
            90 -> {
                motivation = "Staggering!"
                scoreMotivationalMultiplier = 1.65f
            }
            100 -> {
                motivation = "You shall not pass!"
                scoreMotivationalMultiplier = 1.7f
            }
            110 -> {
                motivation = "Extraordinary!"
                scoreMotivationalMultiplier = 1.75f
            }
            120 -> {
                motivation = "Resistance is futile!"
                scoreMotivationalMultiplier = 1.8f
            }
            130 -> {
                motivation = "Assimilate this!"
                scoreMotivationalMultiplier = 1.85f
            }
            140 -> {
                motivation = "Unbelievable!"
                scoreMotivationalMultiplier = 1.9f
            }
            150 -> {
                motivation = "Breathtaking!"
                scoreMotivationalMultiplier = 1.95f
            }
            160 -> {
                motivation = "Epic!"
                scoreMotivationalMultiplier = 2f
            }
            170 -> {
                motivation = "Legendary!"
                scoreMotivationalMultiplier = 2.05f
            }
        }

        for (background in backgrounds)
            background.setSpeed(background.originalSpeed * scoreMotivationalMultiplier)

        if (motivationLabel.actions.size == 0 && !motivationLabel.textEquals(motivation)) {
            motivationLabel.setText(motivation)
            motivationLabel.color.a = 0f
            motivationLabel.addAction(Actions.sequence(
                    Actions.fadeIn(.5f),
                    Actions.delay(.6f),
                    Actions.fadeOut(.5f)
            ))
        }
    }

    private fun centerScoreLabel(animated: Boolean = true) {
        val offset = width * .05f // five percent of total width of screen
        val scoreWidth = scoreLabelA.width + scoreLabelB.prefWidth + offset
        val moveA = width / 2 - scoreWidth / 2
        val moveB = moveA + scoreLabelA.width

        var duration = .5f
        if (!animated) duration = 0f
        scoreLabelA.addAction(Actions.moveTo(moveA, scoreLabelA.y, duration, Interpolation.pow5))
        scoreLabelBGroup.addAction(Actions.moveTo(moveB, scoreLabelB.y, duration, Interpolation.pow5))
    }

    private fun resetBackgroundSpeed() {
        for (background in backgrounds)
            background.setSpeed(background.originalSpeed)
    }

    private fun tiltTutorial(dt: Float) {
        if (tiltTutorialTimer < 10)
            tiltTutorialTimer += dt
        else if (player.hasMoved && tiltTutorialIsActive) {
            tiltTutorialIsActive = false
            tiltTutorialLabel.clearActions()
            tiltTutorialLabel.addAction(Actions.fadeOut(1f))
        } else if (!player.hasMoved && !tiltTutorialIsActive) {
            tiltTutorialIsActive = true
            tiltTutorialLabel.addAction(Actions.forever(Actions.sequence(
                    Actions.alpha(1f, .5f),
                    Actions.alpha(.5f, .5f)
            )))
        }
    }

    private fun resetTutorial() {
        tiltTutorialTimer = 0f
        tiltTutorialIsActive = false
        tiltTutorialLabel.clearActions()
        tiltTutorialLabel.color.a = 0f
    }

    private fun spawnEnemies(dt: Float) {
        enemyTimer += dt
        if (enemyTimer > enemySpawnInterval) {
            val enemy = Enemy(0f, 0f, mainStage)
            if (!player.hasMoved && !title0.isVisible) { // don't spawn in player shooting field
                if (MathUtils.randomBoolean()) // left
                    enemy.setPosition(MathUtils.random(0f, (width / 2f - player.width / 2f) - enemy.width), height)
                else // right
                    enemy.setPosition(MathUtils.random((width / 2f - player.width / 2f) + enemy.width / 2.3f, width - enemy.width), height)
            } else
                enemy.setPosition(MathUtils.random(0f, width - enemy.width), height)
            enemy.setSpeed(enemySpeed)

            enemyTimer = 0f
            if (player.hasMoved) {
                enemySpawnInterval -= .1f
                enemySpeed += 10
            }

            if (enemySpawnInterval < .5f)
                enemySpawnInterval = .5f

            if (enemySpeed > 400f)
                enemySpeed = 400f
        }
    }
}
