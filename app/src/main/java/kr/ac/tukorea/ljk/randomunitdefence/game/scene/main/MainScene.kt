package kr.ac.tukorea.ljk.randomunitdefence.game.scene.main

import android.util.Log
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ljk.randomunitdefence.game.objs.tower.Archer
import kr.ac.tukorea.ljk.randomunitdefence.game.objs.tower.Arrow
import kr.ac.tukorea.ljk.randomunitdefence.game.objs.contoller.CollisionChecker
import kr.ac.tukorea.ljk.randomunitdefence.game.objs.tower.RandomTower
import kr.ac.tukorea.ljk.randomunitdefence.game.map.TiledMapLoader
import kr.ac.tukorea.ljk.randomunitdefence.game.objs.bg.TiledBackground
import kr.ac.tukorea.ljk.randomunitdefence.game.objs.contoller.WaveGen
import kr.ac.tukorea.ljk.randomunitdefence.game.layer.MainLayer


class MainScene(gctx: GameContext) : Scene(gctx){
    init {
        val map = TiledMapLoader.load(gctx.view.context.assets, "map/stage1.tmj")
        val layer = map.firstTileLayer()
        Log.d(
            javaClass.simpleName,
            "map=${map.width}x${map.height}, tile=${map.tilewidth}x${map.tileheight}, " +
                    "layer='${layer.name}', data=${layer.data.size}, firstTile=${layer.tileAt(0, 0)}"
        )
    }



    private var draggingArcher: Archer? = null
    override val clipsRect = true
    private var testFliesAdded = false
    override var world = World(MainLayer.entries.toTypedArray()).apply{
        add(
            TiledBackground(gctx, "map/stage1.tmj", tileWidth = 50f, tileHeight = 50f),
            MainLayer.BG,
        )
        add(CollisionChecker(gctx, this), MainLayer.CONTROLLER)
        add(Archer(gctx, type = Archer.Type.RARE), MainLayer.TOWER)
        add(Arrow(gctx), MainLayer.ATTACK)
        add(RandomTower(gctx), MainLayer.TOUCH)
        add(WaveGen(gctx, this),MainLayer.CONTROLLER)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pt = gctx.metrics.fromScreen(event.x,event.y)
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (pt.x in RandomTower.Companion.move_x- RandomTower.Companion.WIDTH/2 .. RandomTower.Companion.move_x + RandomTower.Companion.WIDTH/2 && pt.y in RandomTower.Companion.move_y - RandomTower.Companion.HEIGHT/2 .. RandomTower.Companion.move_y + RandomTower.Companion.HEIGHT/2){
                val newArcher = Archer(gctx, type = Archer.Type.RARE)
                draggingArcher = newArcher
                draggingArcher?.isDrag = true
                draggingArcher?.touch = true

                world.add(newArcher, MainLayer.TOWER)
            }
        }
        if (event.action == MotionEvent.ACTION_MOVE && draggingArcher?.touch == true){
            draggingArcher?.move_x = pt.x
            draggingArcher?.move_y = pt.y
            draggingArcher?.x = pt.x
            draggingArcher?.y = pt.y
        }
        if (event.action == MotionEvent.ACTION_UP){
            draggingArcher?.isDrag = false
            draggingArcher?.touch = false
        }
        return true
    }

}