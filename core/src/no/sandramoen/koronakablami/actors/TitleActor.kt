package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.koronakablami.utils.BaseActor

class TitleActor(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    var isAnimated = false

    private var vertexShaderCode: String
    private var fragmenterShaderCode: String
    private var shaderProgram: ShaderProgram
    var time = .0f

    init {
        loadImage("title3")
        width = Gdx.graphics.width * .1f
        height = Gdx.graphics.height * .20f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())

        vertexShaderCode = Gdx.files.internal("shaders/default.vs").readString()
        fragmenterShaderCode = Gdx.files.internal("shaders/wave.fs").readString()
        shaderProgram = ShaderProgram(vertexShaderCode, fragmenterShaderCode)
        if (!shaderProgram.isCompiled)
            Gdx.app.error("TitleActor.kt", "Shader compile error: " + shaderProgram.log)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        try {
            if (isAnimated) batch.shader = shaderProgram
            shaderProgram.setUniformf("u_time", time)
            shaderProgram.setUniformf("u_imageSize", Vector2(width, height))
            shaderProgram.setUniformf("u_amplitude", Vector2(.2f, .01f))
            shaderProgram.setUniformf("u_wavelength", Vector2(10f, 1f))
            shaderProgram.setUniformf("u_velocity", Vector2(7f, 1f))
            super.draw(batch, parentAlpha)
            if (isAnimated) batch.shader = null
        } catch (error: Error) {
            super.draw(batch, parentAlpha)
        }
    }

    override fun act(dt: Float) {
        super.act(dt)
        time += dt
    }
}