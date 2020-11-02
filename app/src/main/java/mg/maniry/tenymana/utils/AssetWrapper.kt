package mg.maniry.tenymana.utils

import android.content.Context

interface AssetWrapper {
    fun list(dir: String): Array<String>?
    fun readText(path: String): String
}

class AssetWrapperImpl(
    context: Context
) : AssetWrapper {
    private val assets = context.assets

    override fun list(dir: String): Array<String>? {
        return assets.list(dir)
    }

    override fun readText(path: String): String {
        return assets.open(path).bufferedReader().use { it.readText() }
    }
}
