package com.neogame.psp

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonParser
import java.util.zip.ZipFile
import java.io.File

class EmulatorActivity : AppCompatActivity() {

    private lateinit var gameContainer: FrameLayout
    private lateinit var controllerView: PSPControllerView
    private var gameView: GameView? = null
    private var renderer: BaseRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emulator)

        gameContainer = findViewById(R.id.game_container)
        controllerView = findViewById(R.id.controller_view)

        val gamePath = intent.getStringExtra("game_path") ?: ""
        
        try {
            val zipFile = ZipFile(File(gamePath))
            val configEntry = zipFile.getEntry("game.json") ?: throw Exception("game.json not found")
            val configJson = zipFile.getInputStream(configEntry).readBytes().decodeToString()
            val gameConfig = JsonParser.parseString(configJson).asJsonObject
            
            val gameType = gameConfig.get("metadata")?.asJsonObject?.get("type")?.asString ?: "2D"
            Logger.i("Game Type: $gameType")
            
            val gameEngine = GameEngine(gameConfig)
            
            // ⭐ Créer le bon renderer selon le type
            when (gameType.uppercase()) {
                "2D" -> {
                    gameView = GameView(this)
                    renderer = Renderer2D()
                    gameContainer.addView(gameView)
                }
                "3D" -> {
                    gameView = GameView(this)
                    renderer = Renderer3D(this)
                    gameContainer.addView(gameView)
                }
                "4D" -> {
                    gameView = GameView(this)
                    renderer = Renderer4D(this)
                    gameContainer.addView(gameView)
                }
                else -> {
                    gameView = GameView(this)
                    renderer = Renderer2D()
                    gameContainer.addView(gameView)
                }
            }
            
            gameView?.setGameEngine(gameEngine)
            gameView?.setRenderer(renderer!!)
            gameView?.startGameLoop()
            
            controllerView.setInputCallback { action, _ ->
                gameEngine.handleInput(action, 1f)
            }
            
            zipFile.close()
            Logger.i("Game loaded: $gameType")
        } catch (e: Exception) {
            Logger.e("Load error: ${e.message}")
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        gameView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        gameView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameView?.stopGameLoop()
        renderer?.dispose()
    }
}