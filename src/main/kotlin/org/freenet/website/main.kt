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

package org.freenet.website

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kweb.*
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.plugins.staticFiles.ResourceFolder
import kweb.plugins.staticFiles.StaticFilesPlugin
import mu.KotlinLogging
import org.freenet.website.util.HealthCheckPlugin
import org.freenet.website.util.StripeRoutePlugin
import org.freenet.website.util.recordVisit

private val logger = KotlinLogging.logger {  }

const val usernameTableName = "reservedUsernames"
const val timeToReserveName = 60 * 1000 * 15//15 minutes

val isLocalTestingMode : Boolean = System.getenv("FREENET_SITE_LOCAL_TESTING").equals("true", true)

//TODO Google Authentication can fail in the first few seconds of a pod existing. Need to add check to
//TODO make sure this succeeds, and call it again on fail


fun main() {

    logger.info("Starting Freenet Site, isLocalTestingMode: $isLocalTestingMode")

    Kweb(port = 8080, debug = isLocalTestingMode, plugins = listOf(fomanticUIPlugin, HealthCheckPlugin, StripeRoutePlugin(),
        StaticFilesPlugin(ResourceFolder("static"), "/static"))) {
        logger.info("Received inbound HTTP(S) connection from ${this.httpRequestInfo.remoteHost}")

        doc.head {

            element("link").new {
                parent.setAttribute("rel", "icon")
                parent.setAttribute("href", "/static/rabbit-logo.svg")
                parent.setAttribute("type", "image/svg+xml")
            }

            title().text("Freenet")
            element("meta").setAttribute("content", "width=device-width, initial-scale=1").setAttribute("name", "viewport")
          //  element("link").setAttribute("rel", "stylesheet").setAttribute("href", "/static/checkout.css")
            element("link").setAttribute("rel", "stylesheet").setAttribute("href", "/static/homepage.css")
            element("script").setAttribute("src", "https://js.stripe.com/v3/")
            element("script").setAttribute("src", "/static/checkout.js")

        }
        doc.body {
            routes()

            p().classes("page-end-spacer")
        }

        GlobalScope.launch {
            recordVisit(this@Kweb.httpRequestInfo)
        }
    }

}
