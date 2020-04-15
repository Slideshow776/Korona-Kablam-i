package no.sandramoen.koronakablami.utils

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.InputEvent

class GameUtils {
    companion object {
        fun isTouchDownEvent(e: Event): Boolean { // Custom type checker
            return e is InputEvent && e.type == InputEvent.Type.touchDown
        }

        fun saveGameState() {
            // BaseGame.prefs!!.putInteger("highscore", BaseGame.highscore)
            BaseGame.prefs!!.flush()
        }
    }
}
