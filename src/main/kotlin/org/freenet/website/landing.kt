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
                """The internet has become increasingly centralized over the past 30 years, with a handful of 
                    corporations now controlling most of the internet infrastructure. This privatization of the public 
                    square threatens freedom of speech and democracy.""".trimMargin()
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

            ul(fomantic.ui.list) {
                li().innerHTML("""
                For a video introduction to Locutus watch Ian's talk in July 2022 on
                <a href="https://youtu.be/d31jmv5Tx5k">YouTube</a> or 
                <a href="https://vimeo.com/manage/videos/740461100">Vimeo</a>
            """.trimIndent())
                li().innerHTML(
                    """The <a href="https://docs.freenet.org/" rel="nofollow">Locutus Book</a> is a technical
                        introduction to Locutus including a <a href="https://docs.freenet.org/dev-guide.html">
                        development guide</a> on how to build and test a decentralized app"""
                )
                li().innerHTML("""
                    Have a question or idea? Chat with us on 
                    <a href="https://matrix.to/#/#freenet-locutus:matrix.org">Matrix</a>""")
                li().innerHTML("""
                        Visit the Locutus <a href="https://github.com/freenet/locutus">GitHub repository</a> to 
                        browse our source code, and report bugs """
                )
                li().innerHTML("""
                        While Locutus is still in development, the original Freenet software was first released in
                        March 2000 and has a vibrant community of users and developers, learn more at 
                        <a href="https://freenetproject.org">freenetproject.org</a>
                """)
            }

            h3(fomantic.ui.text).text("Latest News")
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

            h3(fomantic.ui.text).text("Support Our Work")

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