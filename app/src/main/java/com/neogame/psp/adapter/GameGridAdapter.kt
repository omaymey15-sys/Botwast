package com.neogame.psp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neogame.psp.R
import com.neogame.psp.storage.GameManager
import com.neogame.psp.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Adaptateur grille de jeux
 * Affiche les jeux en grille
 */
class GameGridAdapter(
    private var games: List<String>,
    private val onGameClick: (String) -> Unit
) : RecyclerView.Adapter<GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val gamePath = games[position]
        holder.bind(gamePath, onGameClick)
    }

    override fun getItemCount(): Int = games.size

    fun setGames(newGames: List<String>) {
        games = newGames
        notifyDataSetChanged()
    }
}