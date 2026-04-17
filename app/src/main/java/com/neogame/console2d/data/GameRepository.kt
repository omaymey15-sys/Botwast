package com.neogame.console2d.data

import android.content.Context
import android.os.Environment
import com.neogame.console2d.data.models.Game
import com.neogame.console2d.utils.Logger
import java.io.File

class GameRepository {

    fun getAvailableGames(context: Context): List<String> {
        val games = mutableListOf<String>()

        // Load from assets
        try {
            val assetFiles = context.assets.list("games") ?: emptyArray()
            games.addAll(assetFiles
                .filter { it.endsWith(".json") }
                .map { it.replace(".json", "") })
            Logger.d("Loaded ${assetFiles.size} games from assets")
        } catch (e: Exception) {
            Logger.e("Error loading games from assets", e)
        }

        // Load from Downloads
        try {
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val gamesDir = File(downloadDir, "games")

            if (gamesDir.exists()) {
                val files = gamesDir.listFiles { file ->
                    file.isFile && file.name.endsWith(".json")
                } ?: emptyArray()

                games.addAll(files.map { it.nameWithoutExtension })
                Logger.d("Loaded ${files.size} games from Downloads")
            }
        } catch (e: Exception) {
            Logger.e("Error loading games from Downloads", e)
        }

        return games.distinct().sorted()
    }

    fun loadGame(filePath: String): String {
        val file = File(filePath)
        return file.readText()
    }

    fun saveGame(filePath: String, content: String) {
        val file = File(filePath)
        file.parentFile?.mkdirs()
        file.writeText(content)
        Logger.d("Game saved to: $filePath")
    }
}