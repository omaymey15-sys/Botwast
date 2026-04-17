package com.neogame.console2d.utils

import android.content.Context
import android.os.Environment
import java.io.File

object FileUtils {
    
    fun getGamesDirectory(context: Context): File {
        val gamesDir = File(context.getExternalFilesDir(null), Constants.GAMES_FOLDER)
        if (!gamesDir.exists()) {
            gamesDir.mkdirs()
        }
        return gamesDir
    }
    
    fun getDownloadsGamesDirectory(): File {
        val downloadsDir = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val gamesDir = File(downloadsDir, Constants.GAMES_FOLDER)
        if (!gamesDir.exists()) {
            gamesDir.mkdirs()
        }
        return gamesDir
    }
    
    fun loadJsonFile(file: File): String {
        return try {
            file.readText()
        } catch (e: Exception) {
            Logger.e("Error reading file: ${file.path}", e)
            ""
        }
    }
    
    fun saveJsonFile(file: File, content: String) {
        try {
            file.parentFile?.mkdirs()
            file.writeText(content)
            Logger.d("File saved: ${file.path}")
        } catch (e: Exception) {
            Logger.e("Error writing file: ${file.path}", e)
        }
    }
    
    fun deleteFile(file: File): Boolean {
        return try {
            file.delete().also {
                Logger.d("File deleted: ${file.path}")
            }
        } catch (e: Exception) {
            Logger.e("Error deleting file: ${file.path}", e)
            false
        }
    }
    
    fun fileExists(file: File): Boolean {
        return file.exists() && file.isFile
    }
}