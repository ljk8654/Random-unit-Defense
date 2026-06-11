package kr.ac.tukorea.ljk.randomunitdefence.game.objs.tower

import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ljk.randomunitdefence.R

class RandomTower (gctx: GameContext): Sprite(gctx, R.mipmap.random), IBoxCollidable {

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