package com.neogame.psp.adapter

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.neogame.psp.R
import com.neogame.psp.storage.GameManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewHolder pour item de jeu
 * Affiche un jeu dans la grille
 */
class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val gameTitle: TextView = itemView.findViewById(R.id.game_title)
    private val gameType: TextView = itemView.findViewById(R.id.game_type)
    private val btnPlay: Button = itemView.findViewById(R.id.btn_play)

    fun bind(gamePath: String, onGameClick: (String) -> Unit) {
        val gameManager = GameManager(itemView.context)
        
        CoroutineScope(Dispatchers.Main).launch {
            val gameInfo = gameManager.getGameInfo(gamePath)
            if (gameInfo != null) {
                gameTitle.text = gameInfo.first
                gameType.text = "📌 ${gameInfo.second}"
            }
        }

        btnPlay.apply {
            text = "▶️ Jouer"
            background = ContextCompat.getDrawable(itemView.context, R.drawable.button_b)
            setOnClickListener {
                onGameClick(gamePath)
            }
        }
    }
}