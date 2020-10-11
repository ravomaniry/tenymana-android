package mg.maniry.tenymana.utils

import java.lang.Math.random
import kotlin.math.round

interface Random {
    fun int(min: Int, max: Int): Int

    fun <T> from(list: List<T>): T {
        return if (list.size == 1) list[0] else list[this.int(0, list.size - 1)]
    }
}

class RandomImpl : Random {
    override fun int(min: Int, max: Int): Int {
        if (min <= max) {
            return min + round(random() * (max - min)).toInt()
        }
        return min
    }
}
