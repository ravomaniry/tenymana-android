package mg.maniry.tenymana.repositories.setupUtils

import mg.maniry.tenymana.api.FsHelper

suspend fun FsHelper.copyAssets(dir: String) {
    val names = assetManager.list(dir)
    if (names != null) {
        for (name in names) {
            val path = "$dir/$name"
            if (!exists(path)) {
                assetManager.open(path).copyTo(newOutputStream(path))
            }
        }
    }
}
