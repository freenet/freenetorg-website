package org.freenet.website

import kweb.ElementCreator
import kweb.html.BodyElement
import kweb.route
import org.freenet.website.landing.landingPage
import org.freenet.website.names.connectNamesRoutes

fun ElementCreator<BodyElement>.routes() {
    route {

        path("") {
            landingPage()
        }

        connectNamesRoutes()
    }

}