/*
 *         Freenet.org - web application
 *         Copyright (C) 2022  Freenet Project Inc
 *
 *         This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as
 *         published by the Free Software Foundation, either version 3 of the
 *         License, or (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

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