package org.freenet.website

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kweb.*
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.plugins.staticFiles.ResourceFolder
import kweb.plugins.staticFiles.StaticFilesPlugin
import kweb.state.Component
import kweb.state.ObservableList
import kweb.state.render
import mu.KotlinLogging
import org.freenet.website.db.db
import org.freenet.website.landing.dummyNewsItems
import org.freenet.website.landing.news.NewsItem
import org.freenet.website.landing.news.retrieveNews
import org.freenet.website.util.HealthCheckPlugin
import org.freenet.website.util.StripeRoutePlugin
import org.freenet.website.util.recordVisit

private val logger = KotlinLogging.logger { }

val isLocalTestingMode: Boolean = System.getenv("FREENET_SITE_LOCAL_TESTING").equals("true", true)

fun main() {

    val scope = CoroutineScope(Dispatchers.IO)

    logger.info("Starting Freenet Site, isLocalTestingMode: $isLocalTestingMode")

    Kweb(
        port = 8080,
        debug = isLocalTestingMode,
        plugins = listOf(
            fomanticUIPlugin,
            HealthCheckPlugin,
            StripeRoutePlugin(),
            StaticFilesPlugin(ResourceFolder("static"), "/static")
        )
    ) {
        doc.head {

            title().text("Freenet")

            render(RabbitLogo)

            render(ConfigureHeadComponent)
        }
        doc.body {
            render(RoutesComponent(latestNewsItems))

            p().classes("page-end-spacer")
        }

        scope.launch {
            recordVisit(this@Kweb.httpRequestInfo)
        }
    }

}

private object ConfigureHeadComponent : Component {
    override fun render(elementCreator: ElementCreator<*>) {
        with(elementCreator) {
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
    }

}

/*
 * These are the same for all users so we only need one globally
 */
private val latestNewsItems : ObservableList<NewsItem> = run {
    val latestNewsItems = if (db != null) {
        logger.info("Retrieving Latest News")
        retrieveNews(db)
    } else {
        logger.info("Using dummy news as Firestore is not available")
        dummyNewsItems
    }
    latestNewsItems
}

object RabbitLogo : Component {
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