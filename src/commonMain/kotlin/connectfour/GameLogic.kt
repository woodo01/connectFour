package connectfour

import kotlinx.serialization.Serializable

@Serializable
enum class Player { PURPLE, PINK }

@Serializable
data class GameConfig(
    val rows: Int,
    val cols: Int,
    val winLength: Int,
)

sealed class GameResult {
    data class Win(val player: Player, val cells: Set<Pair<Int, Int>>) : GameResult()
    data object Draw : GameResult()
    data object InProgress : GameResult()
}

fun emptyBoard(rows: Int, cols: Int): List<List<Player?>> =
    List(rows) { List<Player?>(cols) { null } }

private val DIRECTIONS = listOf(0 to 1, 1 to 0, 1 to 1, 1 to -1)

fun findWinCells(
    board: List<List<Player?>>,
    row: Int,
    col: Int,
    player: Player,
    config: GameConfig,
): Set<Pair<Int, Int>>? {
    for ((dr, dc) in DIRECTIONS) {
        val cells = mutableSetOf(row to col)
        var r = row + dr
        var c = col + dc
        while (r in 0 until config.rows && c in 0 until config.cols && board[r][c] == player) {
            cells += r to c
            r += dr
            c += dc
        }
        r = row - dr
        c = col - dc
        while (r in 0 until config.rows && c in 0 until config.cols && board[r][c] == player) {
            cells += r to c
            r -= dr
            c -= dc
        }
        if (cells.size >= config.winLength) return cells
    }
    return null
}

private fun isBoardFull(board: List<List<Player?>>, config: GameConfig) =
    (0 until config.cols).all { board[0][it] != null }

fun detectCurrentResult(board: List<List<Player?>>, config: GameConfig): GameResult {
    for (row in 0 until config.rows) {
        for (col in 0 until config.cols) {
            val player = board[row][col] ?: continue
            val winCells = findWinCells(board, row, col, player, config)
            if (winCells != null) return GameResult.Win(player, winCells)
        }
    }
    return if (isBoardFull(board, config)) GameResult.Draw else GameResult.InProgress
}

fun computeResult(
    board: List<List<Player?>>,
    row: Int,
    col: Int,
    player: Player,
    config: GameConfig,
): GameResult {
    val winCells = findWinCells(board, row, col, player, config)
    if (winCells != null) return GameResult.Win(player, winCells)
    return if (isBoardFull(board, config)) GameResult.Draw else GameResult.InProgress
}
