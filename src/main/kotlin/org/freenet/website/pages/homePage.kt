package org.freenet.website.pages

import kweb.*
import kweb.components.Component
import kweb.state.ObservableList
import org.freenet.website.pages.news.NewsItem
import org.freenet.website.pages.news.latestNews
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
accountability, enabling them to censor content, suppress dissent, and exclude 
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
for simplicity and accessibility, Freenet $currentYear can be used seamlessly through 
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

    h3().text("Support Our Work")

    val donationWallets = listOf(
        Pair("Bitcoin", "3M3fbA7RDYdvYeaoR69cDCtVJqEodo9vth"),
        Pair("Zcash", "t1VHw1PHgzvMqEEd31ZBt3Vyy2UrG4J8utB"),
        Pair("Ethereum", "0x79158A5Dbd9C0737CB27411817BD2759f5b9a9Ae"),
    )

    table { table ->
        table.classes("table", "is-hoverable")
        tbody {
            for (wallet in donationWallets) {
                tr {
                    td().text(wallet.first)
                    td { td ->
                        td.classes("has-text-centered")
                        div { div ->
                            div.classes("control", "has-icons-right")
                            input(type = InputType.text)
                                .classes("input", "is-fullwidth")
                                .set("readonly", "true")
                                .set("value", wallet.second)
                                .set("onclick", "this.select()")
                            span { span ->
                                span.classes("icon", "is-normal", "is-right")
                                i().classes("fas", "fa-copy")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun Component.learnMoreLinks() {
    div { div ->
        div.classes("buttons")
        // Video Introduction
        iconButton(
            html = "Watch Ian's Talk",
            href = "https://www.youtube.com/watch?v=d31jmv5Tx5k",
            icon = arrayOf("fas", "fa-video"),
            buttonClasses = arrayOf("button", "is-medium-blue")
        )
        // User Manual
        iconButton(
            html = "Read User Manual",
            href = "https://docs.freenet.org/",
            icon = arrayOf("fas", "fa-book"),
            buttonClasses = arrayOf("button", "is-medium-teal")
        )
        // Community Chat
        iconButton(
            html = "Chat on Matrix",
            href = "https://matrix.to/#/freenet:matrix.org",
            icon = arrayOf("fas", "fa-comments"),
            buttonClasses = arrayOf("button", "is-medium-purple")
        )
        // GitHub Repository
        iconButton(
            html = "Visit GitHub",
            href = "https://github.com/freenet/locutus",
            icon = arrayOf("fab", "fa-github"),
            buttonClasses = arrayOf("button", "is-medium-orange")
        )
    }
}


val dummyNewsItems = ObservableList(
    listOf(
        NewsItem(Date(), "This is the first news item", true),
        NewsItem(Date(), "This is the second news item", false),
        NewsItem(Date(), "This is the third news item", true),
    )
)