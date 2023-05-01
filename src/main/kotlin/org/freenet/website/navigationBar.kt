package org.freenet.website

import kweb.*
import kweb.components.Component
import kweb.state.KVal

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
                            span().innerHTML(ni.html)
                        } else {
                            it.innerHTML(ni.html)
                        }
                        it.href = ni.link
                    }
                }
            }
        }
    }
}

enum class NavItem(val html : String, val link : String, val icon : String? = null) {
    Home("<b>Freenet</b>", "/", "dove"),
    Documentation("Learn", "https://docs.freenet.org/", "book"),
    Development("Dev", "/dev", "code"),
    Identity("Reserve Identity", "/identity", "key"),
}