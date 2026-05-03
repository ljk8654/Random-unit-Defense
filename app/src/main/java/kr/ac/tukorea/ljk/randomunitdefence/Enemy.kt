package kr.ac.tukorea.ljk.randomunitdefence

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.R
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import android.view.MotionEvent

class Enemy (gctx: GameContext): AnimSprite(gctx, kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.bat, 10f, 4){

    init {
        width = Enemy.WIDTH
        height = Enemy.HEIGHT
        setCenter(move_x, move_y)
    }

    override fun update(gctx: GameContext) {
        if (x + width / 2f > gctx.metrics.width){
            SPEED *= -1
        }
        if (x - width / 2f < 0){
           SPEED *= -1
        }
        x += SPEED * gctx.frameTime
        syncDstRect()
    }

    companion object{
        var SPEED = 240f

        const val WIDTH = 100f
        const val HEIGHT = 140f
        var move = false
        var move_x = 600f
        var move_y = 600f
    }
}