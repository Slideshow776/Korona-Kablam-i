package no.sandramoen.koronakablami.screens.gameplay

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import no.sandramoen.koronakablami.actors.Laser
import no.sandramoen.koronakablami.actors.Player
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseScreen

class LevelScreen : BaseScreen() {

    var width = 0f // will be initialized upon initialize()
    var height = 0f // will be initialized upon initialize()

    // var ratio = 0f // will be initialized upon initialize()
    lateinit var player: Player

    private var mayShoot = true

    override fun initialize() {
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height.toFloat()
        // ratio = width / height

        BaseActor.setWorldBounds(width, height)
        player = Player(Gdx.graphics.width / 2f, Gdx.graphics.height * .025f, mainStage)
    }

    override fun update(dt: Float) {
        mayShoot = BaseActor.count(mainStage, Laser::class.java.canonicalName) <= 2
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.SPACE && mayShoot)
            player.shoot()
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (mayShoot)
            player.shoot()
        return false
    }
}