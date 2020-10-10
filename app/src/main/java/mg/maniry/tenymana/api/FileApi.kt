package mg.maniry.tenymana.api

import android.content.Context
import java.io.File

interface FileApi {
    fun readText(path: String): String
    fun writeText(path: String, content: String)
}

class FileApiImpl(context: Context) : FileApi {
    private val rootDir = context.filesDir

    override fun readText(path: String): String {
        return File(rootDir, path).readText()
    }

    override fun writeText(path: String, content: String) {
        return File(rootDir, path).writeText(content)
    }
}
