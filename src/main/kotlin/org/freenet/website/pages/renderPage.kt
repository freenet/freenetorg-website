package org.freenet.website.pages

import kweb.*
import kweb.components.Component
import kweb.state.KVal
import kweb.state.render
import org.freenet.website.pages.about.aboutPage
import org.freenet.website.pages.claimId.claimIdPage
import org.freenet.website.pages.developers.developersPage
import org.freenet.website.pages.home.homePage
import org.freenet.website.pages.joinUs.joinUsPage

data class Page(
    val urlPath: String,
    val tabHtml: String?,
    val link: String,
    val icon: String,
    val additionalClasses: String = "",
    val renderer: ((Component) -> Unit)? = null
)

val pages = mapOf(
    "" to Page(urlPath = "", tabHtml = "<b>Freenet</b>", link = "/", icon = "home", renderer = Component::homePage),
    "about" to Page(urlPath = "about", tabHtml = "About", link = "/about", icon = "info-circle", renderer = Component::aboutPage),
    "dev" to Page(urlPath = "dev", tabHtml = "Developers", link = "/dev", icon = "code", renderer = Component::developersPage),
    "join" to Page(urlPath = "join", tabHtml = "Join Us", link = "/join", icon = "user-plus", renderer = Component::joinUsPage),
    "claim" to Page(urlPath = "claim", tabHtml = null, link = "/claim", icon = "id-card", renderer = Component::claimIdPage),
)

fun Component.renderNavBarAndPage(urlPath: KVal<List<String>>) {
    // KVal to hold the current page
    val currentPage = urlPath.map { path ->
        pages[path.joinToString("/")]
    }

    nav { nav ->
        nav.classes("navbar", "has-shadow", "is-spaced")
        nav["role"] = "navigation"
        nav["aria-label"] = "main navigation"

        //  div { div ->
        //       div.classes("navbar-brand")
        //   }

        div { div ->
            div.classes("navbar-brand")
           // div().classes("navbar-start")
            // Create navbar-item for each page in the list
            pages.values.filter {it.tabHtml != null}.forEach { page ->
                tab(page, currentPage.map { it == page })
            }
        }
    }

    // Render the current page or not-found message
    render(currentPage) { page ->
        if (page != null) {
            page.renderer?.let { it(this) }
        } else {
            p().text("Page not found")
        }
    }
}

private fun Component.tab(page: Page, isActive: KVal<Boolean>) {
    a { a ->
        a.classes(isActive.map { if (it) "navbar-item is-active ${page.additionalClasses}" else "navbar-item ${page.additionalClasses}" })
        span { span ->
            span.classes("icon")
            i { i ->
                i.classes("fas", "fa-${page.icon}")
            }
        }
        // Add a space between the icon and the text
        span().innerHTML("&nbsp;")
        span().innerHTML(page.tabHtml ?: error("tabHtml shouldn't be null"))
        a.href = page.link
    }
}
