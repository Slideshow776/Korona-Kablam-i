package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame

class Engine(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private var vertexShaderCode: String
    private var fragmenterShaderCode: String
    private var shaderProgram: ShaderProgram
    var time = .0f

    init {
        loadImage("player1b")
        width = Gdx.graphics.width * .11f
        height = Gdx.graphics.height * .11f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())

        vertexShaderCode = BaseGame.defaultShader.toString()
        fragmenterShaderCode = BaseGame.waveShader.toString()

        shaderProgram = ShaderProgram(vertexShaderCode, fragmenterShaderCode)
        if (!shaderProgram.isCompiled)
            Gdx.app.error("Engine.kt", "Shader compile error: " + shaderProgram.log)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        try {
            batch.shader = shaderProgram
            shaderProgram.setUniformf("u_time", time)
            shaderProgram.setUniformf("u_imageSize", Vector2(width, height))
            shaderProgram.setUniformf("u_amplitude", Vector2(.65f, .3f))
            shaderProgram.setUniformf("u_wavelength", Vector2(80f, 90f))
            shaderProgram.setUniformf("u_velocity", Vector2(50f, 20f))
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