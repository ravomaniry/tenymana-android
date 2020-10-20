package mg.maniry.tenymana.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface FsHelper {
    suspend fun <T> readJson(path: String, type: Class<T>): T?
    suspend fun <T> writeJson(path: String, data: T, type: Class<T>)
    suspend fun list(path: String): List<String>
}

class FsHelperImpl(
    private val fileApi: FileApi
) : FsHelper {
    private val json = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private suspend fun <T> readAndTransform(path: String, transform: (String) -> T): T? {
        return withContext(Dispatchers.IO) {
            val content = fileApi.readText(path)
            return@withContext content?.let { transform(content) }
        }
    }

    private suspend fun writeText(path: String, content: String) {
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
