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
    h2().classes("title", "is-small").text("Development")

    p().text("Learn about Freenet's architecture and how to develop decentralized apps for Freenet.")

    devLinks()

    recentlyCompleted()

    roadmap()
}

private fun Component.devLinks() {
    div { div ->
        div.classes("buttons")
        iconButton("User Manual", "https://docs.freenet.org/", arrayOf("fas", "fa-book"))
        iconButton("Github", "https://github.com/freenet/freenet-core", arrayOf("fab", "fa-github"))
        iconButton("Crates.io", "https://crates.io/crates/locutus", arrayOf("fa-brands", "fa-rust"))
    }
}
