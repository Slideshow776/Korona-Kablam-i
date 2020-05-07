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
                BaseGame.prefs!!.putFloat("highScore", BaseGame.highScore)
            } catch (error: Error) {
                BaseGame.prefs!!.putFloat("highScore", Float.MAX_VALUE)
            }
            BaseGame.prefs!!.flush()
        }
    }
}
