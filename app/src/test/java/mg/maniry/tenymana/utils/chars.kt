package mg.maniry.tenymana.utils

import mg.maniry.tenymana.gameLogic.models.CharAddress
import mg.maniry.tenymana.gameLogic.models.Character

fun chars(vararg v: Char?): List<Character?> {
    return v.map {
        it?.let { Character(it.toUpperCase(), it.toLowerCase()) }
    }
}

fun addresses(vararg xy: Int?): List<CharAddress?> {
    val list = mutableListOf<CharAddress?>()
    for (i in 0 until xy.size - 1) {
        if (i % 2 == 0) {
            val wI = xy[i]
            val cI = xy[i + 1]
            if (wI == null || cI == null) {
                list.add(null)
            } else {
                list.add(CharAddress(wI, cI))
            }
        }
    }
    return list
}
