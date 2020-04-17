package no.sandramoen.koronakablami.utils

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.InputEvent

class GameUtils {
    companion object {
        fun isTouchDownEvent(e: Event): Boolean { // Custom type checker
            return e is InputEvent && e.type == InputEvent.Type.touchDown
        }

        fun saveGameState() {
            try {
                BaseGame.prefs!!.putLong("highScore", BaseGame.highScore)
            } catch (error: Error) {
                BaseGame.prefs!!.putLong("highScore", 999999999999999999L)
            }
            BaseGame.prefs!!.flush()
        }
    }
}
