package org.freenet.website

import kweb.*
import kweb.html.BodyElement
import org.freenet.website.names.*

fun ElementCreator<BodyElement>.routes() {
    route {

        path("") {
            landingPage()
        }

        connectNamesRoutes()
    }

}