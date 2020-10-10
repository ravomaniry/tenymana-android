package mg.maniry.tenymana.game.models

data class BibleVerse(
    val book: String,
    val chapter: Int,
    val verse: Int,
    val text: String,
    val words: List<Word>
) {

    companion object {
        fun fromText(book: String, chapter: Int, verse: Int, text: String): BibleVerse {
            var words = parseText(text)
            if (onlySeparators(words)) {
                words = parseText(removeIgnoreSymbols(text))
            }
            return BibleVerse(book, chapter, verse, text, words)
        }
    }
}

private val separatorRegex = Regex("[^a-zàäèìïòô]", RegexOption.IGNORE_CASE)

private fun parseText(text: String): List<Word> {
    var index = 0
    var wordValue = ""
    var separatorMode = false
    var isBetweenBraces = false
    var isBetweenParenthesis = false
    val words = mutableListOf<Word>()

    fun appendWord(isLastWord: Boolean) {
        if (wordValue.isNotEmpty()) {
            if (!isLastWord || wordValue.trim() != "") {
                words.add(Word.fromValue(wordValue, index, separatorMode))
                separatorMode = false
                wordValue = ""
                index++
            }
        }
    }

    loop@ for (char in text) {
        val v = char.toString()
        when {
            v == "[" || v == "]" -> {
                isBetweenBraces = !isBetweenBraces
                continue@loop
            }
            v == "(" || v == ")" -> {
                isBetweenParenthesis = !isBetweenParenthesis
                continue@loop
            }
            isBetweenBraces || isBetweenParenthesis -> {
                continue@loop
            }
            separatorRegex.matches(v) -> {
                if (!separatorMode) {
                    appendWord(false)
                    separatorMode = true
                }
            }
            separatorMode -> appendWord(false)
        }
        if (!(separatorMode && wordValue.endsWith(" ") && v == " ")) {
            wordValue += v
        }
    }
    appendWord(true)
    return words
}

private fun removeIgnoreSymbols(text: String): String {
    return text.replace(Regex("[()\\[\\]]"), "")
}

private fun onlySeparators(words: List<Word>): Boolean {
    words.forEach { word ->
        if (!word.isSeparator) {
            return false
        }
    }
    return true
}
