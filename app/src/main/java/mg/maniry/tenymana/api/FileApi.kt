package mg.maniry.tenymana.api

import android.content.Context
import java.io.File

interface FileApi {
    fun exists(path: String): Boolean
    fun readText(path: String): String?
    fun writeText(path: String, content: String)
    fun readDir(path: String): List<String>
    fun delete(path: String): Boolean
}

class FileApiImpl(context: Context) : FileApi {
    private val rootDir = context.filesDir

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

    override fun delete(path: String): Boolean {
        val file = File(rootDir, path)
        return if (file.exists()) file.delete() else false
    }
}

private fun File.mkParentDirs() {
    val p = parent
    if (p != null) {
        File(p).mkdirs()
    }
}
