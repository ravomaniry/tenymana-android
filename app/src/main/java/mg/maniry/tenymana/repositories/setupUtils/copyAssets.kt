package mg.maniry.tenymana.repositories.setupUtils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mg.maniry.tenymana.api.FsHelper

suspend fun FsHelper.copyAssets(srcDir: String, destDir: String) {
    withContext(Dispatchers.IO) {
        val names = assets.list(srcDir)
        if (names != null) {
            for (name in names) {
                val srcPath = "$srcDir/$name"
                val destPath = "$destDir/$name"
                if (!exists(srcPath)) {
                    val content = assets.readText(srcPath)
                    writeText(destPath, content)
                }
            }
        }
    }
}
