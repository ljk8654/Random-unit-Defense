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
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Button


class MainScene(gctx: GameContext) : Scene(gctx){
    enum class Layer{
        BG, TOWER, ATTACK, ENEMY, CONTROLLER, TOUCH
    }
    private val collisionChecker = CollisionChecker(gctx)
    private val originalArcher = Archer(gctx)
    private var draggingArcher: Archer? = null
    override val clipsRect = true
    override var world = World(Layer.entries.toTypedArray()).apply{
        add(HorzScrollBackground(gctx, R.mipmap.tower_bg, 0f), Layer.BG)
        add(collisionChecker, Layer.CONTROLLER)
        add(originalArcher, Layer.TOWER)
        add(Enemy(gctx), Layer.ENEMY)
        add(Arrow(gctx), Layer.ATTACK)
        add(RandomTower(gctx), Layer.TOUCH)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pt = gctx.metrics.fromScreen(event.x,event.y)
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (pt.x in RandomTower.move_x- RandomTower.WIDTH/2 .. RandomTower.move_x + RandomTower.WIDTH/2 && pt.y in RandomTower.move_y - RandomTower.HEIGHT/2 .. RandomTower.move_y + RandomTower.HEIGHT/2){
                val newArcher = Archer(gctx)
                draggingArcher = newArcher
                draggingArcher?.touch = true
                world.add(newArcher, Layer.TOWER)
            }
        }
        if (event.action == MotionEvent.ACTION_MOVE && draggingArcher?.touch == true){
            draggingArcher?.x = pt.x
            draggingArcher?.y = pt.y
        }
        if (event.action == MotionEvent.ACTION_UP){
            draggingArcher?.touch = false
        }
        return true
    }
}