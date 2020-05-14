package no.sandramoen.koronakablami.utils

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetErrorListener
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable


abstract class BaseGame : Game(), AssetErrorListener {
    init {
        game = this
    }

    companion object {
        private var game: BaseGame? = null

        lateinit var assetManager: AssetManager
        lateinit var fontGenerator: FreeTypeFontGenerator

        // game assets
        var labelStyle: LabelStyle? = null
        var textButtonStyle: TextButtonStyle? = null
        var textureAtlas: TextureAtlas? = null
        var splashAnim: Animation<TextureRegion>? = null
        var splashTexture: Texture? = null
        var defaultShader: String? = null
        var glowPulseShader: String? = null
        var shockwaveShader: String? = null
        var waveShader: String? = null

        // game state
        var prefs: Preferences? = null
        var highScore: Float = 0f
        var levelMusic: Music? = null
        var laserShotSound: Sound? = null
        var explosionsSound: Sound? = null
        var pickupSound: Sound? = null
        var newHighScoreSound: Sound? = null
        var screamSound: Sound? = null
        var bossAppearSound: Sound? = null
        var bossHurtSound: Sound? = null
        var bossDefeatedSound: Sound? = null
        var hitShieldSound: Sound? = null
        var audioVolume = .25f
        var miss = false
        var gameOver = true

        fun setActiveScreen(s: BaseScreen) {
            game?.setScreen(s)
        }
    }

    override fun create() {
        Gdx.input.inputProcessor = InputMultiplexer() // discrete input

        // global variables
        prefs = Gdx.app.getPreferences("koronakablamiGameState")
        highScore = prefs!!.getFloat("highScore")

        // asset manager
        assetManager = AssetManager()
        assetManager.setErrorListener(this)
        // assetManager.load("audio/***.wav", Music::class.java)
        assetManager.load("images/included/packed/koronakablami.pack.atlas", TextureAtlas::class.java)
        assetManager.load("audio/331876__furbyguy__idunnometloop.wav", Music::class.java)
        assetManager.load("audio/Laser_Shoot3.wav", Sound::class.java)
        assetManager.load("audio/Explosion15.wav", Sound::class.java)
        assetManager.load("audio/Pickup_Coin8.wav", Sound::class.java)
        assetManager.load("audio/Powerup12.wav", Sound::class.java)
        assetManager.load("audio/Explosion20.wav", Sound::class.java)
        assetManager.load("audio/Hit_Hurt47.wav", Sound::class.java)
        assetManager.load("audio/Hit_Hurt48.wav", Sound::class.java)
        assetManager.load("audio/Hit_Hurt49.wav", Sound::class.java)
        assetManager.load("audio/Hit_Hurt52.wav", Sound::class.java)
        val resolver = InternalFileHandleResolver()
        assetManager.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
        assetManager.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))
        assetManager.setLoader(Text::class.java, TextLoader(InternalFileHandleResolver()))

        assetManager.load(AssetDescriptor("shaders/default.vs", Text::class.java, TextLoader.TextParameter()))
        assetManager.load(AssetDescriptor("shaders/glow-pulse.fs", Text::class.java, TextLoader.TextParameter()))
        assetManager.load(AssetDescriptor("shaders/shockwave.fs", Text::class.java, TextLoader.TextParameter()))
        assetManager.load(AssetDescriptor("shaders/wave.fs", Text::class.java, TextLoader.TextParameter()))
        assetManager.finishLoading()

        textureAtlas = assetManager.get("images/included/packed/koronakablami.pack.atlas") // all images are found in this global static variable

        // images that are excluded from the asset manager
        splashTexture = Texture(Gdx.files.internal("images/excluded/splash.jpg"))
        splashTexture!!.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
        splashAnim = Animation(1f, TextureRegion(splashTexture))

        // audio
        levelMusic = assetManager.get("audio/331876__furbyguy__idunnometloop.wav", Music::class.java)
        laserShotSound = assetManager.get("audio/Laser_Shoot3.wav", Sound::class.java)
        explosionsSound = assetManager.get("audio/Explosion15.wav", Sound::class.java)
        pickupSound = assetManager.get("audio/Pickup_Coin8.wav", Sound::class.java)
        newHighScoreSound = assetManager.get("audio/Powerup12.wav", Sound::class.java)
        screamSound = assetManager.get("audio/Explosion20.wav", Sound::class.java)
        bossDefeatedSound = assetManager.get("audio/Hit_Hurt47.wav", Sound::class.java)
        bossHurtSound = assetManager.get("audio/Hit_Hurt48.wav", Sound::class.java)
        bossAppearSound = assetManager.get("audio/Hit_Hurt49.wav", Sound::class.java)
        hitShieldSound = assetManager.get("audio/Hit_Hurt52.wav", Sound::class.java)

        // text files
        defaultShader = assetManager.get("shaders/default.vs", Text::class.java).getString()
        glowPulseShader = assetManager.get("shaders/glow-pulse.fs", Text::class.java).getString()
        shockwaveShader = assetManager.get("shaders/shockwave.fs", Text::class.java).getString()
        waveShader = assetManager.get("shaders/wave.fs", Text::class.java).getString()

        // fonts
        FreeTypeFontGenerator.setMaxTextureSize(2048) // solves font bug that won't show some characters like "." and "," in android
        val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("fonts/arcade.ttf"))
        val fontParameters = FreeTypeFontParameter()
        fontParameters.size = (.038f * Gdx.graphics.height).toInt() // Font size is based on width of screen...
        fontParameters.color = Color.WHITE
        fontParameters.borderWidth = 2f
        fontParameters.borderColor = Color.BLACK
        fontParameters.borderStraight = true
        fontParameters.minFilter = TextureFilter.Linear
        fontParameters.magFilter = TextureFilter.Linear
        val customFont = fontGenerator.generateFont(fontParameters)

        val buttonFontParameters = FreeTypeFontParameter()
        buttonFontParameters.size = (.04f * Gdx.graphics.height).toInt() // If the resolutions height is 1440 then the font size becomes 86
        buttonFontParameters.color = Color.WHITE
        buttonFontParameters.borderWidth = 2f
        buttonFontParameters.borderColor = Color.BLACK
        buttonFontParameters.borderStraight = true
        buttonFontParameters.minFilter = TextureFilter.Linear
        buttonFontParameters.magFilter = TextureFilter.Linear
        val buttonCustomFont = fontGenerator.generateFont(buttonFontParameters)

        labelStyle = LabelStyle()
        labelStyle!!.font = customFont

        textButtonStyle = TextButtonStyle()
        val buttonTex = textureAtlas!!.findRegion("button")
        val buttonPatch = NinePatch(buttonTex, 24, 24, 24, 24)
        textButtonStyle!!.up = NinePatchDrawable(buttonPatch)
        textButtonStyle!!.font = buttonCustomFont
        textButtonStyle!!.fontColor = Color.WHITE
    }

    override fun dispose() {
        super.dispose()

        assetManager.dispose()
        fontGenerator.dispose()
        /*try { // TODO: uncomment this when development is done
            assetManager.dispose()
            fontGenerator.dispose()
        } catch (error: UninitializedPropertyAccessException) {
            Gdx.app.error("BaseGame", "Error $error")
        }*/
    }

    override fun error(asset: AssetDescriptor<*>, throwable: Throwable) {
        Gdx.app.error("BaseGame.kt", "Could not load asset: " + asset.fileName, throwable)
    }
}
