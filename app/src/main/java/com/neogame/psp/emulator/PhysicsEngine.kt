package com.neogame.psp.emulator

/**
 * Moteur de physique
 * Gère la physique et les collisions
 */
class PhysicsEngine {
    fun update(objects: List<GameEngine.GameObject>, deltaTime: Float) {
        objects.forEach { obj ->
            obj.velocityX *= 0.95f
            obj.velocityZ *= 0.95f
        }
    }
}