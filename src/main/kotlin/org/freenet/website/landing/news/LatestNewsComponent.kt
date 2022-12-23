package org.freenet.website.landing.news

import kweb.components.Component
import kweb.div
import kweb.h3
import kweb.plugins.fomanticUI.fomantic
import kweb.state.ObservableList
import kweb.state.renderEach

fun Component.latestNewsComponent(newsItems: ObservableList<NewsItem>) {
    h3(fomantic.ui.text).text("Latest News")
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
