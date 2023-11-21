package org.freenet.website

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kweb.*
import kweb.components.Component
import kweb.config.KwebDefaultConfiguration
import kweb.html.HeadElement
import kweb.plugins.staticFiles.ResourceFolder
import kweb.plugins.staticFiles.StaticFilesPlugin
import kweb.state.KVal
import kweb.state.render
import mu.two.KotlinLogging
import org.freenet.website.util.Github
import org.freenet.website.pages.blog.blogPage
import org.freenet.website.pages.developers.PivotalTracker
import org.freenet.website.pages.developers.developersPage
import org.freenet.website.pages.homePage
import org.freenet.website.pages.faq.faqPage
import org.freenet.website.pages.blog.BlogRssPlugin
import org.freenet.website.pages.claimId.claimIdPage
import org.freenet.website.util.HealthCheckPlugin
import org.freenet.website.util.UrlToPathSegmentsRF
import org.freenet.website.util.recordVisit
import java.time.Duration

private val logger = KotlinLogging.logger { }

val isLocalTestingMode: Boolean = System.getenv("FREENET_SITE_LOCAL_TESTING").equals("true", true)

suspend fun main() {

    // Initialize singletons to avoid a delay the first time they are used
    PivotalTracker.releases
    Github.getDiscussions()

    val scope = CoroutineScope(Dispatchers.IO)

    logger.info("Starting Freenet Site, isLocalTestingMode: $isLocalTestingMode")

    // TODO: Remove
    val cfg = object : KwebDefaultConfiguration() {
        override val clientStateTimeout: Duration
            get() = Duration.ofHours(1)
    }

    Kweb(
        port = 8080,
        debug = isLocalTestingMode,
        plugins = listOf(
            BlogRssPlugin(),
            HealthCheckPlugin,
            StaticFilesPlugin(ResourceFolder("static"), "/static",)
        ),
        kwebConfig = cfg,
    ) {
        val nav = pathToNavItem()

        doc.head {

            configureHead(nav.map { it.title ?: "Freenet" })
        }
        doc.body {

            section {
                it.classes("section", "content")

                div { div ->
                    div.classes("container")

                    navComponent(nav)

                    render(nav) { activeNavItem ->
                        when (activeNavItem) {
                            is NavItem.Home -> homePage()
                            is NavItem.Development -> developersPage()
                            is NavItem.Faq -> faqPage()
                            is NavItem.Claim -> claimIdPage()
                            is NavItem.Blog -> blogPage(activeNavItem.number)
                            else -> error("Unknown Item: $activeNavItem")
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
                "dev" -> NavItem.Development
                "faq" -> NavItem.Faq
                "claim" -> NavItem.Claim
                "blog" -> NavItem.Blog(if (pathSegments.size > 1) pathSegments[1].toIntOrNull() else null)
                else -> NavItem.Home
            }
        }
    }

typealias HeadComponent = ElementCreator<HeadElement>

private fun HeadComponent.configureHead(title : KVal<String>) {
    title().text(title)

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

    element("script")["src"] = "/static/id.js"
    element("script")["src"] = "/static/forge.all.min.js"
    element("script")["src"] = "https://js.stripe.com/v3/"
    element("script")["src"] = "/static/checkout.js"


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

    element("link") {
        it["rel"] = "alternate"
        it["type"] = "application/rss+xml"
        it["title"] = "Freenet Blog RSS Feed"
        it["href"] = "https://freenet.org/blog.rss"
    }

    //  element("script")["src"] = "https://js.stripe.com/v3/"
  //  element("script")["src"] = "/static/checkout.js"
}

fun Component.rabbitLogoComponent() {
    element("link").new { link ->
        link["rel"] = "icon"
        link["href"] = "/static/rabbit-logo.svg"
        link["type"] = "image/svg+xml"
    }
}
