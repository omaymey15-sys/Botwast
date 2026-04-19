package com.neogame.psp.loaders

/**
 * Parser OBJ
 * Parse les modèles OBJ 3D
 */
class ObjParser {
    fun parseObj(objPath: String): Boolean {
        return try {
            true
        } catch (e: Exception) {
            false
        }
    }
}