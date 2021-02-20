package mg.maniry.tenymana.repositories.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ApiClient(context: Context) {
    private val json = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val queue = newRequestQueue(context)

    private suspend fun fetchText(url: String, tag: String): String {
        return suspendCoroutine { coroutine ->
            val req = StringRequest(
                Request.Method.GET,
                url,
                Response.Listener<String> { coroutine.resumeWith(Result.success(it)) },
                Response.ErrorListener { coroutine.resumeWithException(it) }
            )
            req.tag = tag
            queue.add(req)
        }
    }

    suspend fun <T> fetchJson(url: String, tag: String, type: Class<T>): T {
        val adapter = json.adapter(type)
        return withContext(Dispatchers.IO) {
            val txt = fetchText(url, tag)
            adapter.fromJson(txt) ?: throw (IOException("Invalid json"))
        }
    }

    fun cancel(tag: String) {
        queue.cancelAll(tag)
    }
}
