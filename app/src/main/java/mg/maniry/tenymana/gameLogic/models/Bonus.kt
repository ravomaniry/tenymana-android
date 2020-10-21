package mg.maniry.tenymana.gameLogic.models

data class Bonus(
    val name: String,
    val point: Int,
    val price: Int
) {
    fun doublePriced(): Bonus {
        return this.copy(price = price * 2)
    }
}
