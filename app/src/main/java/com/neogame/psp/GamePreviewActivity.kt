package com.neogame.psp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.neogame.psp.utils.Logger

/**
 * GamePreviewActivity - Aperçu du jeu
 */
class GamePreviewActivity : AppCompatActivity() {

    private lateinit var gameTitle: TextView
    private lateinit var gameDescription: TextView
    private lateinit var gameThumbnail: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_preview)

        gameTitle = findViewById(R.id.game_title)
        gameDescription = findViewById(R.id.game_description)
        gameThumbnail = findViewById(R.id.game_thumbnail)

        Logger.i("GamePreviewActivity créée")
    }
}