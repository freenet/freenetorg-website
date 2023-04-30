package org.freenet.website

import kweb.*
import kweb.components.Component
import kweb.state.KVar

fun Component.navComponent(activeItem : KVar<NavItem>) {
    div { div ->
        div.classes("tabs", "is-centered")
        ul {
            for (ni in NavItem.values()) {
                li { li ->
                    li.classes(activeItem.map { if (it == ni) "is-active" else "" })
                    a(preventDefault = true) {
                        it.text(ni.asText)
                        it.href = ni.link
                    }
                }
            }
        }
    }
}

enum class NavItem(val asText : String, val link : String) {
    Home("Home", "/"),
    Documentation("Documentation", "https://docs.freenet.org/"),
    Development("Github", "https://github.com/freenet/locutus"),
    Donate("Claim Identity", "/identity"),
}