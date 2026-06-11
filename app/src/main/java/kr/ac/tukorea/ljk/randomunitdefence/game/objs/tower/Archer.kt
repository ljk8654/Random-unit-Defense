package kr.ac.tukorea.ljk.randomunitdefence.game.objs.tower

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ljk.randomunitdefence.R
import kr.ac.tukorea.ljk.randomunitdefence.game.objs.enemy.Enemy
import kr.ac.tukorea.ljk.randomunitdefence.game.scene.main.MainScene

class Archer (gctx: GameContext, private val type: Type = Type.NORMAL): AnimSprite(gctx, type.resId, 0f, 6), IBoxCollidable{

    enum class Type(
        val resId: Int,
        val attackInterval: Float,
        val attackRadius: Float,
        val power: Float,
        val level: Int,
    ) {
        NORMAL(
            R.mipmap.archer,
            0.5f,
            300f,
            10f,
            1,
        ),

        RARE(
            R.mipmap.rare,
            0.4f,
            330f,
            15f,
             2,
        ),
        UNIQUE(
            R.mipmap.unique,
            0.3f,
            400f,
            20f,
            2,
        ),
        LEGEND(
            R.mipmap.legend,
            0.3f,
            500f,
            30f,
            2,
        ),
    }

    private var attackTime = type.attackInterval
    var isDrag = false

    private var enemy: Enemy? = null

    override val collisionRect = RectF()

    var move_x = 600f
    var move_y = 400f

    init {
        width = WIDTH
        height = HEIGHT
        setCenter(move_x, move_y)
        updateCollisionRect()
        syncDstRect()
    }

    override fun update(gctx: GameContext) {
        super.update(gctx)

        val target = enemy
        if (isDrag) return
        if (target == null) return
        if (target.isDead()) {
            enemy = null
            fps = 0f
            return
        }

        if (target != null) {
            if (target.x > x) {
                rightImage()
            } else {
                leftImage()
            }

            if (isEnemyInAttackRange(target)) {
                attack(gctx, target)
            } else {
                fps = 0f
                enemy = null
            }
        }

        updateCollisionRect()
        syncDstRect()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawCircle(x, y, type.attackRadius, rangeStrokePaint)
        updateCollisionRect()
        syncDstRect()
        super.draw(canvas)
    }

    companion object{
        private val rangeStrokePaint = Paint().apply {
            color = Color.argb(120, 0, 255, 0)
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        const val WIDTH = 90f
        const val HEIGHT = 200f
    }

    fun targetOn(enemy: Enemy) {
        if (isDrag) return
        if (isEnemyInAttackRange(enemy)) {
            fps = 5f / type.attackInterval
            this.enemy = enemy
        }
    }

    private fun attack(gctx: GameContext, enemy: Enemy) {
        attackTime -= gctx.frameTime

        if (attackTime > 0f) return

        attackTime = type.attackInterval

        val scene = gctx.scene as? MainScene ?: return
        val arrowPower = type.power

        var arrowX = x
        if (enemy.x > x) {
            arrowX = x + 60f
        } else {
            arrowX = x - 60f
        }

        val arrow = Arrow.get(gctx, arrowX, y, arrowPower,enemy)
        scene.world.add(arrow, MainScene.Layer.ATTACK)
    }

    private fun isEnemyInAttackRange(enemy: Enemy): Boolean {
        val dx = enemy.x - x
        val dy = enemy.y - y

        val distanceSq = dx * dx + dy * dy
        val radiusSq = type.attackRadius * type.attackRadius

        return distanceSq <= radiusSq
    }

    private fun updateCollisionRect() {
        collisionRect.set(
            x - type.attackRadius,
            y - type.attackRadius,
            x + type.attackRadius,
            y + type.attackRadius
        )
    }
}