    package kr.ac.tukorea.ljk.randomunitdefence.game.scene.main

    import android.graphics.RectF
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
    import kr.ac.tukorea.ljk.randomunitdefence.game.objs.contoller.Selection
    import kr.ac.tukorea.ljk.randomunitdefence.game.objs.contoller.ArcherMenu
    import kr.ac.tukorea.ljk.randomunitdefence.R

    class MainScene(gctx: GameContext) : Scene(gctx){


        private val tiledMap = TiledMapLoader.load(gctx.view.context.assets, MAP_ASSET_PATH)
        private val markerLayer = tiledMap.tileLayer(MARKER_LAYER_NAME)
        private val selection = Selection(gctx, Archer.SIZE, Archer.SIZE)
        private val archerMenu = ArcherMenu(gctx)
        private val selectionSceneRect = RectF()

        private var draggingArcher: Archer? = null
        private var selectedArcher: Archer? = null
        override val clipsRect = true

        private var isDragging = false
        private var wasMultiTouch = false
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
            add(selection, MainLayer.SELECTOR)
            add(archerMenu, MainLayer.UI)
        }
        init {

            val map = TiledMapLoader.load(gctx.view.context.assets, "map/stage1.tmj")
            val layer = map.firstTileLayer()
            Log.d(
                javaClass.simpleName,
                "map=${map.width}x${map.height}, tile=${map.tilewidth}x${map.tileheight}, " +
                        "layer='${layer.name}', data=${layer.data.size}, firstTile=${layer.tileAt(0, 0)}"
            )
            // GameActivity 에서 기준 좌표계를 1600x900 으로 잡았고,
            // desert.tmj 는 32x18 tile map 이므로 tile 하나를 50x50 으로 그리면 화면을 정확히 채운다.
            archerMenu.onMenuListener = ArcherMenu.OnMenuListener { resId ->
                handleMenuSelection(resId)
            }
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val pt = gctx.metrics.fromScreen(event.x,event.y)
            if (event.action == MotionEvent.ACTION_DOWN) {
                wasMultiTouch = false
                isDragging = false

                if (archerMenu.onTouch(pt.x,pt.y)) {
                    return true
                }
                archerMenu.hide()
                selectedArcher = findArcherAt(pt.x, pt.y)
                if (pt.x in RandomTower.move_x - RandomTower.WIDTH / 2 .. RandomTower.move_x + RandomTower.WIDTH / 2 && pt.y in RandomTower.move_y - RandomTower.HEIGHT / 2 .. RandomTower.move_y
                    + RandomTower.HEIGHT / 2) {
                    val newArcher = Archer(gctx, type = Archer.Type.RARE)
                    draggingArcher = newArcher
                    selectedArcher = null
                    newArcher.isDrag = true
                    newArcher.touch = true
                    newArcher.move_x = pt.x
                    newArcher.move_y = pt.y
                    newArcher.x = pt.x
                    newArcher.y = pt.y
                    world.add(newArcher, MainLayer.TOWER)
                }
            }
            if (event.action == MotionEvent.ACTION_MOVE
                ){
                archerMenu.hide()
                if(draggingArcher?.touch == true){
                draggingArcher?.move_x = pt.x
                draggingArcher?.move_y = pt.y
                draggingArcher?.x = pt.x
                draggingArcher?.y = pt.y
                }
                isDragging = true
                updateSelection(pt.x,pt.y)
            }

            if (event.action == MotionEvent.ACTION_UP) {
                selection.hide()

                val archer = draggingArcher

                if (archer != null) {
                    archer.isDrag = false
                    archer.touch = false

                    val x = tileCenterX(pt.x)
                    val y = tileCenterY(pt.y)

                    if (!canInstallAt(x, y) || hasOverlappingArcher(x, y)) {
                        archerMenu.hide()
                        world.remove(archer, MainLayer.TOWER)
                    } else {
                        archer.x = x
                        archer.y = y
                        archer.move_x = x
                        archer.move_y = y
                        archer.setCenter(x, y)
                    }

                    draggingArcher = null
                    return true
                }

                if (!isDragging && selectedArcher != null) {
                    val selected = selectedArcher!!

                    selection.moveTo(selected.x, selected.y, true)
                    selection.sceneRect(selectionSceneRect)
                    selection.selectedArcher = selected
                    archerMenu.showManageMenuAt(selectionSceneRect)

                    return true
                }

                selectedArcher = null
                archerMenu.hide()
            }
            return true
        }

        private fun tileCenterX(mapX: Float): Float {
            return (mapX / TILE_WIDTH).toInt() * TILE_WIDTH + TILE_WIDTH / 2f
        }

        private fun tileCenterY(mapY: Float): Float {
            return (mapY / TILE_HEIGHT).toInt() * TILE_HEIGHT + TILE_HEIGHT / 2f
        }

        private fun hasOverlappingArcher(x: Float, y: Float): Boolean {

            val archers = world.objectsAt(MainLayer.TOWER)
            var index = 0
            while (index < archers.size-1) {
                val archer = archers[index] as? Archer
                if (archer != null  &&archer.intersectsIfInstalledAt(x, y)) return true
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

        private fun updateSelection(x: Float, y: Float) {
            selection.moveTo(x, y, canInstallAt(x, y) && !hasOverlappingArcher(x, y))
        }
        private fun handleMenuSelection(resId: Int) {
            when (resId) {
                R.mipmap.uninstall -> {
                    selectedArcher?.uninstall()
                    selectedArcher = null
                    selection.hide()
                }
            }
        }

        private fun findArcherAt(x: Float, y: Float): Archer? {
            val archers = world.objectsAt(MainLayer.TOWER)

            var index = archers.size - 1
            while (index >= 0) {
                val archer = archers[index] as? Archer
                if (archer != null && containsPoint(archer, x, y)) {
                    return archer
                }
                index--
            }

            return null
        }
        private fun containsPoint(archer: Archer, x: Float, y: Float): Boolean {
            return x >= archer.x - archer.width / 2f &&
                    x <= archer.x + archer.width / 2f &&
                    y >= archer.y - archer.height / 2f &&
                    y <= archer.y + archer.height / 2f
        }
        companion object {
            private const val MAP_ASSET_PATH = "map/stage1.tmj"
            private const val TILE_WIDTH = 50f
            private const val TILE_HEIGHT = 50f
            private const val MARKER_LAYER_NAME = "BuildArea"
        }
    }