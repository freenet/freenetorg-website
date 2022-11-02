package org.freenet.website

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
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

private val logger = KotlinLogging.logger { }

const val usernameTableName = "reservedUsernames"
const val timeToReserveName = 60 * 1000 * 15//15 minutes

val isLocalTestingMode: Boolean = System.getenv("FREENET_SITE_LOCAL_TESTING").equals("true", true)

//TODO Google Authentication can fail in the first few seconds of a pod existing. Need to add check to
//TODO make sure this succeeds, and call it again on fail


fun main() {

    val scope = MainScope()

    logger.info("Starting Freenet Site, isLocalTestingMode: $isLocalTestingMode")

    Kweb(
        port = 8080, debug = isLocalTestingMode, plugins = listOf(
            fomanticUIPlugin, HealthCheckPlugin, StripeRoutePlugin(),
            StaticFilesPlugin(ResourceFolder("static"), "/static")
        )
    ) {
        logger.info("Received inbound HTTP(S) connection from ${this.httpRequestInfo.remoteHost}")

        doc.head {

            title().text("Freenet")

            render(RabbitLogo)

            element("meta") { meta ->
                meta["content"] = "width=device-width, initial-scale=1"
                meta["name"] = "viewport"
            }
            element("link") { link ->
                link["rel"] = "stylesheet"
                link["href"] = "/static/homepage.css"
            }

            element("script")["src"] = "https://js.stripe.com/v3/"
            element("script")["src"] = "/static/checkout.js"

        }
        doc.body {
            routes()

            p().classes("page-end-spacer")
        }

        scope.launch {
            recordVisit(this@Kweb.httpRequestInfo)
        }
    }

}

object RabbitLogo : Component<Unit> {
    override fun render(elementCreator: ElementCreator<*>) {
        with(elementCreator) {
            element("link").new { link ->
                link["rel"] = "icon"
                link["href"] = "/static/rabbit-logo.svg"
                link["type"] = "image/svg+xml"
            }
        }
    }

}