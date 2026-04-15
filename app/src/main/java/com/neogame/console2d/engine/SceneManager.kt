package com.neogame.console2d.engine

import org.json.JSONObject

class SceneManager {
    
    fun getScene(gameJson: JSONObject, sceneName: String): JSONObject? {
        return gameJson.optJSONObject("scenes")?.optJSONObject(sceneName)
    }

    fun getSceneDescription(scene: JSONObject): String {
        return scene.optString("text", "")
    }

    fun getSceneInputs(scene: JSONObject): JSONObject? {
        return scene.optJSONObject("inputs")
    }

    fun getSceneObjects(scene: JSONObject): List<GameObject> {
        val objects = mutableListOf<GameObject>()
        val objectsArray = scene.optJSONArray("objects")
        
        if (objectsArray != null) {
            for (i in 0 until objectsArray.length()) {
                val obj = objectsArray.getJSONObject(i)
                objects.add(
                    GameObject(
                        x = obj.optDouble("x", 0.0).toFloat(),
                        y = obj.optDouble("y", 0.0).toFloat(),
                        width = obj.optDouble("width", 50.0).toFloat(),
                        height = obj.optDouble("height", 50.0).toFloat(),
                        type = obj.optString("type", "rect"),
                        color = obj.optInt("color", -1)
                    )
                )
            }
        }
        
        return objects
    }
}

data class GameObject(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val type: String,
    val color: Int
)