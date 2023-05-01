package org.freenet.website.pages

import kweb.*
import kweb.components.Component
import kweb.state.ObservableList
import org.freenet.website.pages.news.NewsItem
import org.freenet.website.pages.news.latestNews
import java.util.*

fun Component.homePage(latestNewsItems: ObservableList<NewsItem>) {
    h2().classes("title", "is-small").text("Declare your digital independence")

    p().innerHTML("""
        <p>
        The internet has grown increasingly centralized over the past few decades, with a 
        handful of corporations now controlling most of its infrastructure. This centralization 
        threatens freedom of speech and undermines democratic principles. The public square
        is now privately owned.
        </p>
        <p>
        In 1999, we pioneered the concept of a decentralized internet with the creation of the 
        <a href="/static/freenet-2001.pdf">original Freenet</a>—a distributed, peer-to-peer 
        network that introduced groundbreaking technologies like cryptographic contracts and 
        small-world networks.
        </p>
        <p>
        We are proud to introduce a new Freenet — completely reimagined and redesigned. 
        Freenet 2023 aims to serve as a drop-in decentralized replacement for the World Wide 
        Web.
        </p>
        <p>
        With the new Freenet, developers can easily create and deploy decentralized alternatives 
        to current centralized internet services, including instant messaging, social 
        networking, email, and online stores. These decentralized applications will be 
        user-friendly, scalable, and secured through cryptography. They won't depend on
        the cloud or other centralized infrastructure.
        </p>
        <p>
        Freenet will put control back in he hands of the people.
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