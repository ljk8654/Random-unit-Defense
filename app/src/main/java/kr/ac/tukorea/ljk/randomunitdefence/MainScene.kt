package kr.ac.tukorea.ljk.randomunitdefence

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.HorzScrollBackground
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import android.view.MotionEvent
import android.util.Log
import kr.ac.tukorea.ljk.randomunitdefence.TiledMapLoader
import kr.ac.tukorea.ljk.randomunitdefence.objs.bg.TiledBackground
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

    enum class Layer{
        BG, TOWER, ATTACK, ENEMY, CONTROLLER, TOUCH
    }
    private val collisionChecker = CollisionChecker(gctx)
    private val originalArcher = Archer(gctx)
    private var draggingArcher: Archer? = null
    override val clipsRect = true
    private var testFliesAdded = false
    override var world = World(Layer.entries.toTypedArray()).apply{
        add(
            TiledBackground(gctx, "map/stage1.tmj", tileWidth = 100f, tileHeight = 100f),
            Layer.BG,
        )
        add(collisionChecker, Layer.CONTROLLER)
        add(originalArcher, Layer.TOWER)
        add(Arrow(gctx), Layer.ATTACK)
        add(RandomTower(gctx), Layer.TOUCH)
    }
    override fun onEnter() {
        if (testFliesAdded) return
        testFliesAdded = true
        addTestFlies()
    }

    private fun addTestFlies() {
        // Fly sprite sheet 가 type 별 frame rect 로 제대로 나뉘는지 확인하기 위한 임시 배치이다.
        // 이후 WaveGen 이 생기면 이 코드는 enemy 생성 로직으로 대체된다.
            val enemy = Enemy.get(gctx)
            enemy.setCenter(500f, 500f)
            world.add(enemy, Layer.ENEMY)
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