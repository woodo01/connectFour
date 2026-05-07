package connectfour

import kotlin.test.*

class GameLogicTest {

    // '.' = empty, 'P' = PURPLE, 'K' = PINK
    private fun board(vararg rows: String): List<List<Player?>> =
        rows.map { row ->
            row.map { ch ->
                when (ch) {
                    'P'  -> Player.PURPLE
                    'K'  -> Player.PINK
                    else -> null
                }
            }
        }

    @Test
    fun emptyBoardHasCorrectDimensions() {
        val b = emptyBoard(6, 7)
        assertEquals(6, b.size)
        b.forEach { assertEquals(7, it.size) }
    }


    @Test
    fun horizontalWinDetected() {
        val b = board(
            ".......",
            ".......",
            ".......",
            ".......",
            ".......",
            "PPPP..."
        )
        assertNotNull(findWinCells(b, 5, 0, Player.PURPLE, GameConfig(6, 7, 4)))
    }

    @Test
    fun verticalWinDetected() {
        val b = board(
            ".......",
            ".......",
            "P......",
            "P......",
            "P......",
            "P......"
        )
        assertNotNull(findWinCells(b, 2, 0, Player.PURPLE, GameConfig(6, 7, 4)))
    }

    @Test
    fun diagonalWinDetected() {
        val b = board(
            ".......",
            ".......",
            "...P...",
            "..P....",
            ".P.....",
            "P......"
        )
        assertNotNull(findWinCells(b, 5, 0, Player.PURPLE, GameConfig(6, 7, 4)))
    }

    @Test
    fun antiDiagonalWinDetected() {
        val b = board(
            ".......",
            ".......",
            "P......",
            ".P.....",
            "..P....",
            "...P..."
        )
        assertNotNull(findWinCells(b, 2, 0, Player.PURPLE, GameConfig(6, 7, 4)))
    }


    @Test
    fun winCellsContainAtLeastWinLength() {
        val b = board(
            ".......",
            ".......",
            ".......",
            ".......",
            ".......",
            "PPPPP.."
        )
        val cells = findWinCells(b, 5, 0, Player.PURPLE, GameConfig(6, 7, 4))
        assertNotNull(cells)
        assertTrue(cells.size >= 4)
    }

    @Test
    fun customWinLengthIsRespected() {
        val b = board(
            ".....",
            ".....",
            ".....",
            ".....",
            "PPPPP"
        )
        // win length 5 — five in a row should win
        assertNotNull(findWinCells(b, 4, 0, Player.PURPLE, GameConfig(5, 5, 5)))
        assertNull(findWinCells(b, 4, 0, Player.PURPLE, GameConfig(5, 5, 6)))
    }


    @Test
    fun detectsDraw() {
        // no 4-in-a-row exists
        val b = board(
            "PPKK",
            "KKPP",
            "PPKK",
            "KKPP"
        )
        val result = computeResult(b, 0, 3, Player.PINK, GameConfig(4, 4, 4))
        assertEquals(GameResult.Draw, result)
    }

}
