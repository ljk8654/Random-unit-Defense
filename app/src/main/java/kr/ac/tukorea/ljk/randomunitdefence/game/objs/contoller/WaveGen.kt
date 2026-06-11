package kr.ac.tukorea.ljk.randomunitdefence.game.objs.contoller

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ljk.randomunitdefence.game.objs.enemy.Enemy
import kr.ac.tukorea.ljk.randomunitdefence.game.layer.MainLayer
import kotlin.random.Random

class WaveGen(
    private val gctx: GameContext,
    private val world: World<MainLayer>,
) : IGameObject {
    private var elapsedTime = 0f

    override fun update(gctx: GameContext) {
        elapsedTime += gctx.frameTime
        if (elapsedTime < SPAWN_INTERVAL) return

        elapsedTime -= SPAWN_INTERVAL
        spawn()
    }

    override fun draw(canvas: Canvas) {
    }

    private fun spawn() {
        val y = Random.Default.nextFloat() * gctx.metrics.height
        val enemy = Enemy.Companion.get(gctx)
        enemy.setCenter(0f, y)
        world.add(enemy, MainLayer.ENEMY)
    }


    companion object {
        private const val SPAWN_INTERVAL = 1.1f
    }
}