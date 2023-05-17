package org.freenet.website.pages

import kweb.components.Component
import kweb.div
import kweb.state.render
import mu.two.KotlinLogging
import org.freenet.website.util.retrievePage

private val logger = KotlinLogging.logger { }

private val introHtml =
    retrievePage("https://raw.githubusercontent.com/wiki/freenet/locutus/Intro.md")

fun Component.intro() {
    render(introHtml) { introHtml ->
        if (introHtml == null) {
            div().text("Loading latest news...")
        } else {
            div().innerHTML(introHtml)
        }
    }
}
