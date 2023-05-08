package org.freenet.website.pages

import kweb.*
import kweb.components.Component
import kweb.state.ObservableList
import org.freenet.website.pages.resources.NewsItem
import org.freenet.website.util.iconButton
import java.util.*

/**
 * The main landing page, which provides an overview of Freenet, its mission,
 * and its features.
 */
fun Component.homePage(latestNewsItems: ObservableList<NewsItem>) {
    h2().classes("title", "is-normal").text("Declare your digital independence")

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    p().innerHTML(
        """

<p>
The centralization of the internet poses a fundamental threat to individual
freedom. In $currentYear, a few corporations control most internet services
and infrastructure. These corporations wield immense power over most of us with little 
accountability, enabling them to censor content, exploit our data, and exclude 
users from services they depend on —all with profound implications for 
democracy. We need a solution urgently.
</p>
<p>
In 1999, we created the <a href="/static/freenet-original.pdf">original 
Freenet</a>—the world's first scalable, decentralized, peer-to-peer network. 
It introduced revolutionary ideas such as cryptographic contracts and small-world 
networks, and was analogous to a shared hard disk.
</p>
<p>
Building on this legacy, we present Freenet $currentYear— a drop-in decentralized
replacement for the world wide web. This new Freenet is analogous to a global 
shared computer, a platform for sophisticated decentralized software systems. Designed
for simplicity and flexibility, Freenet $currentYear can be used seamlessly through 
your web browser, providing an experience that feels just like using the traditional web.
</p>
<p>
Freenet $currentYear allows developers to create decentralized alternatives to 
centralized services, including messaging, social media, email, and e-commerce. 
Our user-friendly decentralized applications (dApps) are scalable, interoperable, 
and secured with cryptography.
</p>

    """.trimIndent()
    )

    learnMoreLinks()

    latestNews(latestNewsItems)

}

private fun Component.learnMoreLinks() {
    div { div ->
        div.classes("columns", "is-multiline")
        // Video Introduction
        iconButtonWithDescription(
            html = "Watch Ian's Talk",
            href = "https://www.youtube.com/watch?v=d31jmv5Tx5k",
            icon = arrayOf("fas", "fa-video"),
            buttonClasses = arrayOf("button", "is-medium-blue"),
            description = "Watch Ian's talk to learn more about the project."
        )
        // User Manual
        iconButtonWithDescription(
            html = "Read User Manual",
            href = "https://docs.freenet.org/",
            icon = arrayOf("fas", "fa-book"),
            buttonClasses = arrayOf("button", "is-medium-teal"),
            description = "Read the user manual for detailed instructions on how to use the software."
        )
        // Community Chat
        iconButtonWithDescription(
            html = "Chat on Matrix",
            href = "https://matrix.to/#/#freenet-locutus:matrix.org",
            icon = arrayOf("fas", "fa-comments"),
            buttonClasses = arrayOf("button", "is-medium-purple"),
            description = "Join our Matrix chat room to connect with the community and ask questions."
        )
        // GitHub Repository
        iconButtonWithDescription(
            html = "Visit GitHub",
            href = "https://github.com/freenet/locutus",
            icon = arrayOf("fab", "fa-github"),
            buttonClasses = arrayOf("button", "is-medium-orange"),
            description = "Visit our GitHub repository to explore the source code and contribute."
        )
    }
}

fun Component.iconButtonWithDescription(
    html: String,
    href: String,
    icon: Array<String>,
    buttonClasses: Array<String> = arrayOf("button"),
    description: String
) {
    div { column ->
        column.classes("column", "is-half")
        div { level ->
            level.classes("level")
            div { levelLeft ->
                levelLeft.classes("level-left")
                a { a ->
                    a.classes(*buttonClasses)
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
            div { levelRight ->
                levelRight.classes("level-right")
                p { p ->
                    p.innerHTML(description)
                }
            }
        }
    }
}

