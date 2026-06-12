package kr.ac.tukorea.ljk.randomunitdefence.game.scene.main

import android.graphics.PointF
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

    private val tiledMap = TiledMapLoader.load(gctx.view.context.assets, MAP_ASSET_PATH)
    private val markerLayer = tiledMap.tileLayer(MARKER_LAYER_NAME)

    private var draggingArcher: Archer? = null
    override val clipsRect = true
    private var testFliesAdded = false
    private val touchPoint0 = PointF()
    private val touchPoint1 = PointF()
    private val mapPoint = PointF()
    override var world = World(MainLayer.entries.toTypedArray()).apply{
        add(
            TiledBackground(
                gctx,
                MAP_ASSET_PATH,
            tiledMap,
            tileWidth = TILE_WIDTH,
            tileHeight = TILE_HEIGHT,
        ),
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
                draggingArcher?.move_x = pt.x
                draggingArcher?.move_y = pt.y
                draggingArcher?.x = pt.x
                draggingArcher?.y = pt.y
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
            val archer = draggingArcher ?: return true
            draggingArcher?.isDrag = false
            draggingArcher?.touch = false
            if ( !canInstallAt(pt.x, pt.y) || hasOverlappingArcher(pt.x, pt.y, archer)) {
                world.remove(draggingArcher!!, MainLayer.TOWER)
            }
        }
        return true
    }

    private fun tileCenterX(mapX: Float): Float {
        return (mapX / TILE_WIDTH).toInt() * TILE_WIDTH + TILE_WIDTH / 2f
    }

    private fun tileCenterY(mapY: Float): Float {
        return (mapY / TILE_HEIGHT).toInt() * TILE_HEIGHT + TILE_HEIGHT / 2f
    }

    private fun hasOverlappingArcher(x: Float, y: Float, self: Archer): Boolean {

        val archers = world.objectsAt(MainLayer.TOWER)
        var index = 0
        while (index < archers.size) {
            val archer = archers[index] as? Archer
            if (archer != null && archer != self &&archer.intersectsIfInstalledAt(x, y)) return true
            index++
        }
        return false
    }
    private fun canInstallAt(x: Float, y: Float): Boolean {
        // 올해 버전에서는 Cannon 이 차지하는 2x2 영역을 검사하지 않고,
        // snap 된 중심점이 속한 tile 하나만 Marker layer 에서 확인한다.
        val tileX = (x / TILE_WIDTH).toInt()
        val tileY = (y / TILE_HEIGHT).toInt()
        val gid = markerLayer.tileAt(tileX, tileY)

        return gid != 0
    }
    companion object {
        private const val MAP_ASSET_PATH = "map/stage1.tmj"
        private const val TILE_WIDTH = 50f
        private const val TILE_HEIGHT = 50f
        private const val MARKER_LAYER_NAME = "BuildArea"
    }
}