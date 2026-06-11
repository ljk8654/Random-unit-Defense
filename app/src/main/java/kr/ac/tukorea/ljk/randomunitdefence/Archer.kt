package kr.ac.tukorea.ljk.randomunitdefence

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.random.Random

class Archer (gctx: GameContext, private val type: Type = Type.NORMAL): AnimSprite(gctx, type.resId, 0f, 6), IBoxCollidable{

    enum class Type(
        val resId: Int,
        val attackInterval: Float,
        val attackRadius: Float,
        val power: Int,
    ) {
        NORMAL(
            kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.archer,
            0.5f,
            300f,
            200,
        ),

        RARE(
            kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.rare,
            0.4f,
            330f,
            260,
        ),
    }

    private var attackTime = type.attackInterval

    private var enemy: Enemy? = null

    override val collisionRect = RectF()

    init {
        width = WIDTH
        height = HEIGHT
        setCenter(move_x, move_y)

        updateCollisionRect()
    }

    override fun update(gctx: GameContext) {
        super.update(gctx)

        val target = enemy
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
        var move = false
        var move_x = 600f
        var move_y = 400f
    }

    fun targetOn(enemy: Enemy) {
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
        val power = 200

        var arrowX = x
        if (enemy.x > x) {
            arrowX = x + 60f
        } else {
            arrowX = x - 60f
        }

        val arrow = Arrow.get(gctx, arrowX, y, power, enemy)
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