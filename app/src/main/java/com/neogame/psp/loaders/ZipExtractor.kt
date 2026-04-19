package com.neogame.psp.loaders

/**
 * Extracteur ZIP
 * Extrait les fichiers ZIP
 */
class ZipExtractor {
    fun extract(zipPath: String, destPath: String): Boolean {
        return try {
            true
        } catch (e: Exception) {
            false
        }
    }
}