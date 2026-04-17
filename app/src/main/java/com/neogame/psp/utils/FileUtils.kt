package com.neogame.psp.utils

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * Utilitaires pour la gestion des fichiers
 */
object FileUtils {
    fun getGamesDirectory(context: Context): File {
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            Constants.GAMES_DIR_NAME
        ).apply {
            if (!exists()) mkdirs()
        }
    }

    fun deleteDirectory(directory: File): Boolean {
        return if (directory.isDirectory) {
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    deleteDirectory(file)
                } else {
                    file.delete()
                }
            }
            directory.delete()
        } else {
            directory.delete()
        }
    }
}