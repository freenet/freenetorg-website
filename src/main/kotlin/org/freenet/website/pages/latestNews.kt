package org.freenet.website.pages

import kweb.*
import kweb.components.Component
import kweb.plugins.fomanticUI.fomantic
import kweb.state.ObservableList
import kweb.state.renderEach
import org.freenet.website.pages.resources.NewsItem
import java.util.*

fun Component.latestNews(newsItems: ObservableList<NewsItem>) {
    h3().text("Latest News")

    ul {

        renderEach(newsItems) { newsItem ->
            val prettyDate = humanize.Humanize.formatDate(newsItem.date, "MMMM d, yyyy")
            li().innerHTML(
                """
                <span class="has-text-weight-bold">$prettyDate</span>
                <span>${newsItem.description}</span>
            """.trimIndent())
        }
    }
}

val dummyNewsItems = ObservableList(
    listOf(
        NewsItem(Date(), "This is the first news item", true),
        NewsItem(Date(), "This is the second news item", false),
        NewsItem(Date(), "This is the third news item", true),
    )
)