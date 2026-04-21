package com.neogame.psp

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipFile

class MainActivity : AppCompatActivity() {
    
    private lateinit var gamesRecycler: RecyclerView
    private lateinit var gameAdapter: GameGridAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var btnAddGame: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gamesRecycler = findViewById(R.id.games_grid)
        progressBar = findViewById(R.id.progress_loading)
        btnAddGame = findViewById(R.id.btn_add_game)
        val txtTitle = findViewById<TextView>(R.id.txt_title)

        txtTitle.text = "🎮 ConsolePSP"

        gameAdapter = GameGridAdapter(emptyList()) { gamePath ->
            val intent = Intent(this, EmulatorActivity::class.java)
            intent.putExtra("game_path", gamePath)
            startActivity(intent)
        }

        gamesRecycler.layoutManager = GridLayoutManager(this, 2)
        gamesRecycler.adapter = gameAdapter

        btnAddGame.setOnClickListener {
            startActivityForResult(Intent(this, AddGameActivity::class.java), 200)
        }

        scanGames()
        Logger.i("MainActivity created")
    }

    private fun scanGames() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.Default) {
            val games = mutableListOf<String>()
            val gamesDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "ConsolePSP"
            )

            if (gamesDir.exists()) {
                gamesDir.listFiles()?.forEach { file ->
                    if (file.extension == "zip") {
                        try {
                            val zipFile = ZipFile(file)
                            if (zipFile.getEntry("game.json") != null) {
                                games.add(file.absolutePath)
                            }
                            zipFile.close()
                        } catch (e: Exception) {
                            Logger.e("Scan error: ${e.message}")
                        }
                    }
                }
            }

            withContext(Dispatchers.Main) {
                gameAdapter.setGames(games)
                progressBar.visibility = View.GONE
                Logger.i("Found ${games.size} games")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200) scanGames()
    }

    override fun onResume() {
        super.onResume()
        scanGames()
    }
}