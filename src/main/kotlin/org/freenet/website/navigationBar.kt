package org.freenet.website

import kweb.*
import kweb.components.Component
import kweb.state.KVal
import org.freenet.website.pages.blog.GitHubDiscussions

fun Component.navComponent(activeItem: KVal<NavItem>) {
    nav { nav ->
        nav.classes("navbar", "has-shadow", "is-spaced")
        nav["role"] = "navigation"
        nav["aria-label"] = "main navigation"

        div { div ->
            div.classes("navbar-brand")
            for (ni in listOf(NavItem.Home, NavItem.Developers, NavItem.Faq, NavItem.Claim, NavItem.Blog(null))) {
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

sealed class NavItem(val html: String, val link: String, val icon: String? = null, open val title : String? = null) {
    data object Home : NavItem("<b>Freenet</b>", "/", "home", "Freenet")
    data object Developers : NavItem("Dev", "/dev", "code", "Freenet: Developers")
    data object Faq : NavItem("FAQ", "/faq", "book", "Freenet: FAQ")
    data object Claim : NavItem("Claim", "/claim", "code", "Claim")
    class Blog(val number: Int?) : NavItem("Blog", "/blog", "blog") {
        override val title = if (number == null) {
            "Freenet Blog"
        } else {
            "Freenet Blog: ${GitHubDiscussions?.discussions?.discussionsByNumber?.get(number)?.title ?: "Not Found"}"
        }
    }
}
