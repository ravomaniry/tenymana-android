package mg.maniry.tenymana.game.models

data class Character(
    val value: Char,
    val compValue: Char,
    val resolved: Boolean = false
) {
    fun toGridChar(): Character {
        return Character(compValue.toUpperCase(), compValue)
    }

    fun isSameAs(c: Character): Boolean {
        return compValue == c.compValue
    }

    override fun toString() = "$value:$compValue"
}

data class CharAddress(
    val wIndex: Int,
    val cIndex: Int
) {
    override fun toString() = "$wIndex/$cIndex"
}
