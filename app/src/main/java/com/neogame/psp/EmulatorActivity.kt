package com.neogame.psp

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.neogame.psp.emulator.GameEngine
import com.neogame.psp.loaders.ZipGameLoader
import com.neogame.psp.renderer.RenderEngine
import com.neogame.psp.ui.GameView
import com.neogame.psp.ui.PSPControllerView
import com.neogame.psp.utils.Logger
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * EmulatorActivity - Écran de jeu
 * Gère l'affichage et les contrôles du jeu
 */
class EmulatorActivity : AppCompatActivity() {

    private lateinit var gameContainer: FrameLayout
    private lateinit var controllerView: PSPControllerView
    private lateinit var gameView: GameView

    private var gameEngine: GameEngine? = null
    private var renderEngine: RenderEngine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emulator)

        val gamePath = intent.getStringExtra("game_path") ?: ""
        initViews()
        loadGame(gamePath)

        Logger.i("EmulatorActivity démarrée")
    }

    private fun initViews() {
        gameContainer = findViewById(R.id.game_container)
        controllerView = findViewById(R.id.controller_view)
        gameView = GameView(this)

        gameContainer.addView(gameView, 0)

        controllerView.setInputCallback { action, value ->
            gameEngine?.handleInput(action, value)
        }
    }

    private fun loadGame(gamePath: String) {
        lifecycleScope.launch(Dispatchers.Default) {
            try {
                val zipLoader = ZipGameLoader(this@EmulatorActivity)
                val gameData = zipLoader.loadGameFromZip(gamePath)

                if (gameData != null) {
                    withContext(Dispatchers.Main) {
                        initializeGame(gameData.config, gameData.gameType)
                    }
                } else {
                    Logger.e("Échec du chargement du jeu")
                    finish()
                }
            } catch (e: Exception) {
                Logger.e("Erreur: ${e.message}")
                finish()
            }
        }
    }

    private fun initializeGame(configJson: com.google.gson.JsonObject, gameType: String) {
        try {
            gameEngine = GameEngine(configJson)
            renderEngine = RenderEngine.create(this, gameType)

            gameView.setGameEngine(gameEngine!!)
            gameView.setRenderEngine(renderEngine!!)
            gameView.startGameLoop()

            Logger.i("Jeu initialisé: $gameType")
        } catch (e: Exception) {
            Logger.e("Erreur init: ${e.message}")
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        gameView.onResume()
    }

    override fun onPause() {
        super.onPause()
        gameView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameEngine?.stop()
        renderEngine?.dispose()
        gameView.stopGameLoop()
    }

    override fun onBackPressed() {
        gameEngine?.stop()
        super.onBackPressed()
    }
}