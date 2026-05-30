package kr.ac.tukorea.ljk.randomunitdefence

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ljk.randomunitdefence.Enemy
import kr.ac.tukorea.ljk.randomunitdefence.MainScene
import kotlin.random.Random

class WaveGen(
    private val gctx: GameContext,
    private val world: World<MainScene.Layer>,
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
        val x = Random.nextFloat() * gctx.metrics.width
        val y = Random.nextFloat() * gctx.metrics.height
        val enemy = Enemy.get(gctx)
        enemy.setCenter(x, y)
        world.add(enemy, MainScene.Layer.ENEMY)
    }

    //private fun randomType(): .Type {
     //   val types = Fly.Type.entries
      //  return types[Random.nextInt(types.size)]
   // }

    companion object {
        private const val SPAWN_INTERVAL = 1.1f
    }
}
