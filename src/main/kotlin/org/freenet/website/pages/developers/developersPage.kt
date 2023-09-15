package org.freenet.website.pages.developers

import kweb.components.Component
import kweb.div
import kweb.h2
import kweb.p
import org.freenet.website.util.iconButton

/**
 *  This tab could be dedicated to developers who are interested in Freenet's
 *  architecture, building decentralized apps (dApps), or contributing to
 *  Freenet's development. It could include technical documentation, guides
 *  for building dApps, and information on how to contribute to the project
 *  (e.g., GitHub repository, bug reporting, etc.). Additionally, it could
 *  provide information about the upcoming feature of creating
 *  public/private keypairs and the future decentralized reputation system.
 */
fun Component.developersPage() {
    h2().classes("title", "is-small").text("Developers")

    p().text("Learn about Freenet's architecture and how to develop decentralized apps for Freenet.")

    devLinks()

    roadmap()
}

private fun Component.devLinks() {
    div { div ->
        div.classes("buttons")
        iconButton("Core Concepts", "https://docs.freenet.org/components/overview.html", arrayOf("fas", "fa-sitemap"))
        iconButton("Tutorial", "https://docs.freenet.org/tutorial.html", arrayOf("fab", "fa-readme"))
        iconButton("Github", "https://github.com/freenet/freenet-core", arrayOf("fab", "fa-github"))
        iconButton("Crates.io", "https://crates.io/crates/locutus", arrayOf("fa-brands", "fa-rust"))
    }
}

