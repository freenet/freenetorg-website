package org.freenet.website.pages.about

import kweb.*
import kweb.components.Component
import kweb.state.KVar
import kweb.state.render
import org.freenet.website.util.getUserManualPage

private val introPageHtml = getUserManualPage("https://docs.freenet.org/introduction.html") { document ->
    document.select("main h1").first()!!.tagName("h2").id("introduction_html")
}

private val p2pNetworkPageHtml = getUserManualPage("https://docs.freenet.org/p2p-network.html") { document ->
    document.select("main h1").first()!!.tagName("h2").id("p2p_network")
}

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
    h1().text("About Freenet")

    div { div ->
        div.classes("container")
        h3 { h3 ->
            h3.classes("title")
            h3.text("Contents")
        }
        div { div ->
            div.classes("content")
            ul {
                li {
                    a { a ->
                        a["href"] = "#introduction_html"
                        a.text("Introduction")
                    }
                }
                li {
                    a { a ->
                        a["href"] = "#components_html"
                        a.text("Building Decentralized Applications on Freenet")
                    }
                }
                li {
                    a { a ->
                        a["href"] = "#p2p_network"
                        a.text(" Freenet Network Topology")
                    }
                }
            }
        }

        fun renderPage(html : KVar<String?>) {
            render(html) { html ->
                section { section ->
                    section.classes("section")
                    if (html == null) {
                        h1().text("Loading...")
                    } else {
                        element("main").innerHTML(html)
                    }
                }
            }
        }

        renderPage(introPageHtml)
        renderPage(componentPageHtml)
        renderPage(p2pNetworkPageHtml)
    }
}