package com.neogame.psp.loaders

import com.neogame.psp.utils.Logger

/**
 * Chargeur de jeu
 */
class GameLoader {
    fun loadGame(path: String): Boolean {
        return try {
            Logger.i("Chargement du jeu: $path")
            true
        } catch (e: Exception) {
            Logger.e("Erreur chargement: ${e.message}")
            false
        }
    }
}