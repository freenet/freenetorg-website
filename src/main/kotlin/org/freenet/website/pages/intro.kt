package org.freenet.website.pages

import kweb.components.Component
import kweb.div
import mu.two.KotlinLogging
import retrieveMDPage

private val logger = KotlinLogging.logger { }

fun Component.intro() {
    val introHtml = retrieveMDPage("https://raw.githubusercontent.com/wiki/freenet/freenet-core/Intro.md")

    div().innerHTML(introHtml)
}
