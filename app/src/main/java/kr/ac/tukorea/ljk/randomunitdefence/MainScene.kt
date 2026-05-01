package kr.ac.tukorea.ljk.randomunitdefence

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.HorzScrollBackground
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ljk.randomunitdefence.R
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import android.view.MotionEvent
import kr.ac.tukorea.ljk.randomunitdefence.Archer.Companion.move
import kr.ac.tukorea.ljk.randomunitdefence.Archer.Companion.move_x
import kr.ac.tukorea.ljk.randomunitdefence.Archer.Companion.move_y

enum class Layer{
    BG, TOWER
}
class MainScene(gctx: GameContext) : Scene(gctx){
    override val clipsRect = true
    override val world = World(Layer.entries.toTypedArray()).apply{
        add(HorzScrollBackground(gctx, R.mipmap.tower_bg, 0f), Layer.BG)
        add(Archer(gctx), Layer.TOWER)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pt = gctx.metrics.fromScreen(event.x,event.y)
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (pt.x in Archer.move_x - Archer.WIDTH/2 .. Archer.move_x + Archer.WIDTH/2 && pt.y in Archer.move_y - Archer.HEIGHT/2 .. move_y + Archer.HEIGHT/2){

                Archer.move = true

            }
        }
        if (event.action == MotionEvent.ACTION_MOVE && Archer.move){
            move_x = pt.x
            move_y = pt.y
        }
        if (event.action == MotionEvent.ACTION_UP){
            Archer.move = false
        }
        return true
    }
}