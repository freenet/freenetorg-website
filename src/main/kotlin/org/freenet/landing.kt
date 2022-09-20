package org.freenet

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import kweb.*
import kweb.plugins.fomanticUI.fomantic
import java.util.*

fun ElementCreator<*>.landingPage(db: Firestore) {
    val news = getNews(db)

    div(fomantic.ui.text.center.aligned.container) {
        div(fomantic.ui.text.left.aligned.container) {
            div() {
                h1(fomantic.ui.text).addClasses("title").text("Freenet")
                h2(fomantic.ui.text).addClasses("subtitle").text("Declare your digital independence")
            }

            br()

            p(fomantic.ui.text).text("23 years ago we created the original Freenet, the first distributed, decentralized peer-to-peer network. It pioneered " +
                    "technologies like cryptographic contracts and small-world networks, and is still under active development.\n" +
                    "\n")
            p(fomantic.ui.text).text("Today we’re building Locutus, which will make it easy for developers to create and deploy decentralized alternatives to today’s centralized tech companies. " +
                    "These decentralized apps will be easy to use, scalable, and secured through cryptography.")

            h2(fomantic.ui.text).text("Locutus News")
            div(fomantic.ui.bulleted.list) {
                for (newsItem in news) {
                    div(fomantic.item) {
                        val prettyDate = humanize.Humanize.formatDate(newsItem.date,"MMMM d, yyyy")

                        parent.innerHTML("""
                            <B>${prettyDate}:</B> ${newsItem.description}
                        """.trimIndent())
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
                    p().text("Locutus")
                    div(fomantic.ui.bulleted.list) {
                        div(fomantic.ui.item).innerHTML("Follow <a href=\"https://twitter.com/freenetorg\">@freenetorg</a> on Twitter for updates")
                        div(fomantic.ui.item).innerHTML("Chat with us on <a href=\"https://matrix.to/#/#freenet-locutus:matrix.org\">Matrix</a>")
                    }
                }
                div(fomantic.ui.item) {
                    p().text("Fred")
                    div(fomantic.ui.bulleted.list) {
                        div(fomantic.ui.item).innerHTML("Follow <a href=\"https://freenetproject.org\">@freenetproject</a>")
                        div(fomantic.ui.item).innerHTML("IRC: <a href=\"https://web.libera.chat/?nick=FollowRabbit%7C?#freenet\">#freenet on libera</a>")
                    }
                }
            }

            h2(fomantic.ui.text).text("Support Us")
            div(fomantic.ui.bulleted.list) {
                div(fomantic.ui.item).text("Bitcoin: 3M3fbA7RDYdvYeaoR69cDCtVJqEodo9vth")
                div(fomantic.ui.item).text("Zcash: t1VHw1PHgzvMqEEd31ZBt3Vyy2UrG4J8utB")
                div(fomantic.ui.item).text("Ethereum: 0x79158A5Dbd9C0737CB27411817BD2759f5b9a9Ae")
                div(fomantic.ui.item).innerHTML("<a href=\"https://freenetproject.org/pages/donate.html\">Paypal & others</a>")
            }

        }




    }.setAttribute("background-color", "e8e8e8")
}

data class NewsItem(val date: Date, val description : String, val important : Boolean)

fun getNews(db: Firestore, maxNewsItems : Int = 6): List<NewsItem> {
    val newsCollection = db.collection("news-items")

    val newsDocuments = newsCollection.orderBy("date", Query.Direction.DESCENDING).limit(50).get().get().documents


    val newsItems = newsDocuments.map { doc ->
        val date = doc.getTimestamp("date")?.toDate() ?: error("Unable to retrieve date from document")
        val description = doc.getString("description") ?: error("Unable to retrieve description from document")
        val important = doc.getBoolean("important") ?: error("Unable to retrieve important from document")
        NewsItem(date, description, important)
    }.sortedByDescending { it.date }

    val newsItemList = ArrayList<NewsItem>()

    newsItems.filter { it.important }.take(maxNewsItems / 2).forEach { newsItemList.add(it) }
    newsItems.filter { !it.important }.take(maxNewsItems - newsItemList.size).forEach { newsItemList.add(it) }

    newsItems.sortedBy { it.date }

    return newsItemList;
}
