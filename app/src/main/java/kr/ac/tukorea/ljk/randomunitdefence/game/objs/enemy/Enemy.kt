package kr.ac.tukorea.ljk.randomunitdefence.game.objs.enemy

import android.graphics.Canvas
import android.graphics.RectF
import android.util.Log
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withRotation
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.util.Gauge
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ljk.randomunitdefence.R
import kr.ac.tukorea.ljk.randomunitdefence.game.layer.MainLayer
import kr.ac.tukorea.ljk.randomunitdefence.game.layer.mainWorld
import kotlin.random.Random

class Enemy private constructor(gctx: GameContext, resId: Int): AnimSprite(gctx, resId, 10f, 4),
    IRecyclable, IBoxCollidable {
    enum class Type(
        val health: Float,
        val spawnRate: Int,
        val resId: Int
    ) {
        RED(50f, 10, R.mipmap.red),
        BLUE(30f, 20, R.mipmap.blue),
        GREEN(20f, 30, R.mipmap.green),
        BAT(10f, 40, R.mipmap.bat);

        companion object {
            private val totalSpawnRate = entries.sumOf { it.spawnRate }

            fun random(): Type {
                var selectedRate = Random.Default.nextInt(totalSpawnRate)

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
    private var displayHP= 0f
    var maxHP = 0f
        private set
    var HP = 0f
        private set
    private var angle = 0f
    private fun init(type: Type): Enemy {
        x = 0f
        y = move_y

        width = WIDTH
        height = HEIGHT
        setCenter(x, y)

        HP = type.health
        maxHP = HP
        displayHP = HP
        Log.d("Enemy", "Spawn imageType=$type HP=$HP")
        return this
    }
    override val collisionRect: RectF
        get() = dstRect

    override fun update(gctx: GameContext) {
        updateDisplayHP()
        x += SPEED * gctx.frameTime
        setCenter(x, y)
        if (x - width / 2f > gctx.metrics.width) {
            gctx.mainWorld().remove(this, MainLayer.ENEMY)
        }
    }

    override fun onRecycle() {

    }
    override fun draw(canvas: Canvas) {
        // withRotation 은 아래 save/rotate/restore 패턴을 보기 좋게 감싼 AndroidX KTX helper 이다.
        // canvas.save()
        // canvas.rotate(angle, x, y)
        // super.draw(canvas)
        // canvas.restore()
            canvas.withRotation(angle, x, y) {
            super.draw(canvas)
        }

        // Gauge 는 색/두께만 가진 stateless drawing helper 이다.
        // Fly 마다 Gauge 를 만들면 적이 생성될 때마다 Paint 객체도 같이 생기므로,
        // companion object 의 lifeGauge 하나를 모든 Fly 가 공유하고 progress 만 넘긴다.
        val barSize = width * LIFE_GAUGE_WIDTH_RATIO


        lifeGauge.draw(
            canvas,
            x - barSize / 2f,
            y + barSize / 2f,
            barSize,
            displayHP / maxHP,
        )
    }
    fun decreaseLife(amount: Float) {
        HP -= amount
    }

    fun isDead(): Boolean {
        return HP <= 0f
    }
    private fun updateDisplayHP() {
        if (HP == displayHP) return

        val step = maxHP / HP_GAUGE_ANIMATION_STEP_COUNT
        val diff = HP - displayHP
        displayHP += when {
            diff < -step -> -step
            diff > step -> step
            else -> diff
        }
    }
    companion object{
        fun get(gctx: GameContext): Enemy {
            val type = Type.random()
            return Enemy(gctx, type.resId).init(type)
        }
        var SPEED = 240f
        const val WIDTH = 100f
        const val HEIGHT = 140f
        var move = false
        var move_x = 600f
        var move_y = 600f
        private val rectsArray = listOf(
            listOf(R.mipmap.red),
            listOf(R.mipmap.blue),
            listOf(R.mipmap.green),
            listOf(R.mipmap.bat),
        )
        private const val LIFE_GAUGE_THICKNESS = 0.2f
        private const val LIFE_GAUGE_WIDTH_RATIO = 2f / 3f
        private const val HP_GAUGE_ANIMATION_STEP_COUNT = 25f
        private val lifeGauge = Gauge(
            thickness = LIFE_GAUGE_THICKNESS,
            fgColor = "#C9786400".toColorInt(),
            bgColor = "#B5FFD7D5".toColorInt(),
        )
    }

}