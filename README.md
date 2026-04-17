# 🎮 **Console2D** - Universal 2D Game Engine for Android

A powerful, modular 2D game engine for Android that supports all types of 2D games (platformers, shoot'em ups, puzzles, RPGs, racing games, and more).

## ✨ **Features**

✅ **Universal 2D Engine** - Play any type of 2D game  
✅ **JSON Game Format** - Define games using simple JSON files  
✅ **Touch & Gamepad Support** - Tactile controls + Bluetooth/USB gamepad  
✅ **PSP-style UI** - Retro console interface  
✅ **Canvas Rendering** - Smooth 2D graphics  
✅ **Dynamic Game Loading** - Load games from Assets or Downloads  
✅ **Modular Architecture** - Extensible and maintainable codebase  
✅ **Debug Tools** - Integrated logging system  

---

## 🚀 **Quick Start**

### 1. Clone Repository
```bash
git clone https://github.com/ornannnzembe-ops/Console2D.git
cd Console2D
```

### 2. Open in Android Studio
- File → Open → Select `Console2D` folder
- Wait for Gradle sync

### 3. Build & Run
- Press `Shift + F10` or Click "Run"
- Select a device/emulator

---

## 📁 **Project Structure**

```
Console2D/
├── app/
│   ├── src/main/
│   │   ├── java/com/neogame/console2d/
│   │   │   ├── engine/          # Game engine core
│   │   │   ├── input/           # Input handling
│   │   │   ├── ui/              # UI components
│   │   │   ├── data/            # Data models & repository
│   │   │   └── utils/           # Utilities
│   │   ├── assets/games/        # Game JSON files
│   │   └── res/                 # Resources
│   └── build.gradle
├── build.gradle
└── README.md
```

---

## 🎮 **Creating a Game**

Create a JSON file in `assets/games/` directory:

```json
{
  "name": "My Game",
  "version": "1.0.0",
  "start_scene": "level1",
  "scenes": {
    "level1": {
      "text": "Level 1",
      "background": "#87CEEB",
      "objects": [
        {
          "type": "platform",
          "x": 0,
          "y": 600,
          "width": 800,
          "height": 40,
          "color": -16711681
        }
      ],
      "inputs": {
        "DPAD_LEFT": "move(-20, 0)",
        "DPAD_RIGHT": "move(20, 0)",
        "BUTTON_A": "jump()"
      }
    }
  }
}
```

---

## 🎮 **Control Mapping**

| Action | Touch | Gamepad |
|--------|-------|---------|
| Move Left | D-Pad ← | D-Pad ← |
| Move Right | D-Pad → | D-Pad → |
| Move Up | D-Pad ↑ | D-Pad ↑ |
| Move Down | D-Pad ↓ | D-Pad ↓ |
| Action A | Button A | Button A |
| Action B | Button B | Button B |
| Action X | Button X | Button X |
| Action Y | Button Y | Button Y |
| Menu | START | START |

---

## 🛠️ **Tech Stack**

- **Language**: Kotlin 1.9.21
- **Android SDK**: 24+ (API Level 24+)
- **Build Tool**: Gradle 8.2.0
- **Graphics**: Canvas 2D
- **Data Format**: JSON

---

## 📦 **Included Games**

1. **Platform Adventure** - Classic platformer
2. **Space Shooter** - Shoot'em up arcade
3. **Puzzle Master** - Block puzzle game
4. **RPG Adventure** - Role-playing game
5. **Speed Racing** - Racing game

---

## 🔧 **API Reference**

### Game Engine
```kotlin
GameEngine.loadGame(context, "game_name")
GameEngine.onInput(action, dx, dy)
GameEngine.changeScene("scene_name")
GameEngine.stop()
```

### Input Manager
```kotlin
inputManager.sendAction("BUTTON_A")
inputManager.sendJoystickInput(dx, dy)
```

---

## 📋 **JSON Game Format**

### Root Level
- `name` (string) - Game name
- `version` (string) - Game version
- `start_scene` (string) - Initial scene
- `scenes` (object) - Scene definitions

### Scene Object
- `name` (string) - Scene name
- `text` (string) - Scene description
- `background` (string) - Background color
- `objects` (array) - Game objects
- `inputs` (object) - Input-action mapping

### Game Object
- `type` (string) - Object type
- `x`, `y` (number) - Position
- `width`, `height` (number) - Dimensions
- `color` (number) - ARGB color value

---

## 🚀 **Future Features**

- [ ] Physics engine (gravity, collisions)
- [ ] Sound & music system
- [ ] Score & leaderboard
- [ ] Save/Load system
- [ ] Animation system
- [ ] Particle effects
- [ ] Game Store integration
- [ ] Multiplayer support

---

## 📱 **Requirements**

- Android 5.0+ (API 24+)
- 50MB free storage
- 2GB RAM (recommended)

---

## 🤝 **Contributing**

Contributions are welcome! Feel free to:
- Report bugs
- Suggest features
- Create new games
- Improve documentation

---

## 📄 **License**

MIT License - See LICENSE file for details

---

## 👨‍💻 **Author**

**ornannnzembe-ops**

---

## 📞 **Support**

For issues or questions:
- GitHub Issues
- GitHub Discussions

---

**Made with ❤️ for retro gaming**