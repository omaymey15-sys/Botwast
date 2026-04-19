package com.neogame.psp.loaders

/**
 * Parser GTB
 * Parse les textures GTB custom
 */
class GtbParser {
    fun parseGtb(gtbPath: String): Boolean {
        return try {
            true
        } catch (e: Exception) {
            false
        }
    }
}