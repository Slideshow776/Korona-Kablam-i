package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame
import java.lang.Error

class EnemyBossTentacles(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private var vertexShaderCode: String
    private var fragmenterShaderCode: String
    private var shaderProgram: ShaderProgram
    private var timeToStartShader = MathUtils.random(0f, 5f)

    var velocityXMultiplier = 1f
    var defeatedMultiplier = 1f
    var speedVariationMultiplier = 1f
    var time = .0f
    var runShader = false
    var attacking = false
    var originalHeight = 0f

    init {
        loadImage("enemy1b")
        width = Gdx.graphics.width * .28f
        height = Gdx.graphics.height * .24f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())

        vertexShaderCode = BaseGame.defaultShader.toString()
        fragmenterShaderCode = BaseGame.waveShader.toString()
        shaderProgram = ShaderProgram(vertexShaderCode, fragmenterShaderCode)
        if (!shaderProgram.isCompiled)
            Gdx.app.error("WaveBackground.kt", "Shader compile error: " + shaderProgram.log)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (runShader) {
            try {
                batch.shader = shaderProgram
                shaderProgram.setUniformf("u_time", time)
                shaderProgram.setUniformf("u_imageSize", Vector2(width, height))
                shaderProgram.setUniformf("u_amplitude", Vector2(1.8f, .01f))
                shaderProgram.setUniformf("u_wavelength", Vector2(100f, 1f))
                shaderProgram.setUniformf("u_velocity", Vector2(15f * velocityXMultiplier * defeatedMultiplier * speedVariationMultiplier, 1f))
                super.draw(batch, parentAlpha)
                batch.shader = null
            } catch (error: Error) {
                super.draw(batch, parentAlpha)
            }
        } else
            super.draw(batch, parentAlpha)
    }

    override fun act(dt: Float) {
        super.act(dt)
        time += dt

        if (time >= timeToStartShader)
            runShader = true

        if (velocityXMultiplier > 1f) velocityXMultiplier -= .8f
        else if (velocityXMultiplier < 1f) velocityXMultiplier = 1f
    }
}