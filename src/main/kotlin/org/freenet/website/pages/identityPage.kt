package org.freenet.website.pages

import kweb.components.Component
import kweb.div
import kweb.h1
import kweb.p

fun Component.identityPage() {
    div {
        h1().text("Claim your identity")
        p().text("This is a placeholder page for claiming your identity.")
    }
}