package no.sandramoen.koronakablami.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.koronakablami.utils.BaseActor
import no.sandramoen.koronakablami.utils.BaseGame

class RNA(x: Float, y: Float, s: Stage) : BaseActor(x, y, s){
    var vertexShaderCode: String
    var fragmenterShaderCode: String
    var shaderProgram: ShaderProgram
    var time = .0f

    init {
        loadImage("rna")
        width = Gdx.graphics.width * .15f
        height = Gdx.graphics.height * .15f * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())

        setSpeed(Gdx.graphics.height / 1f) // pixels / seconds
        setMotionAngle(270f)
        setBoundaryPolygon(8)

        // Actions
        setOrigin(Align.center)
        val spin = Actions.rotateBy(7f, 1f)
        this.addAction(Actions.forever(spin))

        vertexShaderCode = BaseGame.defaultShader.toString()
        fragmenterShaderCode = BaseGame.glowPulseShader.toString()
        shaderProgram = ShaderProgram(vertexShaderCode, fragmenterShaderCode)
        if (!shaderProgram.isCompiled)
            Gdx.app.error("RNA.kt", "Shader compile error: " + shaderProgram.log)
    }

    override fun act(dt: Float) {
        super.act(dt)
        applyPhysics(dt)
        if (y < 0f - height)
            remove()
        time += dt * .5f
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        try {
            batch.shader = shaderProgram
            shaderProgram.setUniformf("u_time", time)
            shaderProgram.setUniformf("u_imageSize", Vector2(width, height))
            shaderProgram.setUniformf("u_glowRadius", .5f)
            super.draw(batch, parentAlpha)
            batch.shader = null
        } catch (error: Error) {
            super.draw(batch, parentAlpha)
        }
    }
}
