package org.freenet

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import kweb.*
import kweb.plugins.fomanticUI.fomantic
import java.util.*

fun ElementCreator<*>.landingPage(db: Firestore?) {
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

fun getNews(db: Firestore?, maxNewsItems : Int = 6): List<NewsItem> {
    if (db != null) {
        val newsCollection = db.collection("news-items")

        val newsDocuments = newsCollection.orderBy("date", Query.Direction.DESCENDING).limit(50).get().get().documents


        val newsItems = newsDocuments.map { doc -> doc.toObject(NewsItem::class.java) }.sortedByDescending { it.date }

        val newsItemList = ArrayList<NewsItem>()

        newsItems.filter { it.important }.take(maxNewsItems / 2).forEach { newsItemList.add(it) }
        newsItems.filter { !it.important }.take(maxNewsItems - newsItemList.size).forEach { newsItemList.add(it) }

        newsItems.sortedBy { it.date }

        return newsItemList;
    } else {
        return listOf(
            NewsItem(Date(), "Man bites dog", true),
            NewsItem(Date(), "Something awful happened", false),
            NewsItem(Date(), "Something great happened", true),
        )
    }
}

/*

<svg xmlns:svg="http://www.w3.org/2000/svg" xmlns="http://www.w3.org/2000/svg" version="1.0" width="30%" height="100%" viewBox="45 100 80 80"><link xmlns="" type="text/css" rel="stylesheet" id="dark-mode-custom-link"/><link xmlns="" type="text/css" rel="stylesheet" id="dark-mode-general-link"/><style xmlns="" lang="en" type="text/css" id="dark-mode-custom-style"/><style xmlns="" lang="en" type="text/css" id="dark-mode-native-style"/><style xmlns="" lang="en" type="text/css" id="dark-mode-native-sheet"/>
  <defs/>
  <g transform="translate(-00,-0)">
    <path d="M 80.03,122.66 C 94.49,107.06 104.32,113.99 87.53,124.89 C 82.19,128.36 83.58,129.37 87.17,128.82 C 92.24,128.05 95.01,129.02 96.24,129.98 C 99.18,132.28 100.65,130.57 101.64,128.82 C 103.61,125.35 110.25,129.12 105.39,132.75 C 101.59,135.58 102.12,138.77 105.57,140.61 C 111.96,144.01 126.30,150.75 124.14,157.93 C 123.26,160.86 121.67,163.62 118.07,159.89 C 111.32,152.92 106.83,146.97 91.28,144.54 C 81.24,142.96 71.15,140.33 61.46,145.25 C 56.90,147.67 51.16,149.23 49.94,146.37 C 48.35,142.64 56.47,139.57 64.32,138.82 C 69.42,138.33 73.99,134.99 67.00,133.29 C 58.39,130.64 68.28,123.09 74.50,124.89 C 78.55,126.07 78.95,123.83 80.03,122.66 z" style="fill:#ff0000;fill-opacity:0.0;stroke:#6D6D6D;stroke-opacity:0.2;stroke-width:1.5;"/>
  </g>
</svg>
 */