package mg.maniry.tenymana.api

import android.content.Context
import android.content.res.AssetManager
import java.io.File

interface FileApi {
    val assetsManager: AssetManager
    fun exists(path: String): Boolean
    fun readText(path: String): String?
    fun writeText(path: String, content: String)
    fun readDir(path: String): List<String>
}

class FileApiImpl(context: Context) : FileApi {
    private val rootDir = context.filesDir
    override val assetsManager: AssetManager = context.assets

    override fun exists(path: String): Boolean {
        return File(rootDir, path).exists()
    }

    override fun readText(path: String): String? {
        val f = File(rootDir, path)
        return if (f.exists()) f.readText() else null
    }

    override fun writeText(path: String, content: String) {
        File(rootDir, path).apply {
            mkParentDirs()
            if (exists()) {
                delete()
            }
            this.writeText(content)
        }
    }

    override fun readDir(path: String): List<String> {
        val dir = File(rootDir, path)
        return dir.list()?.toList() ?: emptyList()
    }
}

private fun File.mkParentDirs() {
    val p = parent
    if (p != null) {
        File(p).mkdirs()
    }
}
