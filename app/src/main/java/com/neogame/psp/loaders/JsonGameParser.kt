package com.neogame.psp.loaders

import com.google.gson.JsonObject
import com.neogame.psp.utils.Logger

/**
 * Parser JSON
 * Parse la configuration des jeux JSON
 */
class JsonGameParser {
    fun parseGameJson(jsonContent: String): JsonObject? {
        return try {
            com.google.gson.JsonParser.parseString(jsonContent).asJsonObject
        } catch (e: Exception) {
            Logger.e("Erreur parse: ${e.message}")
            null
        }
    }
}