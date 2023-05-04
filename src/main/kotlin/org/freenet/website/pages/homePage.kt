package org.freenet.website.pages

import kweb.*
import kweb.components.Component
import kweb.state.ObservableList
import org.freenet.website.pages.news.NewsItem
import org.freenet.website.pages.news.latestNews
import java.util.*

/**
 * The main landing page, which provides an overview of Freenet, its mission,
 * and its features.
 */
fun Component.homePage(latestNewsItems: ObservableList<NewsItem>) {
    h2().classes("title", "is-small").text("Declare your digital independence")

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    p().innerHTML("""
        <p>
        The internet's growing centralization endangers free speech and democracy, 
        as a handful of corporations now dominate its infrastructure. In $currentYear, 
        the public square is privately owned.
        </p>
        <p>
        In 1999, we pioneered the <a href="/static/freenet-original.pdf">original 
        Freenet</a>—the first scalable, decentralized, peer-to-peer network that 
        introduced groundbreaking concepts like cryptographic contracts and 
        small-world networks.
        </p>
        <p>
        Continuing this legacy, we're thrilled to introduce Freenet $currentYear—a 
        decentralized platform to replace the World Wide Web. While building on the 
        original, Freenet $currentYear emphasizes user-friendliness, real-time 
        communication, and interoperability.
        </p>
        <p>
        Freenet $currentYear empowers developers to build decentralized alternatives 
        to centralized services, including messaging, social media, email, and 
        e-commerce. Our decentralized applications (dApps) are user-friendly, 
        scalable, interoperable, and secured with cryptography—liberating you 
        from centralized control.
        </p>

    """.trimIndent())

    h3().classes("title", "is-medium").text("Learn More")

    ul {
        li().innerHTML(
            """
                For a video introduction to our new Freenet watch Ian's talk in July 2022 on
                <a href="https://youtu.be/x9g018OYwb4">YouTube</a>.
            """.trimIndent()
        )
        li().innerHTML(
            """The Freenet <a href="https://docs.freenet.org/">User Manual</a>"""
        )
        li().innerHTML(
            """
                    Have a question or idea? Chat with us on 
                    <a href="https://matrix.to/#/#freenet-locutus:matrix.org">Matrix</a>"""
        )
        li().innerHTML(
            """
                        Visit the new Freenet <a href="https://github.com/freenet/locutus">GitHub repository</a> to 
                        browse our source code, and report bugs """
        )
        li().innerHTML(
            """
                        While new Freenet is still in development, the original Freenet Classic software has a vibrant 
                        community of users and developers, learn more at <a href="https://freenetproject.org">freenetproject.org</a>.
                """
        )
    }

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
                                span.classes("icon", "is-small", "is-right")
                                i().classes("fas", "fa-copy")
                            }
                        }
                    }
                }
            }
        }
    }
}

val dummyNewsItems = ObservableList(
    listOf(
        NewsItem(Date(), "This is the first news item", true),
        NewsItem(Date(), "This is the second news item", false),
        NewsItem(Date(), "This is the third news item", true),
    )
)