package kr.ac.tukorea.ljk.randomunitdefence

import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.R
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable

class Archer (gctx: GameContext): AnimSprite(gctx, kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.archer, 10f, 6), IBoxCollidable{

    private var attackTime = ATTACK_INTERVAL

    init {
        width = Archer.WIDTH
        height = Archer.HEIGHT
        setCenter(move_x, move_y)
    }

    override fun update(gctx: GameContext) {
        super.update(gctx)
        attack(gctx)
        updateCollisionRect()
        syncDstRect()
    }

    override val collisionRect = RectF()

    companion object{
        const val ATTACK_INTERVAL = 0.5f
        const val COLLISION_INSET = 200f
        const val WIDTH = 90f
        const val HEIGHT = 200f
        var move = false
        var move_x = 600f
        var move_y = 400f
    }
    private fun attack(gctx: GameContext){
        attackTime -= gctx.frameTime
        if (attackTime > 0f) return

        attackTime = ATTACK_INTERVAL

        val scene = gctx.scene as? MainScene ?: return
        val power = 200
        val arrow = Arrow.get(gctx, x-60f, y, power)
        scene.world.add(arrow, MainScene.Layer.ATTACK)
    }
    private fun updateCollisionRect(){
        collisionRect.set(dstRect)
        collisionRect.inset(-COLLISION_INSET, -COLLISION_INSET)
    }
}