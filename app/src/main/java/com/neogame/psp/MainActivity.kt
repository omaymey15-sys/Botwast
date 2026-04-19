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

        // 💥 CRASH HANDLER GLOBAL (anti fermeture brutale)
        Thread.setDefaultUncaughtExceptionHandler { _, e ->

            e.printStackTrace()

            try {
                val file = File(getExternalFilesDir(null), "crash_log.txt")
                file.appendText("\n\n${e.stackTraceToString()}")
            } catch (_: Exception) {}

            runOnUiThread {
                Toast.makeText(this, "Crash détecté", Toast.LENGTH_LONG).show()

                val intent = Intent(this, CrashActivity::class.java)
                intent.putExtra("error", e.stackTraceToString())
                startActivity(intent)

                finish()
            }

            Runtime.getRuntime().exit(0)
        }

        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_main)

            gameManager = GameManager(this)

            initViews()
            setupRecycler()
            requestStoragePermissions()
            scanGames()

            Logger.i("MainActivity started OK")

        } catch (e: Exception) {
            e.printStackTrace()

            val intent = Intent(this, CrashActivity::class.java)
            intent.putExtra("error", e.stackTraceToString())
            startActivity(intent)

            finish()
        }
    }

    private fun initViews() {
        try {
            gamesRecycler = findViewById(R.id.games_grid)
            progressBar = findViewById(R.id.progress_loading)
            btnAddGame = findViewById(R.id.btn_add_game)
            txtTitle = findViewById(R.id.txt_title)

            val error = intent.getStringExtra("crash_error")

            txtTitle.text = if (error != null) {
                "💥 CRASH DETECTED"
            } else {
                "🎮 ${Constants.APP_NAME}"
            }

            btnAddGame.text = "➕ Add Game"

            btnAddGame.setOnClickListener {
                addGameLauncher.launch(
                    Intent(this, AddGameActivity::class.java)
                )
            }

        } catch (e: Exception) {
            Logger.e("initViews error: ${e.message}")
        }
    }

    private fun setupRecycler() {
        try {
            gamesRecycler.layoutManager = GridLayoutManager(this, 2)

            gameAdapter = GameGridAdapter(emptyList()) { gamePath ->
                startActivity(
                    Intent(this, EmulatorActivity::class.java)
                        .putExtra("game_path", gamePath)
                )
            }

            gamesRecycler.adapter = gameAdapter

        } catch (e: Exception) {
            Logger.e("Recycler error: ${e.message}")
        }
    }

    private fun requestStoragePermissions() {
        try {
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

        } catch (e: Exception) {
            Logger.e("Permission error: ${e.message}")
        }
    }

    private fun scanGames() {

        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.Default) {

            try {
                val games = mutableListOf<String>()

                val gamesDir = FileUtils.getGamesDirectory(this@MainActivity)

                if (gamesDir != null && gamesDir.exists()) {

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

                    Logger.i("Games found: ${games.size}")
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    txtTitle.text = "SCAN ERROR"
                }

                Logger.e("scanGames crash: ${e.message}")
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
