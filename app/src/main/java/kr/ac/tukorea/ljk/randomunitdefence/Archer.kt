package kr.ac.tukorea.ljk.randomunitdefence

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.AnimSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Archer (gctx: GameContext): AnimSprite(gctx, kr.ac.tukorea.ljk.randomunitdefence.R.mipmap.archer, 10f, 6), IBoxCollidable{

    private var attackTime = ATTACK_INTERVAL

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
                enemy = null
            }
        }

        updateCollisionRect()
        syncDstRect()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawCircle(x, y, ATTACK_RADIUS, rangeStrokePaint)

        super.draw(canvas)
    }

    companion object{
        private val rangeStrokePaint = Paint().apply {
            color = Color.argb(120, 0, 255, 0)
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        const val ATTACK_INTERVAL = 0.5f
        const val ATTACK_RADIUS = 300f
        const val WIDTH = 90f
        const val HEIGHT = 200f
        var move = false
        var move_x = 600f
        var move_y = 400f
    }

    fun targetOn(enemy: Enemy) {
        if (isEnemyInAttackRange(enemy)) {
            this.enemy = enemy
        }
    }

    private fun attack(gctx: GameContext, enemy: Enemy) {
        attackTime -= gctx.frameTime

        if (attackTime > 0f) return

        attackTime = ATTACK_INTERVAL

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
        val radiusSq = ATTACK_RADIUS * ATTACK_RADIUS

        return distanceSq <= radiusSq
    }

    private fun updateCollisionRect() {
        collisionRect.set(
            x - ATTACK_RADIUS,
            y - ATTACK_RADIUS,
            x + ATTACK_RADIUS,
            y + ATTACK_RADIUS
        )
    }
}