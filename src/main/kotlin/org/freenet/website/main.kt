package org.freenet.website

import org.freenet.website.pages.about.aboutPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kweb.*
import kweb.components.Component
import kweb.html.HeadElement
import kweb.plugins.staticFiles.ResourceFolder
import kweb.plugins.staticFiles.StaticFilesPlugin
import kweb.state.ObservableList
import kweb.state.render
import mu.two.KotlinLogging
import org.freenet.website.db.db
import org.freenet.website.apis.PivotalTracker
import org.freenet.website.pages.dummyNewsItems
import org.freenet.website.pages.homePage
import org.freenet.website.pages.joinUs.joinUsPage
import org.freenet.website.pages.resources.NewsItem
import org.freenet.website.pages.resources.retrieveNews
import org.freenet.website.pages.developers.developersPage
import org.freenet.website.pages.resources.resourcesPage
import org.freenet.website.util.HealthCheckPlugin
import org.freenet.website.util.UrlToPathSegmentsRF
import org.freenet.website.util.recordVisit

private val logger = KotlinLogging.logger { }

val isLocalTestingMode: Boolean = System.getenv("FREENET_SITE_LOCAL_TESTING").equals("true", true)

fun main() {

    val scope = CoroutineScope(Dispatchers.IO)

    // Initial retrieval of PT releases to avoid a delay the first time it's used
    PivotalTracker.releases

    logger.info("Starting Freenet Site, isLocalTestingMode: $isLocalTestingMode")

    Kweb(
        port = 8080,
        debug = isLocalTestingMode,
        plugins = listOf(
            HealthCheckPlugin,
            StaticFilesPlugin(ResourceFolder("static"), "/static",)
        )
    ) {
        doc.head {

            configureHead()
        }
        doc.body {

            section {
                it.classes("section", "content")

                div { div ->
                    div.classes("container")

                    val nav = pathToNavItem()

                    navComponent(nav)

                    render(nav) { activeNavItem ->
                        when (activeNavItem) {
                            NavItem.About -> aboutPage()
                            NavItem.Home -> homePage(latestNewsItems)
                            NavItem.Developers -> developersPage()
                            NavItem.JoinUs -> joinUsPage()
                            NavItem.Resources -> resourcesPage()
                            else -> error("Unknown NavItem: $activeNavItem")
                        }
                    }
                }
            }
        }

        scope.launch {
            recordVisit(this@Kweb.httpRequestInfo)
        }
    }

}

private fun WebBrowser.pathToNavItem() = url.map(UrlToPathSegmentsRF)
    .map { pathSegments ->
        if (pathSegments.isEmpty()) {
            NavItem.Home
        } else {
            when (pathSegments[0]) {
                "about" -> NavItem.About
                "dev" -> NavItem.Developers
                "join" -> NavItem.JoinUs
                "resources" -> NavItem.Resources
                else -> NavItem.Home
            }
        }
    }

typealias HeadComponent = ElementCreator<HeadElement>

private fun HeadComponent.configureHead() {
    title().text("Freenet")

    element("script") {
        it["src"] = "/static/freenet.js"
    }

    element("link") {
        it["rel"] = "stylesheet"
        it["href"] = "/static/fontawesome/css/fontawesome.min.css"
    }

    element("link") {
        it["rel"] = "stylesheet"
        it["href"] = "/static/fontawesome/css/solid.min.css"
    }

    element("link") {
        it["rel"] = "stylesheet"
        it["href"] = "/static/fontawesome/css/brands.min.css"
    }

    element("link") {
        it["rel"] = "stylesheet"
        it["href"] = "/static/bulma.min.css"
    }

    element("link") {
        it["rel"] = "stylesheet"
        it["href"] = "/static/freenetorg.css"
    }

    element("meta") { meta ->
        meta["content"] = "width=device-width, initial-scale=1"
        meta["name"] = "viewport"
    }

  //  element("script")["src"] = "https://js.stripe.com/v3/"
  //  element("script")["src"] = "/static/checkout.js"
}

/*
 * These are the same for all users so we only need one globally
 */
private val latestNewsItems: ObservableList<NewsItem> = run {
    val latestNewsItems = if (db != null) {
        logger.info("Retrieving Latest News")
        retrieveNews(db)
    } else {
        logger.info("Using dummy news as Firestore is not available")
        dummyNewsItems
    }
    latestNewsItems
}

fun Component.rabbitLogoComponent() {
    element("link").new { link ->
        link["rel"] = "icon"
        link["href"] = "/static/rabbit-logo.svg"
        link["type"] = "image/svg+xml"
    }
}
