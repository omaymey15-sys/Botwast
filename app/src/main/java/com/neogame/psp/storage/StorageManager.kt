package com.neogame.psp.storage

/**
 * Base de données de jeux
 * Gère la base de données des jeux
 */
class GameDatabase {
    fun saveGame(gameName: String): Boolean {
        return true
    }

    fun loadGame(gameName: String): Boolean {
        return true
    }
}