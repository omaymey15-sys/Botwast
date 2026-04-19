package com.neogame.psp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neogame.psp.adapter.GameGridAdapter
import com.neogame.psp.storage.GameManager
import com.neogame.psp.utils.Constants
import com.neogame.psp.utils.FileUtils
import com.neogame.psp.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * MainActivity - Menu principal
 * Affiche la grille de jeux disponibles
 */
class MainActivity : AppCompatActivity() {

    private lateinit var gamesRecycler: RecyclerView
    private lateinit var gameAdapter: GameGridAdapter
    private lateinit var gameManager: GameManager
    private lateinit var progressBar: ProgressBar
    private lateinit var btnAddGame: Button
    private lateinit var txtTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameManager = GameManager(this)
        initViews()
        setupRecycler()
        requestStoragePermissions()
        scanGames()

        Logger.i("MainActivity created - Console PSP")
    }

    private fun initViews() {
        gamesRecycler = findViewById(R.id.games_grid)
        progressBar = findViewById(R.id.progress_loading)
        btnAddGame = findViewById(R.id.btn_add_game)
        txtTitle = findViewById(R.id.txt_title)

        txtTitle.text = "🎮 ${Constants.APP_NAME}"
        txtTitle.textSize = 28f
        
        btnAddGame.apply {
            text = "➕ Add Game"
            textSize = 14f
            background = ContextCompat.getDrawable(this@MainActivity, R.drawable.button_a)
            setOnClickListener {
                startActivityForResult(Intent(this@MainActivity, AddGameActivity::class.java), 200)
            }
        }
    }

    private fun setupRecycler() {
        gamesRecycler.layoutManager = GridLayoutManager(this, 2)
        gameAdapter = GameGridAdapter(emptyList()) { gamePath ->
            val intent = Intent(this, EmulatorActivity::class.java)
            intent.putExtra("game_path", gamePath)
            startActivity(intent)
        }
        gamesRecycler.adapter = gameAdapter
    }

    private fun requestStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                startActivity(Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION))
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 100)
            }
        }
    }

    private fun scanGames() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.Default) {
            val games = mutableListOf<String>()
            val gamesDir = FileUtils.getGamesDirectory(this@MainActivity)

            if (gamesDir.exists()) {
                gamesDir.listFiles()?.forEach { file ->
                    if (file.extension == Constants.ZIP_EXTENSION) {
                        try {
                            if (gameManager.isValidGameZip(file.absolutePath)) {
                                games.add(file.absolutePath)
                            }
                        } catch (e: Exception) {
                            Logger.e("Erreur scan: ${e.message}")
                        }
                    }
                }
            }

            withContext(Dispatchers.Main) {
                gameAdapter.setGames(games)
                progressBar.visibility = View.GONE
                Logger.i("Trouvé ${games.size} jeux")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200) scanGames()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scanGames()
        }
    }

    override fun onResume() {
        super.onResume()
        scanGames()
    }
}