# 🎮 Console2D - Android Game Engine 2D

Un moteur de jeux **2D universel** pour Android capable de lancer **tous les types de jeux 2D** (plateforme, shoot'em up, puzzle, etc.)

## ✨ Caractéristiques

✅ **Moteur 2D flexible** - Supporte tous les genres de jeux 2D
✅ **JSON Game Format** - Définissez les jeux en JSON
✅ **Input universel** - Tactile + Gamepad Bluetooth/USB
✅ **UI Style PSP** - Interface de console rétro
✅ **Canvas Rendering** - Graphismes 2D lisses
✅ **Chargement dynamique** - Importez des jeux depuis Download
✅ **Architecture modulaire** - Extensible et maintenable

## 🏗️ Structure du projet

```
Console2D/
├── engine/          → Moteur de jeu
├── input/           → Gestion des inputs
├── ui/              → Interfaces graphiques
├── data/            → Modèles et repository
└── assets/games/    → Jeux JSON
```

## 🚀 Démarrage rapide

### 1. Cloner le repo
```bash
git clone https://github.com/ornannnzembe-ops/Console2D.git
cd Console2D
```

### 2. Ouvrir dans Android Studio
- File → Open → Sélectionner le dossier

### 3. Build & Run
- Cliquer sur "Run" ou Shift+F10

## 📝 Créer un jeu JSON

Créez un fichier `mon_jeu.json` dans `assets/games/` :

```json
{
  "name": "Mon Jeu",
  "start_scene": "level1",
  "scenes": {
    "level1": {
      "text": "Bienvenue!",
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

## 🎮 Contrôles

| Action | Tactile | Gamepad |
|--------|---------|---------|
| Déplacer | Joystick | D-Pad / Analog |
| Sauter | Bouton A | Bouton A |
| Tirer | Bouton B | Bouton B |
| Menu | Bouton START | START |

## 🛠️ Tech Stack

- **Kotlin** - Langage principal
- **Android SDK 24+** - Compatibilité
- **Canvas API** - Rendering 2D
- **JSON** - Format de jeu

## 📦 Genres supportés

- 🟢 Jeux de plateforme
- 🟢 Shoot'em up
- 🟢 Jeux de puzzle
- 🟢 RPG 2D
- 🟢 Jeux de course
- 🟢 Tous les jeux 2D !

## 🔧 Prochaines features

- [ ] Système de sauvegarde
- [ ] Sons et musique
- [ ] Système de score
- [ ] Animations avancées
- [ ] Physique (gravité, collisions)
- [ ] Store intégré

## 👨‍💻 Auteur

**ornannnzembe-ops**

## 📄 License

MIT License