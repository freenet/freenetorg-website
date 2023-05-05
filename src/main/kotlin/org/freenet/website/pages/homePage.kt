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
The increasing centralization of the internet poses a threat to free speech and  
democracy. In the modern era, a few corporations dominate the digital landscape, 
effectively privatizing the public square.
</p>
<p>
In 1999, we blazed a trail with the <a href="/static/freenet-original.pdf">original 
Freenet</a>—the world's first scalable, decentralized, peer-to-peer network. 
It introduced revolutionary ideas such as cryptographic contracts and small-world 
networks, functioning like a shared hard disk.
</p>
<p>
Building on this legacy, we present Freenet $currentYear—a comprehensive redesign 
and a decentralized platform to revolutionize the World Wide Web. Freenet 
$currentYear serves as a global shared computer, a platform for advanced 
completely decentralized software systems.
</p>
<p>
Freenet $currentYear allows developers to create decentralized alternatives to 
centralized services, including messaging, social media, email, and e-commerce. 
Our user-friendly decentralized applications (dApps) are scalable, interoperable, 
and secured with cryptography—liberating us all from centralized control.
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