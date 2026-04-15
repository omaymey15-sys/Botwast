package com.neogame.console2d

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.neogame.console2d.data.GameRepository

class MainActivity : AppCompatActivity() {

    private lateinit var gameListView: ListView
    private lateinit var refreshButton: Button
    private val gameRepository = GameRepository()
    private var games: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameListView = findViewById(R.id.gameListView)
        refreshButton = findViewById(R.id.refreshButton)

        loadGames()

        gameListView.setOnItemClickListener { _, _, position, _ ->
            val selectedGame = games[position]
            launchGame(selectedGame)
        }

        refreshButton.setOnClickListener {
            loadGames()
        }
    }

    private fun loadGames() {
        games = gameRepository.getAvailableGames(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, games)
        gameListView.adapter = adapter
    }

    private fun launchGame(gameName: String) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("game_name", gameName)
        startActivity(intent)
    }
}