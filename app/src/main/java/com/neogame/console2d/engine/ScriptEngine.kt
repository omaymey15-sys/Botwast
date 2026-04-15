package com.neogame.console2d.engine

import android.util.Log

class ScriptEngine {

    fun executeScript(script: String, engine: GameEngine) {
        Log.d("ScriptEngine", "Executing: $script")

        when {
            script.contains("move(") -> {
                val regex = """move\((-?\d+),\s*(-?\d+)\)""".toRegex()
                val match = regex.find(script)
                match?.let {
                    val x = it.groupValues[1].toFloat()
                    val y = it.groupValues[2].toFloat()
                    engine.playerX += x
                    engine.playerY += y
                }
            }

            script.contains("jump()") -> {
                engine.playerY -= 100
            }

            script.contains("changeScene(") -> {
                val regex = """changeScene\("(.+?)"\)""".toRegex()
                val match = regex.find(script)
                match?.let {
                    val sceneName = it.groupValues[1]
                    engine.changeScene(sceneName)
                }
            }

            script.contains("openMenu()") -> {
                Log.d("ScriptEngine", "Menu opened")
            }

            else -> {
                Log.d("ScriptEngine", "Unknown script: $script")
            }
        }
    }
}