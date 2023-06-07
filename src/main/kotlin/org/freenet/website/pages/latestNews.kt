package org.freenet.website.pages

import kweb.components.Component
import kweb.div
import kweb.state.render
import mu.two.KotlinLogging
import retrieveMDPage

private val logger = KotlinLogging.logger { }


fun Component.latestNews() {
    val latestNewsHtml = retrieveMDPage("https://raw.githubusercontent.com/wiki/freenet/locutus/News.md")
    div().innerHTML(latestNewsHtml)
}

