package org.freenet.website.dev

import kweb.*
import kweb.button
import kweb.components.Component

fun Component.devPage() {
    h3().classes("title").text("Developers")

    p().text("Learn about Freenet's architecture and how to develop decentralized apps for Freenet.")

    devLinks()

    roadmap()
}

private fun Component.devLinks() {
    div { div ->
        div.classes("buttons")

        iconButton("Tutorial", "https://docs.freenet.org/tutorial.html", arrayOf("fab", "fa-readme"))
        iconButton("Github", "https://github.com/freenet/locutus", arrayOf("fab", "fa-github"))
        iconButton("Crates.io", "https://crates.io/crates/locutus", arrayOf("fa-brands", "fa-rust"))

    }
}

private fun Component.iconButton(html : String, href : String, icon : Array<String>) {
    a { a ->
        a.classes("button")
        a.href = href

        span { span ->
            span.classes("icon")

            i { i ->
                i.classes(*icon)
            }
        }

        span { span ->
            span.innerHTML(html)
        }

    }
}