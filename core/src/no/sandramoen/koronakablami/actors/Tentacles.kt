package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame
import java.lang.Error

class Tentacles(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private var vertexShaderCode: String
    private var fragmenterShaderCode: String
    private var shaderProgram: ShaderProgram
    var time = .0f

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
        try {
            batch.shader = shaderProgram
            shaderProgram.setUniformf("u_time", time)
            shaderProgram.setUniformf("u_imageSize", Vector2(width, height))
            shaderProgram.setUniformf("u_amplitude", Vector2(.6f, 1.5f))
            shaderProgram.setUniformf("u_wavelength", Vector2(250f, 250f))
            shaderProgram.setUniformf("u_velocity", Vector2(75f, 75f))
            super.draw(batch, parentAlpha)
            batch.shader = null
        } catch (error: Error) {
            super.draw(batch, parentAlpha)
        }
    }

    override fun act(dt: Float) {
        super.act(dt)
        time += dt
    }
}