package org.freenet.website.pages

import kweb.components.Component
import kweb.div
import kweb.state.render
import mu.two.KotlinLogging
import org.freenet.website.util.retrievePage

private val logger = KotlinLogging.logger { }

private val latestNewsHtml = retrievePage("https://raw.githubusercontent.com/wiki/freenet/locutus/News.md")

fun Component.latestNews() {
    render(latestNewsHtml) { latestNewsHtml ->
        if (latestNewsHtml == null) {
            div().text("Loading latest news...")
        } else {
            div().innerHTML(latestNewsHtml)
        }
    }
}

