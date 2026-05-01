package kr.ac.tukorea.ljk.randomunitdefence

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.HorzScrollBackground
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ljk.randomunitdefence.R

class MainScene(gctx: GameContext) : Scene(gctx){
    override val clipsRect = true
    override val world = World(arrayOf(0)).apply{
        add(HorzScrollBackground(gctx, R.mipmap.tower_bg, 0f),0)
    }
}