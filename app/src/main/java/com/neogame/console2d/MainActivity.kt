package com.neogame.console2d

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.neogame.console2d.data.GameRepository
import com.neogame.console2d.utils.Logger

class MainActivity : AppCompatActivity() {

    private lateinit var gameListView: ListView
    private lateinit var refreshButton: Button
    private lateinit var settingsButton: Button
    private val gameRepository = GameRepository()
    private var games: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()
        loadGames()

        Logger.d("MainActivity created")
    }

    private fun initViews() {
        gameListView = findViewById(R.id.gameListView)
        refreshButton = findViewById(R.id.refreshButton)
        settingsButton = findViewById(R.id.settingsButton)
    }

    private fun setupListeners() {
        gameListView.setOnItemClickListener { _, _, position, _ ->
            if (position < games.size) {
                val selectedGame = games[position]
                launchGame(selectedGame)
            }
        }

        refreshButton.setOnClickListener {
            loadGames()
            Toast.makeText(this, "Jeux rafraîchis!", Toast.LENGTH_SHORT).show()
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun loadGames() {
        try {
            games = gameRepository.getAvailableGames(this)
            if (games.isEmpty()) {
                Toast.makeText(this, "Aucun jeu trouvé", Toast.LENGTH_SHORT).show()
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, games)
            gameListView.adapter = adapter
            Logger.d("Loaded ${games.size} games")
        } catch (e: Exception) {
            Logger.e("Error loading games", e)
            Toast.makeText(this, "Erreur lors du chargement", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchGame(gameName: String) {
        try {
            Logger.d("Launching game: $gameName")
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("game_name", gameName)
            startActivity(intent)
        } catch (e: Exception) {
            Logger.e("Error launching game", e)
            Toast.makeText(this, "Impossible de lancer le jeu", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        Logger.d("MainActivity resumed")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d("MainActivity destroyed")
    }
}