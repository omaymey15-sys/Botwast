package com.neogame.psp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameGridAdapter(
    private var games: List<String>,
    private val onGameClick: (String) -> Unit
) : RecyclerView.Adapter<GameGridAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gameTitle: TextView = itemView.findViewById(R.id.game_title)
        private val gameType: TextView = itemView.findViewById(R.id.game_type)
        private val btnPlay: Button = itemView.findViewById(R.id.btn_play)

        fun bind(gamePath: String) {
            CoroutineScope(Dispatchers.Main).launch {
                val gameManager = GameManager(itemView.context)
                val gameInfo = gameManager.getGameInfo(gamePath)
                if (gameInfo != null) {
                    gameTitle.text = gameInfo.first
                    gameType.text = "📌 ${gameInfo.second}"
                }
            }
            btnPlay.setOnClickListener { onGameClick(gamePath) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(games[position])
    }

    override fun getItemCount(): Int = games.size

    fun setGames(newGames: List<String>) {
        games = newGames
        notifyDataSetChanged()
    }
}