package org.freenet.website.pages

import kweb.components.Component
import kweb.div
import kweb.h3
import kweb.plugins.fomanticUI.fomantic
import kweb.state.ObservableList
import kweb.state.renderEach
import org.freenet.website.pages.resources.NewsItem

fun Component.latestNews(newsItems: ObservableList<NewsItem>) {
    h3().text("Latest News")

    div(fomantic.ui.bulleted.list) {
        renderEach(newsItems) { newsItem ->
            div(fomantic.item) {
                val prettyDate = humanize.Humanize.formatDate(newsItem.date, "MMMM d, yyyy")

                element.innerHTML(
                    """
                            <B>${prettyDate}:</B> ${newsItem.description}
                        """.trimIndent()
                )
            }
        }
    }
}
