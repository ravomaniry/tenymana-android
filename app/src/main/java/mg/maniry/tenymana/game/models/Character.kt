package mg.maniry.tenymana.game.models

import java.util.*

data class Character(
    val value: String,
    val compValue: String,
    val resolved: Boolean = false
) {

    fun toSlotChar(): Character {
        return Character(compValue.toUpperCase(Locale.ROOT), compValue)
    }

    fun isSameAs(c: Character): Boolean {
        return compValue == c.compValue
    }
}
