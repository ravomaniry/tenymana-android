package mg.maniry.tenymana.api

import android.content.Context
import java.io.File

interface FileApi {
    fun readText(path: String): String?
    fun writeText(path: String, content: String)
    fun readDir(path: String): List<String>
}

class FileApiImpl(context: Context) : FileApi {
    private val rootDir = context.filesDir

    override fun readText(path: String): String? {
        val f = File(rootDir, path)
        return if (f.exists()) f.readText() else null
    }

    override fun writeText(path: String, content: String) {
        val f = File(rootDir, path)
        if (f.exists()) {
            f.writeText(content)
        }
    }

    override fun readDir(path: String): List<String> {
        val dir = File(rootDir, path)
        return dir.list()?.toList() ?: emptyList()
    }
}
