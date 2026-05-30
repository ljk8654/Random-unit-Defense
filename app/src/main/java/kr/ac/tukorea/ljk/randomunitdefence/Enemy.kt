package kr.ac.tukorea.ljk.randomunitdefence

import android.graphics.Rect
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.R
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.SheetSprite
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kotlin.random.Random

class Enemy private constructor(gctx: GameContext, resId: Int): AnimSprite(gctx, resId, 10f, 4), IRecyclable,IBoxCollidable{
    enum class Type(
        val health: Float,
        val spawnRate: Int,
        val resId: Int
    ) {
        RED(50f, 10, kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.red),
        BLUE(30f, 20, kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.blue),
        GREEN(20f, 30, kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.green),
        BAT(10f, 40, kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.bat);

        companion object {
            private val totalSpawnRate = entries.sumOf { it.spawnRate }

            fun random(): Type {
                var selectedRate = Random.nextInt(totalSpawnRate)

                // Type 이 자기 spawnRate 규칙을 직접 가진다.
                // spawnRate 를 차례로 빼다가 음수가 되는 지점이 선택된 type 이다.
                // spawnRate 가 0 인 BOSS 는 기본 랜덤 생성에서는 선택되지 않는다.
                // for-in 은 iterator 객체 생성 가능성이 있으므로, 게임 중 자주 불릴 수 있는 곳에서는 index loop 를 쓴다.
                for (i in 0..<entries.size) {
                    val type = entries[i]
                    selectedRate -= type.spawnRate
                    if (selectedRate < 0) {
                        return type
                    }
                }

                return BAT
            }
        }
    }
   init {
        x = 0f
        y = move_y
        width = WIDTH
        height = HEIGHT
        setCenter(x, y)
    }
    var maxHP = 0f
        private set
    var HP = 0f
        private set
    private fun init(type: Type): Enemy {
        x = 0f
        y = move_y

        width = WIDTH
        height = HEIGHT
        setCenter(x, y)

        HP = type.health
        maxHP = HP

        return this
    }
    override val collisionRect: RectF
        get() = dstRect

    override fun update(gctx: GameContext) {
        x += SPEED * gctx.frameTime
        setCenter(x, y)
        if (x - width / 2f > gctx.metrics.width) {
            (gctx.scene as MainScene).world.remove(this, MainScene.Layer.ENEMY)
        }
    }

    override fun onRecycle() {

    }

    companion object{
        fun get(gctx: GameContext): Enemy {
            val world = (gctx.scene as MainScene).world
            val type = Type.random()
            val enemy = world.obtain(Enemy::class.java) ?: Enemy(gctx, type.resId)
            return enemy.init(type)
        }
        var SPEED = 240f

        const val WIDTH = 100f
        const val HEIGHT = 140f
        var move = false
        var move_x = 600f
        var move_y = 600f
        private val rectsArray = listOf(
            listOf(kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.red),
            listOf(kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.blue),
            listOf(kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.green),
            listOf(kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.bat),
        )
    }



}