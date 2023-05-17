package org.freenet.website.pages.home

import kweb.components.Component
import kweb.div
import kweb.h2
import kweb.p
import kweb.section
import org.freenet.website.util.iconButton
import java.util.*

/**
 * The main landing page, which provides an overview of Freenet, its mission,
 * and its features.
 */
fun Component.homePage() {

    section { section ->
        section.classes("section")
        h2().classes("subtitle", "is-normal").text("Declare your digital independence")
        intro()
        p()
        learnMoreLinks()
    }

    section { section ->
        section.classes("section")
        latestNews()
    }

}

private fun Component.learnMoreLinks() {
    div { div ->
        div.classes("buttons")
        iconButton(
            html = "Watch Intro Talk",
            href = "https://www.youtube.com/watch?v=d31jmv5Tx5k",
            icon = arrayOf("fas", "fa-video"),
            buttonClasses = arrayOf("button", "is-medium-blue")
        )
        // User Manual
        iconButton(
            html = "Read User Manual",
            href = "https://docs.freenet.org/",
            icon = arrayOf("fas", "fa-book"),
            buttonClasses = arrayOf("button", "is-medium-teal")
        )
        // Community Chat
        iconButton(
            html = "Chat on Matrix",
            href = "https://matrix.to/#/#freenet-locutus:matrix.org",
            icon = arrayOf("fas", "fa-comments"),
            buttonClasses = arrayOf("button", "is-medium-purple")
        )
        // GitHub Repository
        iconButton(
            html = "Visit GitHub",
            href = "https://github.com/freenet/locutus",
            icon = arrayOf("fab", "fa-github"),
            buttonClasses = arrayOf("button", "is-medium-orange")
        )
    }
}
