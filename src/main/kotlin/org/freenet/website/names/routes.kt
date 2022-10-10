package org.freenet.website.names

import kweb.routing.RouteReceiver

fun RouteReceiver.connectNamesRoutes() {
    path("/success") {
        renderSuccess()
    }

    path("/cancel") {
        renderCancel()
    }

    path("/names") {
        renderNamesLanding()
    }
}