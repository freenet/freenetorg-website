import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import kweb.*
import kweb.plugins.fomanticUI.fomantic
import java.util.*

const val MAX_NEWS_ITEMS = 7

fun ElementCreator<*>.homePage(db: Firestore) {
    val newsCollection = db.collection("news-items")

    val newsDocuments = newsCollection.orderBy("date", Query.Direction.DESCENDING).limit(50).get().get().documents

    data class NewsItem(val date: Date, val description : String, val important : Boolean)

    val newsItems = newsDocuments.map { doc ->
        val date = doc.getTimestamp("date")?.toDate() ?: error("Unable to retrieve date from document")
        val description = doc.getString("description") ?: error("Unable to retrieve description from document")
        val important = doc.getBoolean("important") ?: error("Unable to retrieve important from document")
        NewsItem(date, description, important)
    }.sortedByDescending { it.date }

    val newsItemList = ArrayList<NewsItem>()

    newsItems.filter { it.important }.take(MAX_NEWS_ITEMS).forEach { newsItemList.add(it) }
    newsItems.filter { !it.important }.take(MAX_NEWS_ITEMS - newsItemList.size).forEach { newsItemList.add(it) }

    div(fomantic.ui.text.center.aligned.container) {
        div(fomantic.ui.text.left.aligned.container) {
            div() {
                h1(fomantic.ui.text).text("Freenet.org")
                h2(fomantic.ui.text).text("Declare your digital independence")
            }

            br()

            h2(fomantic.ui.text).text("Freenet")
            p(fomantic.ui.text).text("23 years ago we created the original Freenet, the first distributed, decentralized peer-to-peer network. It pioneered " +
                    "technologies like cryptographic contracts and small-world networks, and is still under active development.\n" +
                    "\n")
            p(fomantic.ui.text).text("Today we’re building Locutus, which will make it easy for developers to create and deploy decentralized alternatives to today’s centralized tech companies. " +
                    "These decentralized apps will be easy to use, scalable, and secured through cryptography.")

            h2(fomantic.ui.text).text("Locutus News")
            div(fomantic.ui.bulleted.list) {
                for (newsItem in newsItemList) {
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
                div(fomantic.ui.item).text("Fred - The original Freenet software, first released in March 2000 and continuously developed since")
                div(fomantic.ui.item).text("Locutus - A decentralized application layer for the Internet, first release expected September 2022")
            }

            h2(fomantic.ui.text).text("Community")
            div(fomantic.ui.bulleted.list) {
                div(fomantic.ui.item).text("r/freenet on Reddit")
                div(fomantic.ui.item) {
                    p().text("Locutus")
                    div(fomantic.ui.bulleted.list) {
                        div(fomantic.ui.item).text("Follow @freenetorg on Twitter for updates")
                        div(fomantic.ui.item).text("Chat with us on Matrix")
                    }
                }
                div(fomantic.ui.item) {
                    p().text("Fred")
                    div(fomantic.ui.bulleted.list) {
                        div(fomantic.ui.item).text("Follow @freenetproject")
                        div(fomantic.ui.item).text("IRC: #freenet on libera")
                    }
                }
            }

            h2(fomantic.ui.text).text("Support Us")
            div(fomantic.ui.bulleted.list) {
                div(fomantic.ui.item).text("Bitcoin: 3M3fbA7RDYdvYeaoR69cDCtVJqEodo9vth")
                div(fomantic.ui.item).text("Zcash: t1VHw1PHgzvMqEEd31ZBt3Vyy2UrG4J8utB")
                div(fomantic.ui.item).text("Ethereum: 0x79158A5Dbd9C0737CB27411817BD2759f5b9a9Ae")
                div(fomantic.ui.item).text("Paypal & others: link")
            }

        }




    }.setAttribute("background-color", "e8e8e8")
}