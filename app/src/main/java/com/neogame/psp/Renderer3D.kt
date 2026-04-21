package com.neogame.psp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.android.filament.*
import com.google.android.filament.EntityManager

class Renderer3D(private val context: Context) : BaseRenderer() {

    private var engine: Engine? = null
    private var renderer: Renderer? = null
    private var scene: Scene? = null
    private var view: View? = null
    private var camera: Camera? = null
    private var cameraEntity: Int = 0

    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textSize = 20f
    }

    init {
        try {
            engine = Engine.create()
            renderer = engine?.createRenderer()
            scene = engine?.createScene()
            view = engine?.createView()

            // ✅ FIX camera (important)
            cameraEntity = EntityManager.get().create()
            camera = engine?.createCamera(cameraEntity)

            view?.camera = camera
            view?.scene = scene

            Logger.i("Filament 3D Engine initialized")

        } catch (e: Exception) {
            Logger.e("Filament 3D init error: ${e.message}")
        }
    }

    override fun render(canvas: Canvas?, gameEngine: GameEngine?) {
        if (canvas == null || gameEngine == null) return

        canvas.drawColor(Color.parseColor("#1a1a2e"))

        gameEngine.getGameObjects().forEach { obj ->
            paint.color = obj.color
            paint.style = Paint.Style.FILL

            val scale = 1f + (obj.z / 500f)
            val x = obj.x * scale + 100 * (obj.z / 500f)
            val y = obj.y * scale + 100 * (obj.z / 500f)
            val w = obj.width * scale
            val h = obj.height * scale

            canvas.drawRect(x, y, x + w, y + h, paint)

            paint.color = Color.CYAN
            paint.textSize = 14f
            canvas.drawText("${obj.id} Z:${obj.z.toInt()}", x + 5, y + 20, paint)
        }

        paint.color = Color.GREEN
        paint.textSize = 16f
        canvas.drawText("✓ Filament 3D Engine Active", 20f, 80f, paint)
    }

    override fun dispose() {
        try {
            engine?.let { eng ->

                renderer?.let { eng.destroyRenderer(it) }
                view?.let { eng.destroyView(it) }
                scene?.let { eng.destroyScene(it) }

                camera?.let { eng.destroyCameraComponent(cameraEntity) }

            }

            engine?.destroy()

            // Nettoyage mémoire
            renderer = null
            view = null
            scene = null
            camera = null
            engine = null

            Logger.i("Filament 3D disposed")

        } catch (e: Exception) {
            Logger.e("Dispose error: ${e.message}")
        }
    }

    override fun getName(): String = "3D Filament Renderer"
}