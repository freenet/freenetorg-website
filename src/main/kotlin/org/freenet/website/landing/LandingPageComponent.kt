package org.freenet.website.landing

import kweb.*
import kweb.components.Component
import kweb.plugins.fomanticUI.fomantic
import kweb.state.ObservableList
import org.freenet.website.landing.news.NewsItem
import org.freenet.website.landing.news.latestNewsComponent
import org.freenet.website.landing.roadmap.roadmapComponent
import java.util.*

fun Component.landingPageComponent(latestNewsItems : ObservableList<NewsItem>)  {
            div(fomantic.ui.text.center.aligned.container) {
                div(fomantic.ui.text.left.aligned.container) {
                    div {
                        h1(fomantic.ui.text).text("Freenet")
                        h2(fomantic.ui.text).addClasses("subtitle").text("Declare your digital independence")
                    }

                    br()

                    p(fomantic.ui.text).text(
                        """The internet has grown increasingly centralized over the past few decades, with a handful of 
                    corporations now controlling most of its infrastructure. This privatization of the public square 
                    threatens freedom of speech and democracy.""".trimMargin()
                    )

                    p(fomantic.ui.text).innerHTML(
                        """23 years ago, we created <a href="https://freenetproject.org/">Freenet Classic</a> - the 
                            first distributed, decentralized peer-to-peer network.  Freenet Classic pioneered 
                            technologies like cryptographic contracts and small-world networks and still has an active 
                            community of users and developers today.
                """.trimMargin()
                    )

                    p(fomantic.ui.text).text(
                        """The internet has evolved significantly since its early days and so have user 
                            expectations. To meet these current challenges, we are developing a new version of 
                            Freenet for 2023, initially known as "Locutus." In January 2023, the 
                            Freenet Board of Directors announced that the original Freenet will be renamed to 
                            "Freenet Classic" and Locutus will be renamed to Freenet.""".trimIndent()
                    )

                    p(fomantic.ui.text).text(
                        """The new version of Freenet makes it easy for developers to create and deploy 
                            decentralized alternatives to current centralized internet services such as instant 
                            messaging, social networking, email, and online stores. These decentralized applications 
                            will be user-friendly, scalable and secured through the use of cryptography. Freenet will 
                            empower users by giving back control.
                """.trimMargin()
                    )

                    h3(fomantic.ui.text).text("Learn More")

                    ul(fomantic.ui.list) {
                        li().innerHTML(
                            """
                For a video introduction to our new Freenet watch Ian's talk in July 2022 on
                <a href="https://youtu.be/x9g018OYwb4">YouTube</a>.
            """.trimIndent()
                        )
                        li().innerHTML(
                            """The <a href="https://docs.freenet.org/" rel="nofollow">Dev Guide</a> is a technical
                        introduction to Freehet including a <a href="https://docs.freenet.org/dev-guide.html">
                        development guide</a> on how to build and test a decentralized app"""
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

                    latestNewsComponent(latestNewsItems)

                    roadmapComponent()

                    h3(fomantic.ui.text).text("Support Our Work")

                    val donationWallets = listOf(
                        Pair("Bitcoin", "3M3fbA7RDYdvYeaoR69cDCtVJqEodo9vth"),
                        Pair("Zcash", "t1VHw1PHgzvMqEEd31ZBt3Vyy2UrG4J8utB"),
                        Pair("Ethereum", "0x79158A5Dbd9C0737CB27411817BD2759f5b9a9Ae"),
                    )

                    div(fomantic.ui.list) {
                        for (wallet in donationWallets) {
                            div(fomantic.ui.item) {
                                div(fomantic.ui.mini.labeled.input) {
                                    div(fomantic.ui.label).text(wallet.first)
                                    input(type = InputType.text)
                                        .set("readonly", "true")
                                        .set("size", wallet.second.length.toString())
                                        .set("value", wallet.second)
                                }
                            }
                        }
                        div(fomantic.ui.item).innerHTML(
                            "Find Paypal and other donation options " +
                                    "<a href=\"https://freenetproject.org/pages/donate.html\">here</a>."
                        )
                    }

                }


            }.set("background-color", "e8e8e8")
        }

val dummyNewsItems = ObservableList(
    listOf(
        NewsItem(Date(), "This is the first news item", true),
        NewsItem(Date(), "This is the second news item", false),
        NewsItem(Date(), "This is the third news item", true),
    )
)