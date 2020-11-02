package mg.maniry.tenymana.repositories.setupUtils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mg.maniry.tenymana.api.FsHelper

suspend fun FsHelper.copyAssets(dir: String) {
    withContext(Dispatchers.IO) {
        val names = assetManager.list(dir)
        if (names != null) {
            for (name in names) {
                val path = "$dir/$name"
                if (!exists(path)) {
                    val content = assetManager.open(path).bufferedReader().use { it.readText() }
                    writeText(path, content)
                }
            }
        }
    }
}
