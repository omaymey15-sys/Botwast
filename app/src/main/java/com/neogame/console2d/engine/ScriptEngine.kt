package com.neogame.console2d.engine

import com.neogame.console2d.utils.Logger

class ScriptEngine {

    fun executeScript(script: String, engine: GameEngine) {
        Logger.d("Executing script: $script")

        try {
            when {
                script.contains("move(") -> handleMove(script, engine)
                script.contains("jump()") -> handleJump(engine)
                script.contains("changeScene(") -> handleChangeScene(script, engine)
                script.contains("attack()") -> Logger.d("Attack action executed")
                script.contains("defend()") -> Logger.d("Defend action executed")
                script.contains("shoot()") -> Logger.d("Shoot action executed")
                script.contains("interact()") -> Logger.d("Interact action executed")
                else -> Logger.w("Unknown script: $script")
            }
        } catch (e: Exception) {
            Logger.e("Error executing script: $script", e)
        }
    }

    private fun handleMove(script: String, engine: GameEngine) {
        val regex = """move\((-?\d+),\s*(-?\d+)\)""".toRegex()
        val match = regex.find(script)
        match?.let {
            val x = it.groupValues[1].toFloat()
            val y = it.groupValues[2].toFloat()
            engine.playerX += x
            engine.playerY += y
            Logger.d("Move: x=$x, y=$y")
        }
    }

    private fun handleJump(engine: GameEngine) {
        engine.playerVelY = -15f
        Logger.d("Jump executed")
    }

    private fun handleChangeScene(script: String, engine: GameEngine) {
        val regex = """changeScene\("(.+?)"\)""".toRegex()
        val match = regex.find(script)
        match?.let {
            val sceneName = it.groupValues[1]
            engine.changeScene(sceneName)
            Logger.d("Scene changed to: $sceneName")
        }
    }
}