package com.neogame.console2d.data

import android.content.Context
import android.os.Environment
import com.neogame.console2d.data.models.Game
import java.io.File

class GameRepository {

    fun getAvailableGames(context: Context): List<String> {
        val games = mutableListOf<String>()

        // Charger depuis assets
        try {
            val assetFiles = context.assets.list("games") ?: emptyArray()
            games.addAll(assetFiles.map { it.replace(".json", "") })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Charger depuis Download
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val gamesDir = File(downloadDir, "games")

        if (gamesDir.exists()) {
            val files = gamesDir.listFiles { file ->
                file.isFile && file.name.endsWith(".json")
            } ?: emptyArray()

            games.addAll(files.map { it.nameWithoutExtension })
        }

        return games.distinct()
    }

    fun loadGame(filePath: String): String {
        val file = File(filePath)
        return file.readText()
    }
}