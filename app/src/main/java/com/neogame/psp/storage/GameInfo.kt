package com.neogame.psp.storage

/**
 * Info du jeu
 * Données informatives sur un jeu
 */
data class GameInfo(
    val id: String,
    val name: String,
    val type: String,
    val path: String,
    val size: Long = 0,
    val isValid: Boolean = true
)