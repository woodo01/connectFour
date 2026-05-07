package connectfour

import kotlinx.browser.localStorage
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val STORAGE_KEY = "connectfour_save"

@Serializable
data class SavedCell(val row: Int, val col: Int)

@Serializable
data class SavedGame(
    val config: GameConfig,
    val board: List<List<Player?>>,
    val currentPlayer: Player,
    val lastPlaced: SavedCell?,
) {
    companion object {
        fun load(): SavedGame? = try {
            localStorage.getItem(STORAGE_KEY)?.let { Json.decodeFromString(it) }
        } catch (_: Exception) {
            null
        }

        fun clear() {
            localStorage.removeItem(STORAGE_KEY)
        }
    }

    fun save() {
        localStorage.setItem(STORAGE_KEY, Json.encodeToString(this))
    }
}
