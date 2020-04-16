package no.sandramoen.koronakablami;

import no.sandramoen.koronakablami.screens.gameplay.LevelScreen
import no.sandramoen.koronakablami.screens.shell.SplashScreen
import no.sandramoen.koronakablami.utils.BaseGame

class KoronaKablamIGame : BaseGame() {
    override fun create() {
        super.create()
        // setActiveScreen(SplashScreen()) // TODO: @release: uncomment this
        setActiveScreen(LevelScreen()) // TODO: @release: comment this
    }
}
