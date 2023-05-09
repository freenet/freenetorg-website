package org.freenet.website

import kweb.*
import kweb.components.Component
import kweb.state.KVal

fun Component.navComponent(activeItem: KVal<NavItem>) {
    nav { nav ->
        nav.classes("navbar", "has-shadow", "is-spaced")
        nav["role"] = "navigation"
        nav["aria-label"] = "main navigation"

        div { div ->
            div.classes("navbar-brand")
            for (ni in NavItem.values()) {
                a { a ->
                    a.classes(activeItem.map { if (it == ni) "navbar-item is-active" else "navbar-item" })
                    if (ni.icon != null) {
                        span { span ->
                            span.classes("icon",) // "is-small")
                            i { i ->
                                i.classes("fas", "fa-${ni.icon}")
                            }
                        }
                        // Add a space between the icon and the text
                        span().innerHTML("&nbsp;")
                        span().innerHTML(ni.html)
                    } else {
                        a.innerHTML(ni.html)
                    }
                    a.href = ni.link
                }
            }
        }
    }
}

enum class NavItem(val html: String, val link: String, val icon: String? = null) {
    Home("<b>Freenet</b>", "/", "home"),
    About("About", "/about", "info-circle"),
    Developers("Developers", "/dev", "code"),
    JoinUs("Join Us", "/join", "users"),
    Faq("FAQ", "/faq", "book"),
}
