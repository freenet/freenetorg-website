package org.freenet.website

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kweb.*
import kweb.components.Component
import kweb.html.HeadElement
import kweb.plugins.staticFiles.ResourceFolder
import kweb.plugins.staticFiles.StaticFilesPlugin
import kweb.state.render
import mu.two.KotlinLogging
import org.freenet.website.pages.about.aboutPage
import org.freenet.website.pages.claimId.claimIdPage
import org.freenet.website.pages.developers.PivotalTracker
import org.freenet.website.pages.developers.developersPage
import org.freenet.website.pages.homePage
import org.freenet.website.pages.joinUs.joinUsPage
import org.freenet.website.pages.faq.faqPage
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
            StaticFilesPlugin(ResourceFolder("static"), "/static")
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
                        activeNavItem.page(this)
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
            NavigationNode.Home
        } else {
            val path = "/" + pathSegments[0]
            findNodeByPath(navigationMenu, path) ?: NavigationNode.Home
        }
    }

fun findNodeByPath(nodes: List<NavigationNode>, path: String): NavigationNode? {
    for (node in nodes) {
        when (node) {
            is NavigationNode.Dropdown -> {
                findNodeByPath(node.children, path)?.let { return it }
            }
            else -> {
                if (node.path == path) {
                    return node
                }
            }
        }
    }
    return null
}


typealias HeadComponent = ElementCreator<HeadElement>

private fun HeadComponent.configureHead() {
    title().text("Freenet")

    element("script") {
        it["src"] = "/static/freenet.js"
    }

    addStylesheet("/static/fontawesome/css/fontawesome.min.css")
    addStylesheet("/static/fontawesome/css/solid.min.css")
    addStylesheet("/static/fontawesome/css/brands.min.css")
    addStylesheet("/static/bulma.min.css")
    addStylesheet("/static/freenetorg.css")

    element("meta") { meta ->
        meta["content"] = "width=device-width, initial-scale=1"
        meta["name"] = "viewport"
    }

    //  element("script")["src"] = "https://js.stripe.com/v3/"
    //  element("script")["src"] = "/static/checkout.js"
}

private fun HeadComponent.addStylesheet(href: String) {
    element("link") {
        it["rel"] = "stylesheet"
        it["href"] = href
    }
}
