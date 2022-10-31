package org.freenet.website

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kweb.*
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.plugins.staticFiles.ResourceFolder
import kweb.plugins.staticFiles.StaticFilesPlugin
import kweb.state.Component
import kweb.state.render
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
12
    Kweb(port = 8080, debug = isLocalTestingMode, plugins = listOf(fomanticUIPlugin, HealthCheckPlugin, StripeRoutePlugin(),
        StaticFilesPlugin(ResourceFolder("static"), "/static"))) {
        logger.info("Received inbound HTTP(S) connection from ${this.httpRequestInfo.remoteHost}")

        doc.head {

            title().text("Freenet")

            render(RabbitLogo)

            element("meta").set("content", "width=device-width, initial-scale=1").set("name", "viewport")
          //  element("link").setAttribute("rel", "stylesheet").setAttribute("href", "/static/checkout.css")
            element("link")
                .set("rel", "stylesheet")
                .set("href", "/static/homepage.css")
            element("script")["src"] = "https://js.stripe.com/v3/"
            element("script")["src"] = "/static/checkout.js"

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

object RabbitLogo : Component<Unit> {
    override fun render(elementCreator: ElementCreator<*>) {
        with(elementCreator) {
            element("link").new {
                element {
                    set("rel", "icon")
                    set("href", "/static/rabbit-logo.svg")
                    set("type", "image/svg+xml")
                }
            }
        }
    }

}