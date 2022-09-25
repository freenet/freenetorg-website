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
                """The internet has become increasingly centralized over the past few years, with a handful of 
                    companies now controlling the internet infrastructure. This privatization of the public 
                    square threatens freedom of speech and democracy..""".trimMargin()
            )

            p(fomantic.ui.text).text(
                """We're committed to decentralization and freedom of speech on the internet. 23 years ago, we 
                  created the original Freenet- the first distributed, decentralized peer-to-peer network. It 
                  pioneered technologies like cryptographic contracts and small-world networks and is still 
                  under active development today.
                """.trimMargin()
            )
            p(fomantic.ui.text).text(
                """Now we're building Locutus. Locutus makes it easy for developers to create and deploy 
                    decentralized alternatives to today's centralized tech companies. These decentralized apps will 
                    be easy to use, scalable, and secured through cryptography.
                """.trimMargin())

            p(fomantic.ui.text).innerHTML("""
                To learn about the original Freenet client <B>Fred</B>, visit its website at
                <a href="https://freenetproject.org">freenetproject.org</a>.
                """.trimIndent())

            p(fomantic.ui.text).innerHTML("""
                For a video introduction to Locutus
                watch <strong>Ian's talk and Q&amp;A</strong> - 
                <a href="https://youtu.be/d31jmv5Tx5k" rel="nofollow">YouTube</a> / 
                <a href="https://vimeo.com/manage/videos/740461100" rel="nofollow">Vimeo</a>.
                To learn about the new <B>Locutus</B> software as a developer read 
                <a href="https://docs.freenet.org/" rel="nofollow">The Locutus Book</a>, or visit
                our <a href="https://github.com/freenet/locutus">GitHub repository</a>.
            """.trimIndent())

            h2(fomantic.ui.text).text("Latest News")
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

val newsItemList = if (db != null) retrieveNews(db) else {
    KVar(listOf(
        NewsItem(Date(),"This is the first news item", true),
        NewsItem(Date(),"This is the second news item", false),
        NewsItem(Date(),"This is the third news item", true),
    ))
}