package com.neogame.console2d.data.models

data class Game(
    val name: String,
    val filePath: String,
    val description: String = "",
    val thumbnail: String? = null,
    val version: String = "1.0"
)