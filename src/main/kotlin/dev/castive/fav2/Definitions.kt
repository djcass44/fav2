package dev.castive.fav2

object Definitions {
    private val possibleNames = arrayOf(
        "favicon",
        "apple-touch-icon"
    )
    fun contains(str: String): Boolean {
        return possibleNames.any { it.contains(str) }
    }
}