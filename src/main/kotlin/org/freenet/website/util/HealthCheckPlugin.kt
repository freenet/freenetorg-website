package org.freenet.website.util

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kweb.plugins.KwebPlugin

object HealthCheckPlugin : KwebPlugin() {
    override fun appServerConfigurator(routeHandler: Routing) {
        routeHandler.get("/health") {
            call.respondText("OK")
        }
    }
}