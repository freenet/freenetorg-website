package org.freenet.website.pages.about

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kweb.*
import kweb.components.Component
import kweb.state.KVar
import kweb.state.render
import org.freenet.website.util.getUserManualPage
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

private val componentPageHtml = getUserManualPage("https://docs.freenet.org/components.html") { document ->
    document.select("main h1").first()!!.tagName("h2").id("components_html")
}

/**
This tab could provide detailed information about Freenet's history,
goals, and vision. It could include an explanation of the decentralized
architecture, the concept of cryptographic contracts, and the
small-world network. It could also address the concerns of users
who are interested in freedom of speech online.
 */
fun Component.aboutPage() {
    h2 { h2 ->
        h2.classes("title", "is-small").text("Contents")
    }
    ul {
        li { a { a ->
            a.href = "#components_html"
            a.text("Anatomy of a Freenet application")
        } }
    }

    render(componentPageHtml) { html ->
        if (html == null) {
            h1().text("Loading...")
        } else {
            h1().text("About Freenet")
            element("main").innerHTML(html)
        }
    }
}