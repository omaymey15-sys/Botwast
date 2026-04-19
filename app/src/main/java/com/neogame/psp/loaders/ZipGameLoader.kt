package com.neogame.psp.loaders

import android.content.Context
import com.google.gson.JsonParser
import com.neogame.psp.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipFile

/**
 * Chargeur de jeu ZIP
 * Charge les jeux au format ZIP
 */
class ZipGameLoader(private val context: Context) {

    data class GameData(
        val config: com.google.gson.JsonObject,
        val assets: Map<String, ByteArray>,
        val gameType: String
    )

    suspend fun loadGameFromZip(zipPath: String): GameData? = withContext(Dispatchers.IO) {
        return@withContext try {
            val zipFile = ZipFile(File(zipPath))
            val gameJsonEntry = zipFile.getEntry("game.json") ?: return@withContext null

            val gameJsonContent = zipFile.getInputStream(gameJsonEntry).readBytes().decodeToString()
            val gameConfig = JsonParser.parseString(gameJsonContent).asJsonObject

            val gameType = gameConfig.get("metadata")?.asJsonObject?.get("type")?.asString ?: "2D"
            val assets = mutableMapOf<String, ByteArray>()

            zipFile.entries().iterator().forEach { entry ->
                if ((entry.name.startsWith("assets/") || entry.name.startsWith("obj/")) && !entry.isDirectory) {
                    val data = zipFile.getInputStream(entry).readBytes()
                    assets[entry.name] = data
                }
            }

            zipFile.close()
            Logger.i("Jeu chargé: $gameType")
            GameData(gameConfig, assets, gameType)
        } catch (e: Exception) {
            Logger.e("Erreur chargement: ${e.message}")
            null
        }
    }

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
            val type = gameConfig.get("metadata")?.asJsonObject?.get("type")?.asString ?: "2D"

            zipFile.close()
            Pair(name, type)
        } catch (e: Exception) {
            null
        }
    }
}