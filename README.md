# Connect Four

A web-based Connect Four game built with **Kotlin Multiplatform** and **Compose HTML**.

---

## Features

- **Two-player local play** - Purple vs Pink, taking turns in the same browser
- **Configurable board** - choose rows and columns from 4 to 20
- **Configurable win length** - Connect 4 up to 10
- **Win and draw detection** - winning cells are highlighted with a pulse animation
- **Piece fall animation** - pieces drop into their slot 
- **Persistence** - the game survives a page reload, progress is saved to `localStorage` after every move
- **Responsive layout** - adapts to desktop and mobile

---

## Project Structure

```
src/
├── commonMain/kotlin/connectfour/
│   └── GameLogic.kt     # Pure game logic: board, win detection, result
├── commonTest/kotlin/connectfour/
│   └── GameLogicTest.kt # Unit tests
└── jsMain/
    ├── kotlin/connectfour/
    │   ├── main.kt      # Entry point
    │   ├── App.kt       # All composables: App, ConfigScreen, GameScreen
    │   └── Persistence.kt # SavedGame - localStorage save / load / clear
    └── resources/
        └── index.html   # Single HTML file - all CSS lives here
```
---

## How to Run

```bash
./gradlew build
./gradlew jsBrowserDevelopmentRun
```

Opens the game automatically at `http://localhost:8080`.

---

## How to Play

1. On the **Settings** screen, pick the number of rows, columns, and how many in a row wins
2. Click **Start Game**
3. Click any column to drop a piece
4. The status bar shows whose turn it is and announces the winner or a draw
5. **New Game** resets the board with the same settings. **Settings** returns to the config screen

If you reload the page mid-game, the board is restored exactly as you left it.

---
