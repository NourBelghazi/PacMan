# 👻 Pac-Man (Java)

A faithful **Pac-Man clone developed in Java using Swing**, featuring all classic mechanics: power pellets, scared ghosts, combo scoring, cherry bonuses, tunnel wrap, level progression, and a persistent high score.

---

## 📷 Preview

<img width="755" height="877" alt="image" src="https://github.com/user-attachments/assets/13968b4c-4309-4f0a-aaf3-c7591eb200aa" />

---

## ✅ Features

| Feature | Status |
|---|---|
| Pacman movement (arrow keys) | ✅ |
| Wall collision | ✅ |
| Food dot collection (+10 pts each) | ✅ |
| Ghost random movement | ✅ |
| Lives system (3 lives) | ✅ |
| Game over screen | ✅ |
| **Power pellets** (`powerFood.png`) | ✅ |
| **Scared ghost mode** (`scaredGhost.png`) | ✅ |
| **Combo ghost scoring** (200 → 400 → 800 → 1600) | ✅ |
| **Ghost respawn with flicker animation** | ✅ |
| **Cherry bonus fruit** (`cherry.png`) | ✅ |
| **Left / right tunnel wrap** | ✅ |
| **Level progression** (all food → next level) | ✅ |
| **Persistent high score** | ✅ |
| **Pause / resume** (P key) | ✅ |
| **Restart after game over** (ENTER / SPACE) | ✅ |
| **Power-mode progress bar** in HUD | ✅ |

---

## ⌨️ Controls

| Key | Action |
|---|---|
| ↑ ↓ ← → | Move Pacman |
| `P` | Pause / Resume |
| `ENTER` or `SPACE` | Restart after game over |

---

## 🏆 Scoring

| Action | Points |
|---|---|
| Eat a food dot | 10 |
| Eat a power pellet | 50 |
| Eat the cherry | 100 |
| Eat 1st ghost in power mode | 200 |
| Eat 2nd ghost (combo) | 400 |
| Eat 3rd ghost (combo) | 800 |
| Eat 4th ghost (combo) | 1 600 |

> The combo resets every time Pacman eats a new power pellet.

---

## ▶️ Run the Game

### 1️⃣ Clone the repository

```
git clone git@github.com:NourBelghazi/Pacman-in-Java.git
```

### 2️⃣ IntelliJ IDEA

1. Open the project folder.
2. Mark `src/` as the **Sources Root**.
3. Run `App.java`.

### 3️⃣ Command line

```bash
javac -d out src/*.java
java -cp out:src App
```

---

## 📁 Project structure

```
Pacman-in-Java/
├── src/
│   ├── App.java           # Entry point – creates the JFrame window
│   ├── Pacman.java        # Game panel – all logic, rendering, and input
│   └── images/
│       ├── wall.png
│       ├── pacmanUp/Down/Left/Right.png
│       ├── blueGhost / redGhost / pinkGhost / orangeGhost.png
│       ├── scaredGhost.png   ← scared ghost (power mode)
│       ├── powerFood.png     ← power pellet sprite
│       └── cherry.png        ← bonus fruit sprite
├── .gitignore
└── README.md
```

---

## 🧠 Architecture overview

### `App.java`

Creates the `JFrame` window and adds the `Pacman` panel.

### `Pacman.java`

```
Pacman (JPanel)
│
├── Block (inner class)          – any entity: wall, food, ghost, Pacman
│
├── loadMap()                    – parses TILE_MAP into entity sets
├── move()                       – one tick, delegates to sub-methods:
│   ├── movePacman()
│   ├── moveGhosts()
│   ├── handleGhostCollisions()
│   ├── handleFoodCollection()
│   ├── handlePowerPelletCollection()
│   ├── handleCherryCollection()
│   ├── tickTimers()
│   └── checkLevelComplete()
├── draw() / drawHUD()           – rendering + overlays
├── activatePowerMode()          – switch ghosts to scaredGhost.png
├── deactivatePowerMode()        – restore normal sprites
├── resetPositions()             – reposition after life lost
├── spawnCherry()                – place cherry at row 3 col 9
├── restartGame()                – full reset, keeps high score
└── collision(a, b)              – AABB with 5-px inset
```

**Game loop:** `javax.swing.Timer` fires every **50 ms** → `move()` + `repaint()`.

---

## 🎮 Game mechanics

### Power pellets
Four pellets at the maze corners (`W` tile). Eating one switches all ghosts to `scaredGhost.png` for ~15 s. A cyan bar tracks remaining time.

### Scared ghost respawn
Eaten ghost teleports to its spawn, flickers for ~4 s (invincible), then becomes active again. If power mode is still on when the flicker ends, it turns scared once more.

### Cherry bonus
Appears at row 3 col 9 when **half** the food is eaten. Worth 100 pts. Disappears after ~10 s.

### Tunnel wrap
Pacman and ghosts exit one side and reappear on the other.

### Level progression
Clearing all food and power pellets increments the level and reloads the map.

---

## 🗺️ Tile-map legend

| Character | Meaning |
|---|---|
| `X` | Wall |
| ` ` (space) | Food dot |
| `W` | Power pellet |
| `b` / `o` / `p` / `r` | Ghost start positions |
| `P` | Pacman start |
| `O` | Open / ghost-house area |

---

## ⚙️ Technologies Used

- **Java** + **Java Swing**
- `JPanel` rendering with `paintComponent()`
- Keyboard input with `KeyListener`
- Game loop with `ActionListener` + `Timer`
- Tile-based map system

---

## 🚀 Possible Improvements

- Ghost AI pathfinding (chase / scatter / frightened modes)
- Pacman mouth animation frames
- Sound effects
- Persistent high score (file storage)
- Multiple maps / map editor

---

## 👨‍💻 Author

**Nour Belghazi**
