package mg.maniry.tenymana.api

import android.content.res.AssetManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface FsHelper {
    val assetManager: AssetManager
    suspend fun exists(path: String): Boolean
    suspend fun <T> readJson(path: String, type: Class<T>): T?
    suspend fun <T> writeJson(path: String, data: T, type: Class<T>)
    suspend fun writeText(path: String, content: String)
    suspend fun readText(path: String): String?
    suspend fun list(path: String): List<String>
}

class FsHelperImpl(
    private val fileApi: FileApi
) : FsHelper {
    private val json = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    override val assetManager: AssetManager = fileApi.assetsManager

    override suspend fun exists(path: String): Boolean {
        return withContext(Dispatchers.IO) {
            fileApi.exists(path)
        }
    }

    private suspend fun <T> readAndTransform(path: String, transform: (String) -> T): T? {
        val content = readText(path)
        return content?.let { transform(content) }
    }

    override suspend fun readText(path: String): String? {
        return withContext(Dispatchers.IO) {
            fileApi.readText(path)
        }
    }

    override suspend fun writeText(path: String, content: String) {
        withContext(Dispatchers.IO) {
            fileApi.writeText(path, content)
        }
    }

    override suspend fun <T> readJson(path: String, type: Class<T>): T? {
        val adapter = json.adapter(type)
        return readAndTransform(path) { adapter.fromJson(it) }
    }

    override suspend fun <T> writeJson(path: String, data: T, type: Class<T>) {
        val adapter = json.adapter(type)
        writeText(path, adapter.toJson(data))
    }

    override suspend fun list(path: String): List<String> {
        return withContext(Dispatchers.IO) {
            fileApi.readDir(path)
        }
    }
}
