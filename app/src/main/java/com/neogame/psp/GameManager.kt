package com.neogame.psp

import android.content.Context
import com.google.gson.JsonParser
import java.io.File
import java.util.zip.ZipFile

class GameManager(private val context: Context) {
    
    suspend fun getGameInfo(zipPath: String): Pair<String, String>? {
        return try {
            val zipFile = ZipFile(File(zipPath))
            val gameJsonEntry = zipFile.getEntry("game.json") ?: return null

            val gameJsonContent = zipFile.getInputStream(gameJsonEntry).readBytes().decodeToString()
            val gameConfig = JsonParser.parseString(gameJsonContent).asJsonObject

            val name = gameConfig.get("metadata")?.asJsonObject?.get("name")?.asString ?: "Unknown"
            val type = gameConfig.get("metadata")?.asJsonObject?.get("type")?.asString ?: "2D"

            zipFile.close()
            Pair(name, type)
        } catch (e: Exception) {
            Logger.e("Info error: ${e.message}")
            null
        }
    }
}