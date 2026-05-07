package connectfour

import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.*
import kotlin.math.max
import kotlin.math.min

@Composable
fun App() {
    var config by remember { mutableStateOf<GameConfig?>(SavedGame.load()?.config) }

    val current = config
    if (current == null) {
        ConfigScreen(onStart = { config = it })
    } else {
        GameScreen(config = current, onBack = {
            SavedGame.clear()
            config = null
        })
    }
}

@Composable
fun ConfigScreen(onStart: (GameConfig) -> Unit) {
    var rows by remember { mutableStateOf(6) }
    var cols by remember { mutableStateOf(7) }
    var winLen by remember { mutableStateOf(4) }

    val maxWin = minOf(rows, cols, 10)
    val effectiveWin = winLen.coerceIn(4, maxWin)

    Div(attrs = { classes("page") }) {
        Div(attrs = { classes("title-block") }) {
            H1 { Text("Connect Four") }
            P(attrs = { classes("subtitle") }) { Text("Choose your board size and start playing") }
        }

        Div(attrs = { classes("config-form") }) {
            SliderField(label = "Rows", value = rows, min = 4, max = 20, onValueChange = { rows = it })
            SliderField(label = "Columns", value = cols, min = 4, max = 20, onValueChange = { cols = it })
            SliderField(label = "Connect", value = effectiveWin, min = 4, max = maxWin, onValueChange = { winLen = it })

            Button(attrs = {
                classes("btn", "btn-primary")
                onClick { onStart(GameConfig(rows, cols, effectiveWin)) }
            }) {
                Text("Start Game")
            }
        }
    }
}

@Composable
private fun SliderField(label: String, value: Int, min: Int, max: Int, onValueChange: (Int) -> Unit) {
    Div(attrs = { classes("form-group") }) {
        Div(attrs = { classes("form-label-row") }) {
            Label { Text(label) }
            Span(attrs = { classes("form-value") }) { Text(value.toString()) }
        }
        Input(InputType.Range, attrs = {
            attr("min", min.toString())
            attr("max", max.toString())
            attr("value", value.toString())
            onInput { onValueChange(it.value?.toInt() ?: value) }
        })
    }
}

@Composable
fun GameScreen(config: GameConfig, onBack: () -> Unit) {
    val saved = remember(config) { SavedGame.load() }
    var board by remember(config) { mutableStateOf(saved?.board ?: emptyBoard(config.rows, config.cols)) }
    var currentPlayer by remember(config) { mutableStateOf(saved?.currentPlayer ?: Player.PURPLE) }
    var result by remember(config) { mutableStateOf(saved?.let { detectCurrentResult(it.board, config) } ?: GameResult.InProgress) }

    var lastPlaced by remember(config) { mutableStateOf<Pair<Int, Int>?>(null) }

    fun canDrop(col: Int) = result is GameResult.InProgress && board[0][col] == null

    fun drop(col: Int) {
        if (!canDrop(col)) return
        val row = (config.rows - 1 downTo 0).first { board[it][col] == null }
        val player = currentPlayer
        val newBoard = board.mapIndexed { r, rowList ->
            if (r == row) rowList.mapIndexed { c, cell -> if (c == col) player else cell } else rowList
        }
        board = newBoard
        lastPlaced = row to col
        result = computeResult(newBoard, row, col, player, config)
        val nextPlayer = if (result is GameResult.InProgress) {
            if (player == Player.PURPLE) Player.PINK else Player.PURPLE
        } else player
        if (result is GameResult.InProgress) currentPlayer = nextPlayer
        SavedGame(
            config = config,
            board = newBoard,
            currentPlayer = nextPlayer,
            lastPlaced = SavedCell(row, col),
        ).save()
    }

    fun reset() {
        SavedGame.clear()
        board = emptyBoard(config.rows, config.cols)
        currentPlayer = Player.PURPLE
        result = GameResult.InProgress
        lastPlaced = null
    }

    val winCells: Set<Pair<Int, Int>> = (result as? GameResult.Win)?.cells ?: emptySet()


    val cellByWidth = "calc(92vw / ${config.cols})"
    val cellByHeight = "calc((100dvh - 200px) / ${config.rows})"
    val cellSize = "clamp(18px, min($cellByWidth, $cellByHeight), 60px)"
    val cellGap = "clamp(3px, 1vmin, 8px)"

    Div(attrs = { classes("page", "game-page") }) {

        Div(attrs = { classes("status-bar") }) {
            when (val r = result) {
                is GameResult.Win -> {
                    PlayerDot(r.player)
                    Span { Text("${r.player.displayName()} wins!") }
                }
                is GameResult.Draw -> Span { Text("It's a draw!") }
                else -> {
                    PlayerDot(currentPlayer)
                    Span { Text("${currentPlayer.displayName()}'s turn") }
                }
            }
        }

        Div(attrs = { classes("board-wrapper") }) {
            Div(attrs = {
                classes("board")
                if (result is GameResult.Win) classes("board-won")
                style {
                    property("--cell-size", cellSize)
                    property("--cell-gap", cellGap)
                }
            }) {

                Div(attrs = {
                    classes("board-columns")
                    style { property("gap", cellGap) }
                }) {
                    for (col in 0 until config.cols) {
                        Div(attrs = {
                            classes("board-col")
                            onClick { drop(col) }
                            style { property("gap", cellGap) }
                        }) {
                            for (row in 0 until config.rows) {
                                val player = board[row][col]
                                val isWinCell = (row to col) in winCells
                                val isNew = lastPlaced == (row to col) && player != null
                                // Duration scales with fall distance
                                val fallDuration = min(0.55, max(0.1, row * 0.055 + 0.1))

                                Div(attrs = {
                                    classes("cell", player?.cellClass() ?: "cell-empty")
                                    if (isWinCell) classes("cell-winning")
                                    if (isNew) classes("cell-falling")
                                    style {
                                        property("width", cellSize)
                                        property("height", cellSize)
                                        if (isNew) {
                                            property("--fall-rows", row.toString())
                                            property("--fall-duration", "${fallDuration}s")
                                        }
                                    }
                                }) {}
                            }
                        }
                    }
                }
            }
        }

        Div(attrs = { classes("btn-row") }) {
            Button(attrs = {
                classes("btn", "btn-primary")
                onClick { reset() }
            }) { Text("New Game") }

            Button(attrs = {
                classes("btn", "btn-secondary")
                onClick { onBack() }
            }) { Text("Settings") }
        }
    }
}

@Composable
private fun PlayerDot(player: Player) {
    Div(attrs = {
        classes("player-dot", if (player == Player.PURPLE) "player-dot-purple" else "player-dot-pink")
    }) {}
}

private fun Player.displayName() = name.lowercase().replaceFirstChar { it.uppercase() }
private fun Player.cellClass() = if (this == Player.PURPLE) "cell-purple" else "cell-pink"
