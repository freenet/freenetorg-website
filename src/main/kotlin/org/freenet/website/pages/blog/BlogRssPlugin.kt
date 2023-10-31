package org.freenet.website.pages.blog

import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kweb.plugins.KwebPlugin
import org.jsoup.Jsoup
import java.time.Instant

class BlogRssPlugin : KwebPlugin() {
    data class CachedRSS(val rss: String, val generated: Instant)

    @Volatile var cachedRss: CachedRSS? = null

    private fun getRss(): CachedRSS {
        val discussionsGenerated = GitHubDiscussions.discussions?.generated ?: Instant.now()
        val cached = cachedRss
        if (cached == null || cached.generated.isBefore(discussionsGenerated)) {
            val generatedRss = generateRss()
            cachedRss = CachedRSS(generatedRss, discussionsGenerated)
        }
        return cachedRss!!
    }

    private fun generateRss() : String {
        val rss = StringBuilder()
        rss.append("""
<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:sy="http://purl.org/rss/1.0/modules/syndication/">
    <channel>
        <title>Freenet Blog</title>
        <link>https://freenet.org/blog</link>
        <description>News and updates from the Freenet Project</description>
        <language>en-us</language>
        <ttl>1440</ttl>
        <sy:updatePeriod>daily</sy:updatePeriod>
        <sy:updateFrequency>1</sy:updateFrequency>
        """.trimIndent())
        val discussions = GitHubDiscussions.discussions
        if (discussions != null) {
            for (blog in discussions.discussions.take(20)) {
                val html = Jsoup.parse(blog.bodyHTML)
                // Get text of first <p> from html
                val firstParagraph = html.select("p").first()?.text() ?: ""
                rss.append(
                    """
        <item>
            <title>${blog.title}</title>
            <link>https://freenet.org${blog.freenetUrlPath}</link>
            <pubDate>${blog.createdAt}</pubDate>
            <description>${firstParagraph}</description>
        </item> """.trimIndent()
                )
            }
        }
        rss.append("""
   </channel>
</rss>
        """.trimIndent())

        return rss.toString()
    }

    override fun appServerConfigurator(routeHandler: Routing) {
        routeHandler.get("/blog.rss") {
            val rssCache = getRss()
            val etag = rssCache.generated.hashCode().toString()
            val etagHeader = call.request.headers[HttpHeaders.IfNoneMatch]
            if (etagHeader == etag) {
                call.respond(HttpStatusCode.NotModified)
            } else {
                call.response.header(HttpHeaders.ETag, etag)
                call.respondText(
                    text = rssCache.rss,
                    contentType = ContentType.Application.Xml,
                    status = OK
                )
            }
        }
    }
}