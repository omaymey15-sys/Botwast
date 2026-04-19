package com.neogame.psp.storage

import android.content.Context
import com.google.gson.JsonParser
import com.neogame.psp.utils.Constants
import com.neogame.psp.utils.FileUtils
import com.neogame.psp.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipFile

/**
 * Gestionnaire de jeux
 * Gère l'accès aux jeux et à leur gestion
 */
class GameManager(private val context: Context) {

    suspend fun isValidGameZip(zipPath: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val zipFile = ZipFile(File(zipPath))
            val hasGameJson = zipFile.getEntry("game.json") != null
            zipFile.close()
            hasGameJson
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getGameInfo(zipPath: String): Pair<String, String>? = withContext(Dispatchers.IO) {
        return@withContext try {
            val zipFile = ZipFile(File(zipPath))
            val gameJsonEntry = zipFile.getEntry("game.json") ?: return@withContext null

            val gameJsonContent = zipFile.getInputStream(gameJsonEntry).readBytes().decodeToString()
            val gameConfig = JsonParser.parseString(gameJsonContent).asJsonObject

            val name = gameConfig.get("metadata")?.asJsonObject?.get("name")?.asString ?: "Inconnu"
            val type = gameConfig.get("metadata")?.asJsonObject?.get("type")?.asString ?: Constants.GAME_TYPE_2D

            zipFile.close()
            Pair(name, type)
        } catch (e: Exception) {
            Logger.e("Erreur: ${e.message}")
            null
        }
    }

    fun getGamesDirectory(): String {
        return FileUtils.getGamesDirectory(context).absolutePath
    }
}