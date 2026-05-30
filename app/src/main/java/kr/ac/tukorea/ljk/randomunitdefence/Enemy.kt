package kr.ac.tukorea.ljk.randomunitdefence

import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.R
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.SheetSprite
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable

class Enemy private constructor(gctx: GameContext): AnimSprite(gctx, kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.bat, 10f, 4), IRecyclable,IBoxCollidable{

    init {
        width = Enemy.WIDTH
        height = Enemy.HEIGHT
        setCenter(move_x, move_y)
    }

    override val collisionRect: RectF
        get() = dstRect

    override fun update(gctx: GameContext) {
        if (x + width / 2f > gctx.metrics.width){
            SPEED *= -1
            leftImage()
        }
        if (x - width / 2f < 0){
           SPEED *= -1
            rightImage()
        }
        x += SPEED * gctx.frameTime
        syncDstRect()
    }

    override fun onRecycle() {
        TODO("Not yet implemented")
    }

    companion object{
        fun get(gctx: GameContext): Enemy {
            val scene = gctx.scene as? MainScene ?: return Enemy(gctx)
            val enemy = scene.world.obtain(Enemy::class.java) ?: Enemy(gctx)
            return enemy
        }
        var SPEED = 240f

        const val WIDTH = 100f
        const val HEIGHT = 140f
        var move = false
        var move_x = 600f
        var move_y = 600f
    }



}