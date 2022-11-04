package org.freenet.website.landing.news

import kweb.ElementCreator
import kweb.div
import kweb.h3
import kweb.plugins.fomanticUI.fomantic
import kweb.state.Component
import kweb.state.ObservableList
import kweb.state.renderEach

class LatestNewsComponent(private val newsItems : ObservableList<NewsItem>) : Component {
    override fun render(elementCreator: ElementCreator<*>) {
        with(elementCreator) {
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

    }

}