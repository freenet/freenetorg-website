package org.freenet.website

import kweb.*
import kweb.components.Component
import kweb.state.KVal
import kweb.state.KVar

fun Component.navComponent(activeItem : KVal<NavItem>) {
    div { div ->
        div.classes("tabs")
        ul {
            for (ni in NavItem.values()) {
                li { li ->
                    li.classes(activeItem.map { if (it == ni) "is-active" else "" })
                    a(preventDefault = true) {
                        if (ni.icon != null) {
                            span { span ->
                                span.classes("icon", "is-small")
                                i { i ->
                                    i.classes("fas", "fa-${ni.icon}")
                                }
                            }
                            span().text(ni.asText)
                        } else {
                            it.text(ni.asText)
                        }
                        it.href = ni.link
                    }
                }
            }
        }
    }
}

enum class NavItem(val asText : String, val link : String, val icon : String? = null) {
    Home("Freenet", "/", "dove"),
    Documentation("Learn", "https://docs.freenet.org/", "book"),
    Development("Dev", "https://github.com/freenet/locutus", "code"),
    Roadmap("Roadmap", "/roadmap", "map"),
    Identity("Reserve Identity", "/identity", "key"),
}