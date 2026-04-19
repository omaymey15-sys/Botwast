package com.neogame.psp.emulator

/**
 * Gestionnaire d'entrées
 * Traite les entrées utilisateur
 */
class InputHandler {
    private val pressedKeys = mutableSetOf<String>()

    fun pressKey(key: String) {
        pressedKeys.add(key)
    }

    fun releaseKey(key: String) {
        pressedKeys.remove(key)
    }

    fun isKeyPressed(key: String): Boolean = key in pressedKeys
}