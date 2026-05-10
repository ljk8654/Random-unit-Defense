package kr.ac.tukorea.ljk.randomunitdefence

import android.graphics.RectF
import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.R
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable

class RandomTower (gctx: GameContext): Sprite(gctx, kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.random), IBoxCollidable{

    init {
        width = RandomTower.WIDTH
        height = RandomTower.HEIGHT
        setCenter(move_x, move_y)
    }

    override val collisionRect = RectF()

    companion object{
        const val WIDTH = 110f
        const val HEIGHT = 200f
        var move = false
        var move_x = 150f
        var move_y = 800f
    }
}