package kr.ac.tukorea.ljk.randomunitdefence.game.objs.tower

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import android.graphics.RectF
import kr.ac.tukorea.ljk.randomunitdefence.R
import kr.ac.tukorea.ljk.randomunitdefence.game.objs.enemy.Enemy
import kr.ac.tukorea.ljk.randomunitdefence.game.layer.MainLayer
import kr.ac.tukorea.ljk.randomunitdefence.game.layer.mainWorld
import kotlin.math.sqrt

class Arrow (gctx: GameContext): Sprite(gctx, R.mipmap.arrow), IBoxCollidable, IRecyclable {
    override var x = 600f
    override var y = 300f
    var power = 0f
        private set
    val splashes: Boolean
        get() = power >= SPLASH_MIN_POWER
    val explosionRadius: Float
        get() = BASE_EXPLOSION_RADIUS + EXPLOSION_RADIUS_POWER_RATIO * power
    private lateinit var target: Enemy

    init {
        width = Arrow.WIDTH
        height = Arrow.HEIGHT
        setCenter(x, y)
    }

    fun init(startX: Float, startY: Float,power: Float, target: Enemy): Arrow{
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
        val t = power / distance

        x =  x + dx * t
        y =  y + dy * t

        syncDstRect()
        if (x + height / 2f < 0f){
            gctx.mainWorld().remove(this, MainLayer.ATTACK)
        }
    }
    fun updateBox(target: Enemy){
        dstRect
    }
    companion object{
        const val WIDTH = 60f
        const val HEIGHT = 30f
        const val BASE_POWER = 20f
        var move = false

        fun get(gctx: GameContext, x: Float, y: Float, power:Float, target: Enemy): Arrow {
            val arrow =gctx.mainWorld().obtain(Arrow::class.java) ?: Arrow(gctx)
            return arrow.init(x,y,power, target)
        }
        private const val SPLASH_MIN_POWER = 30f
        private const val BASE_EXPLOSION_RADIUS = 60f
        private const val EXPLOSION_RADIUS_POWER_RATIO = 3f

    }

}