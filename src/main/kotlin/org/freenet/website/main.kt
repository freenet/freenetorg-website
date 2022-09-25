package org.freenet.website

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.stripe.model.Customer
import com.stripe.model.PaymentIntent
import io.ktor.server.plugins.*
import io.ktor.util.date.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kweb.*
import kweb.plugins.fomanticUI.fomantic
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.plugins.staticFiles.ResourceFolder
import kweb.plugins.staticFiles.StaticFilesPlugin
import kweb.state.KVal
import kweb.state.KVar
import kweb.state.render
import kweb.util.json
import mu.KotlinLogging
import org.freenet.website.util.StripeRoutePlugin
import java.util.*
import kotlin.collections.HashMap

private val logger = KotlinLogging.logger {  }

const val usernameTableName = "reservedUsernames"
const val timeToReserveName = 60 * 1000 * 15//15 minutes

val isLocalTestingMode : Boolean = System.getenv("FREENET_SITE_LOCAL_TESTING").equals("true", true)

//TODO Google Authentication can fail in the first few seconds of a pod existing. Need to add check to
//TODO make sure this succeeds, and call it again on fail


fun main() {

    logger.info("Starting Freenet Site, isLocalTestingMode: $isLocalTestingMode")

    Kweb(port = 8080, debug = isLocalTestingMode, plugins = listOf(fomanticUIPlugin, StripeRoutePlugin(),
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

    }

}
