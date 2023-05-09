package org.freenet.website.util

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kweb.state.KVal
import kweb.state.KVar
import mu.two.KotlinLogging
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.time.Duration

private val logger = KotlinLogging.logger { }

fun retrievePage(url: String, refreshDuration: Duration = Duration.ofHours(1)) : KVal<String?> {
    val scope = CoroutineScope(Dispatchers.IO)

    val client = HttpClient(CIO)
    val htmlKVar = KVar<String?>(null)
    var etag: String? = null
    scope.launch {
        while (true) {
            try {
                val response: HttpResponse = client.get(url) {
                    etag?.let { header("If-None-Match", it) }
                }

                if (response.status.value == 304) {
                    // Not Modified
                    logger.info { "Page hasn't changed" }
                } else {
                    val newEtag = response.headers["ETag"]
                    if (newEtag != null) {
                        etag = newEtag
                    }

                    val responseBody = response.bodyAsText()

                    val parser = Parser.builder().build()
                    val document = parser.parse(responseBody)
                    val renderer = HtmlRenderer.builder().build()
                    val html = renderer.render(document)
                    htmlKVar.value = html
                }
            } catch (e: ClientRequestException) {
                logger.error(e) { "Failed to retrieve latest news due to client request error" }
            } catch (e: Exception) {
                logger.error(e) { "Failed to retrieve latest news due to an unexpected error" }
            }
            delay(refreshDuration)
        }
    }
    return htmlKVar
}