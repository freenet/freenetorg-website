package org.freenet.website

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kweb.*
import kweb.components.Component
import kweb.html.HeadElement
import kweb.plugins.staticFiles.ResourceFolder
import kweb.plugins.staticFiles.StaticFilesPlugin
import mu.two.KotlinLogging
import org.freenet.website.pages.developers.PivotalTracker
import org.freenet.website.pages.renderNavBarAndPage
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

                    renderNavBarAndPage(url.map(UrlToPathSegmentsRF))

                }
            }
        }

        scope.launch {
            recordVisit(this@Kweb.httpRequestInfo)
        }
    }

}

typealias HeadComponent = ElementCreator<HeadElement>

private fun HeadComponent.configureHead() {
    title().text("Freenet")

    // Order matters, dependencies come first
    addScript("/static/jquery.min.js")
    addScript("/static/qrcode.min.js")

    // TODO: This was compiled with --with-all, but it should only include
    // TODO: features we're using
    addScript("/static/sjcl.min.js")
    addScript("/static/id.js")

    // addScript("/static/crypto-js.min.js")
    //  addScript("/static/jsbn.js")
    // addScript("/static/jsrsasign-all-min.js")

    listOf(
        "/static/fontawesome/css/fontawesome.min.css",
        "/static/fontawesome/css/solid.min.css",
        "/static/fontawesome/css/brands.min.css",
        "/static/bulma.min.css",
        "/static/freenetorg.css"
    ).forEach { addStylesheet(it) }

    element("meta") { meta ->
        meta["content"] = "width=device-width, initial-scale=1"
        meta["name"] = "viewport"
    }
}

private fun HeadComponent.addScript(src: String) {
    element("script") {
        it["src"] = src
    }
}

private fun HeadComponent.addStylesheet(href: String) {
    element("link") {
        it["rel"] = "stylesheet"
        it["href"] = href
    }
}
