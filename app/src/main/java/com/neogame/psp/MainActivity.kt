package com.neogame.psp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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

class MainActivity : AppCompatActivity() {

    private lateinit var gamesRecycler: RecyclerView
    private lateinit var gameAdapter: GameGridAdapter
    private lateinit var gameManager: GameManager
    private lateinit var progressBar: ProgressBar
    private lateinit var btnAddGame: Button
    private lateinit var txtTitle: TextView

    private val addGameLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            scanGames()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 💥 CRASH HANDLER GLOBAL
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            Logger.e("💥 CRASH GLOBAL: ${e.message}")
            e.printStackTrace()

            runOnUiThread {
                Toast.makeText(this, "CRASH: ${e.message}", Toast.LENGTH_LONG).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("crash_error", e.toString())
                startActivity(intent)
                finish()
            }
        }

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

        // 💥 afficher crash si existant
        val error = intent.getStringExtra("crash_error")
        if (error != null) {
            txtTitle.text = "💥 CRASH DETECTÉ\n$error"
        } else {
            txtTitle.text = "🎮 ${Constants.APP_NAME}"
        }

        txtTitle.textSize = 28f

        btnAddGame.apply {
            text = "➕ Add Game"
            background = ContextCompat.getDrawable(this@MainActivity, R.drawable.button_a)

            setOnClickListener {
                addGameLauncher.launch(
                    Intent(this@MainActivity, AddGameActivity::class.java)
                )
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
                startActivity(
                    Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                )
            }

        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                100
            )
        }
    }

    private fun scanGames() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.Default) {

            try {
                val games = mutableListOf<String>()
                val gamesDir = FileUtils.getGamesDirectory(this@MainActivity)

                if (gamesDir.exists()) {
                    gamesDir.listFiles()?.forEach { file ->

                        try {
                            if (file.extension == Constants.ZIP_EXTENSION) {
                                if (gameManager.isValidGameZip(file.absolutePath)) {
                                    games.add(file.absolutePath)
                                }
                            }
                        } catch (e: Exception) {
                            Logger.e("File error: ${e.message}")
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    gameAdapter.setGames(games)
                    progressBar.visibility = View.GONE
                    Logger.i("Found ${games.size} games")
                }

            } catch (e: Exception) {
                Logger.e("SCAN CRASH: ${e.message}")

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    txtTitle.text = "Scan error: ${e.message}"
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            scanGames()
        }
    }

    override fun onResume() {
        super.onResume()
        scanGames()
    }
}