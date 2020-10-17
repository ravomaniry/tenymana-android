package mg.maniry.tenymana.game.models

data class Word(
    val index: Int,
    val value: String,
    val chars: List<Character>,
    val resolved: Boolean = false,
    val isSeparator: Boolean = false,
    val bonus: Bonus? = null
) {
    val size: Int get() = chars.size
    val resolvedVersion: Word get() = this.copy(resolved = true)
    operator fun get(i: Int) = chars[i]

    val firstUnrevealedIndex: Int
        get() {
            for (i in 0..size) {
                if (!chars[i].resolved) {
                    return i
                }
            }
            return -1;
        }

    fun removeBonus(): Word {
        return this.copy(bonus = null)
    }

    fun startsWith(c: Character): Boolean {
        return size > 0 && chars[0].isSameAs(c)
    }

    fun sameChars(otherChars: List<Character>): Boolean {
        if (otherChars.size == size) {
            otherChars.forEachIndexed { i, c ->
                if (c.compValue != chars[i].compValue) {
                    return false
                }
            }
            return true
        }
        return false
    }

    fun copyWithChar(index: Int, char: Character): Word {
        val nextChars = chars.toMutableList()
        nextChars[index] = char
        return this.copy(chars = nextChars)
    }

    override fun toString() = "[$value:$resolved]"

    companion object {
        fun fromValue(value: String, index: Int, isSeparator: Boolean = false): Word {
            val chars = value.toList().map {
                Character(it, compValue = getLetterComparisonValue(it))
            }
            return Word(index, value, chars, isSeparator, isSeparator)
        }
    }
}

private fun getLetterComparisonValue(char: Char): Char {
    return when (val compValue = char.toLowerCase()) {
        'à', 'ä' -> 'a'
        'ô', 'ò' -> 'o'
        'è' -> 'e'
        'ì', 'ï' -> 'i'
        else -> compValue
    }
}
