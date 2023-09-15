import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import okhttp3.*
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.File
import java.time.Duration

// Define the cache size (10 MB)
const val cacheSize: Long = 10 * 1024 * 1024

// Define the path to the system temporary directory
val cacheDirectory = File(System.getProperty("java.io.tmpdir"), "okhttp-cache")

// Initialize the OkHttp cache
val okhttpCache = Cache(cacheDirectory, cacheSize)

private val notFound = """
    <section class="section">
        <div class="container has-text-centered">
            <h1 class="title">Oops, our website is having some issues.</h1>
            <p class="subtitle">Please 
                <a href="https://matrix.to/#/#freenet-freenet-core:matrix.org">let us know</a>
                if you see this consistently.
            </p>
        </div>
    </section>
""".trimIndent()

private val client = OkHttpClient.Builder()
    .cache(okhttpCache) // add the cache to the OkHttpClient
    .build()

// Define a CacheLoader that parses markdown and converts it to HTML
val loader = object : CacheLoader<String, String>() {
    override fun load(key: String): String {
        val request = Request.Builder()
            .url(key)
            .build()

        val response = client.newCall(request).execute()

        return if (response.isSuccessful) {
            val responseBody = response.body.string() ?: ""

            val parser = Parser.builder().build()
            val document = parser.parse(responseBody)
            val renderer = HtmlRenderer.builder().build()

            renderer.render(document)
        } else {
            notFound
        }
    }
}

// Initialize the parsed HTML cache
val htmlCache: LoadingCache<String, String> = CacheBuilder.newBuilder()
    .maximumSize(1000) // Precautionary limit
    .refreshAfterWrite(Duration.ofMinutes(10))
    .build(loader)

fun retrieveMDPage(url: String): String {
    return htmlCache.get(url)
}
