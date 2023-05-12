package org.freenet.website

import kweb.*
import kweb.components.Component
import kweb.state.KVal
import org.freenet.website.pages.about.aboutPage
import org.freenet.website.pages.claimId.claimIdPage
import org.freenet.website.pages.community.communityPage
import org.freenet.website.pages.developers.architecture.architecturePage
import org.freenet.website.pages.developers.roadmap.roadmapPage
import org.freenet.website.pages.faq.faqPage
import org.freenet.website.pages.homePage

fun Component.navComponent(activeItem: KVal<NavigationNode>) {
    nav { nav ->
        nav.classes("navbar", "has-shadow", "is-spaced")
        nav["role"] = "navigation"
        nav["aria-label"] = "main navigation"

        div { div ->
            div.classes("navbar-brand")
            for (node in navigationMenu) {
                when (node) {
                    is NavigationNode.Dropdown -> {
                        div { div ->
                            div.classes("navbar-item", "has-dropdown", "is-hoverable")

                            a { a ->
                                a.classes("navbar-link")
                                a.innerHTML(node.displayName)
                                a.href = node.path
                            }

                            div { div ->
                                div.classes("navbar-dropdown")
                                for (childNode in node.children) {
                                    a { a ->
                                        a.classes(activeItem.map { if (it == childNode) "navbar-item is-active" else "navbar-item" })
                                        a.innerHTML(childNode.displayName)
                                        a.href = childNode.path
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        a { a ->
                            a.classes(activeItem.map { if (it == node) "navbar-item is-active" else "navbar-item" })
                            a.innerHTML(node.displayName)
                            a.href = node.path
                        }
                    }
                }
            }
        }
    }
}

val navigationMenu = listOf(
    NavigationNode.Home,
    NavigationNode.About,
    NavigationNode.Dropdown(
        "Developers",
        "/dev",
        { }, // Do nothing when "Developers" is clicked
        listOf(
            NavigationNode.Architecture,
            NavigationNode.Roadmap
        )
    ),
    NavigationNode.Community,
    NavigationNode.Faq,
    NavigationNode.ClaimId
)



sealed class NavigationNode(
    val displayName: String,
    val path: String,
    val page: Component.() -> Unit
) {
    object Home : NavigationNode("Home", "/", Component::homePage)
    object About : NavigationNode("About", "/about", Component::aboutPage)
    object Community : NavigationNode("Community", "/community", Component::communityPage)
    object Faq : NavigationNode("FAQ", "/faq", Component::faqPage)
    object ClaimId : NavigationNode("Claim Identity", "/claim-id", Component::claimIdPage)
    object Architecture : NavigationNode("Architecture", "/dev/architecture", Component::architecturePage)
    object Roadmap : NavigationNode("Roadmap", "/dev/roadmap", Component::roadmapPage)
    class Dropdown(
        displayName: String,
        path: String,
        page: Component.() -> Unit,
        val children: List<NavigationNode>
    ) : NavigationNode(displayName, path, page)
}


