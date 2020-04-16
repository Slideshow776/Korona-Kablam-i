package no.sandramoen.koronakablami.screens.gameplay

import com.badlogic.gdx.Gdx
import no.sandramoen.koronakablami.actors.Player
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseScreen

class LevelScreen : BaseScreen() {

    var width = 0f // will be initialized upon initialize()
    var height = 0f // will be initialized upon initialize()
    // var ratio = 0f // will be initialized upon initialize()
    lateinit var player: Player

    override fun initialize() {
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height.toFloat()
        // ratio = width / height

        BaseActor.setWorldBounds(width, height)
        player = Player(Gdx.graphics.width / 2f, Gdx.graphics.height * .025f, mainStage)
    }

    override fun update(dt: Float) {
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        player.shoot()
        return false
    }
}