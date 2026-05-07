package kr.ac.tukorea.ljk.randomunitdefence

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.R
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import android.graphics.RectF
import kotlin.math.sqrt

class Arrow (gctx: GameContext): Sprite(gctx, kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.arrow), IBoxCollidable, IRecyclable {
    override var x = 600f
    override var y = 300f
    var power = 0
    private lateinit var target: Enemy

    init {
        width = Arrow.WIDTH
        height = Arrow.HEIGHT
        setCenter(x, y)
    }

    fun init(startX: Float, startY: Float, power: Int, target: Enemy): Arrow{
        x = startX
        y = startY
        this.power = power
        this.target = target
        syncDstRect()
        return this
    }

    override val collisionRect: RectF
        get() = dstRect

    override fun onRecycle() {

    }

    override fun update(gctx: GameContext) {
        if (!::target.isInitialized) return
        val dx = target.x - x
        val dy = target.y - y
        val distance = sqrt(dx * dx + dy * dy)
        val t = POWER / distance

        x =  x + dx * t
        y =  y + dy * t

        syncDstRect()
        if (x + height / 2f < 0f){
            val scene = gctx.scene as? MainScene ?: return
            scene.world.remove(this, MainScene.Layer.ATTACK)
        }
    }
    fun updateBox(target: Enemy){
        dstRect
    }
    companion object{
        const val WIDTH = 60f
        const val HEIGHT = 30f
        const val POWER = 20f
        var move = false

        fun get(gctx: GameContext, x: Float, y: Float, power:Int, target: Enemy): Arrow {
            val scene = gctx.scene as? MainScene ?: return Arrow(gctx).init(x,y,power, target)
            val arrow = scene.world.obtain(Arrow::class.java) ?: Arrow(gctx)
            return arrow.init(x,y,power, target)
        }

    }

}