package mg.maniry.tenymana.helpers

import mg.maniry.tenymana.utils.Random

class RandomMock : Random {
    val intFn = Fn<Int>()
    val fromFn = Fn<Any>()
    val doubleFn = Fn<Double>()

    override fun int(min: Int, max: Int) = intFn(min, max)

    override fun double() = doubleFn()

    @Suppress("unchecked_cast")
    override fun <T> from(list: List<T>) = fromFn(list) as T
}
