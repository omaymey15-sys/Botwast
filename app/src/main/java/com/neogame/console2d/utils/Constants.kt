package com.neogame.console2d.utils

object Constants {
    
    // FPS Settings
    const val FPS_TARGET = 60
    const val FRAME_DELAY = 1000 / FPS_TARGET
    
    // Player Physics
    const val PLAYER_WIDTH = 50f
    const val PLAYER_HEIGHT = 50f
    const val PLAYER_SPEED = 10f
    const val PLAYER_JUMP_HEIGHT = 150f
    const val GRAVITY = 0.5f
    
    // Input Settings
    const val JOYSTICK_DEADZONE = 0.2f
    const val MOTION_EVENT_DELAY = 50L // milliseconds
    
    // File System
    const val GAMES_FOLDER = "games"
    const val GAME_EXTENSION = ".json"
    
    // Debug
    const val DEBUG = true
    const val LOG_TAG = "Console2D"
    
    // Screen
    const val SCREEN_WIDTH = 800
    const val SCREEN_HEIGHT = 800
}