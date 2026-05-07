package kr.ac.tukorea.ljk.randomunitdefence

import android.graphics.Canvas
import android.util.Log
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.collidesWith
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class CollisionChecker(private val gctx: GameContext) : IGameObject {
    override fun update(gctx: GameContext) {
        val scene = gctx.scene as? MainScene ?: return

        // 바깥쪽 Enemy 와 안쪽 Bullet 을 모두 forEachReversedAt() 로 뒤에서 앞으로 돈다.
        // 그러면 충돌한 Bullet 이나 Enemy 를 즉시 remove() 해도
        // 각 layer 안의 아직 방문하지 않은 앞쪽 객체들을 계속 안전하게 볼 수 있다.
        // 그리고 forEachReversedAt() 는 지금 inline 함수이므로,
        // helper 호출 형태로 써도 별도 함수/람다 객체가 추가로 생기지 않고
        // 호출 위치에 그대로 펴진다고 생각하면 된다.
        scene.world.forEachReversedAt(MainScene.Layer.ENEMY) { enemyObject ->
            val enemy = enemyObject as? Enemy ?: return@forEachReversedAt

            scene.world.forEachReversedAt(MainScene.Layer.TOWER) { towerObject ->
                val archer = towerObject as? Archer ?: return@forEachReversedAt

                if (archer.collidesWith(enemy)) {
                    archer.targetOn(enemy)

                }
            }

            scene.world.forEachReversedAt(MainScene.Layer.ATTACK) { arrowObject ->
                val arrow = arrowObject as? Arrow ?: return@forEachReversedAt

                if (arrow.collidesWith(enemy)) {
                    Log.v(javaClass.simpleName, "Collision !! Enemy( x=${enemy.x}, y=${enemy.y})")
                    scene.world.remove(arrow, MainScene.Layer.ATTACK)
                }
            }
        }
    }

    override fun draw(canvas: Canvas) {
        // collisionRect 디버그 표시는 World.draw() 가 전체 IBoxCollidable 객체를 훑으며 맡는다.
    }
}
