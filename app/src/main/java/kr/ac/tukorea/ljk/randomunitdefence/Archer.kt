package kr.ac.tukorea.ljk.randomunitdefence

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.R
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import android.view.MotionEvent

class Archer (gctx: GameContext): AnimSprite(gctx, kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.archer, 10f, 6){

    init {
        width = Archer.WIDTH
        height = Archer.HEIGHT
        setCenter(move_x, move_y)
    }

    override fun update(gctx: GameContext) {
        syncDstRect()
    }

    companion object{
        const val WIDTH = 90f
        const val HEIGHT = 200f
        var move = false
        var move_x = 600f
        var move_y = 400f
    }
}