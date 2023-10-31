package org.freenet.website.pages.home

import kweb.components.Component
import kweb.div
import mu.two.KotlinLogging
import retrieveMDPage

private val logger = KotlinLogging.logger { }


fun Component.latestNews() {
    val latestNewsHtml = retrieveMDPage("https://raw.githubusercontent.com/wiki/freenet/freenet-core/News.md")
    div().innerHTML(latestNewsHtml)
}

