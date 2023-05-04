package org.freenet.website

import kweb.components.Component
import kweb.div
import kweb.h1
import kweb.h2
import kweb.section

fun Component.pageTitle() {
    section {
        it.classes("hero", "is-small")
        div {
            it.classes("hero-body")
            h1().classes("title", "is-1").text("Freenet")
            h2().classes("subtitle", "is-medium").text("Declare your digital independence")
        }
    }
}
