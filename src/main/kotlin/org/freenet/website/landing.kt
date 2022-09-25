package org.freenet.website

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import kweb.*
import kweb.plugins.fomanticUI.fomantic
import kweb.state.CloseReason
import kweb.state.KVal
import kweb.state.KVar
import kweb.state.render
import org.freenet.website.db.db
import org.freenet.website.util.toObject
import java.util.*

const val MAX_NEWS_ITEMS = 7

fun ElementCreator<*>.landingPage() {

    div(fomantic.ui.text.center.aligned.container) {
        div(fomantic.ui.text.left.aligned.container) {
            div() {
                h1(fomantic.ui.text).text("Freenet")
                h2(fomantic.ui.text).addClasses("subtitle").text("Declare your digital independence")
            }

            br()

            p(fomantic.ui.text).text(
                """|The Internet has grown increasingly centralized over the past 20 years, such that a handful 
                   |of companies now effectively control the Internet infrastructure. The public square is privately 
                   |owned, threatening freedom of speech and democracy.""".trimMargin()
            )

            p(fomantic.ui.text).text(
                """|23 years ago we created the original Freenet, the first distributed, decentralized 
                   |peer-to-peer network. It pioneered technologies like cryptographic contracts and small-world 
                   |networks, and is still under active development.""".trimMargin()
            )
            p(fomantic.ui.text).text(
                """|Today we’re building Locutus, which will make it easy for developers 
                   |to create and deploy decentralized alternatives to today’s centralized tech companies. These 
                   |decentralized apps will be easy to use, scalable, and secured through cryptography.""".trimMargin())

            h2(fomantic.ui.text).text("Locutus News")
            div(fomantic.ui.bulleted.list) {
                render(newsItemList) { items ->
                    for (newsItem in items) {
                        div(fomantic.item) {
                            val prettyDate = humanize.Humanize.formatDate(newsItem.date, "MMMM d, yyyy")

                            parent.innerHTML(
                                """
                            <B>${prettyDate}:</B> ${newsItem.description}
                        """.trimIndent()
                            )
                        }
                    }
                }
            }

            h2(fomantic.ui.text).text("Software")
            div(fomantic.ui.bulleted.list) {
                div(fomantic.ui.item).innerHTML("<a href=\"https://freenetproject.org/\">Fred</a> - The original Freenet software, first released in March 2000 and continuously developed since")
                div(fomantic.ui.item).innerHTML("<a href=\"https://github.com/freenet/locutus\">Locutus</a> - A decentralized application layer for the Internet, first release expected September 2022")
            }

            h2(fomantic.ui.text).text("Community")
            div(fomantic.ui.bulleted.list) {
                div(fomantic.ui.item).innerHTML("<a href=\"https://reddit.com/r/freenet\">r/freenet</a> on Reddit")
                div(fomantic.ui.item) {
                    span().text("Locutus")
                    div(fomantic.ui.bulleted.list) {
                        div(fomantic.ui.item).innerHTML("Follow <a href=\"https://twitter.com/freenetorg\">@freenetorg</a> on Twitter for updates")
                        div(fomantic.ui.item).innerHTML("Chat with us on <a href=\"https://matrix.to/#/#freenet-locutus:matrix.org\">Matrix</a>")
                    }
                }
                div(fomantic.ui.item) {
                    span().text("Fred")
                    div(fomantic.ui.bulleted.list) {
                        div(fomantic.ui.item).innerHTML("Follow <a href=\"https://freenetproject.org\">@freenetproject</a>")
                        div(fomantic.ui.item).innerHTML("IRC: <a href=\"https://web.libera.chat/?nick=FollowRabbit%7C?#freenet\">#freenet on libera</a>")
                    }
                }
            }

            h2(fomantic.ui.text).text("Support Us")

            val donationWallets = listOf(Pair("Bitcoin", "3M3fbA7RDYdvYeaoR69cDCtVJqEodo9vth"),
                                         Pair("Zcash", "t1VHw1PHgzvMqEEd31ZBt3Vyy2UrG4J8utB"),
                                         Pair("Ethereum", "0x79158A5Dbd9C0737CB27411817BD2759f5b9a9Ae"),
                )

            div(fomantic.ui.list) {
                for (wallet in donationWallets) {
                    div(fomantic.ui.item) {
                        div(fomantic.ui.mini.labeled.input) {
                            div(fomantic.ui.label).text(wallet.first)
                            input(type = InputType.text)
                                .setAttribute("readonly", "true")
                                .setAttribute("size", wallet.second.length.toString())
                                .setAttribute("value", wallet.second)
                        }
                    }
                }
                div(fomantic.ui.item).innerHTML("Find Paypal and other donation options " +
                        "<a href=\"https://freenetproject.org/pages/donate.html\">here</a>.")
            }

        }




    }.setAttribute("background-color", "e8e8e8")
}

private val newsItemList = if (db != null) retrieveNews(db) else {
    KVar(listOf(
        NewsItem(Date(),"This is the first news item", true),
        NewsItem(Date(),"This is the second news item", false),
        NewsItem(Date(),"This is the third news item", true),
    ))
}