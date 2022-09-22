package org.freenet

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import kweb.ElementCreator
import kweb.state.CloseReason
import kweb.state.KVal
import kweb.state.KVar
import org.freenet.util.toObject
import java.util.*

fun retrieveNews(db: Firestore): KVal<List<NewsItem>> {
    val newsCollection = db.collection("news-items")

    val newsDocuments = newsCollection.orderBy("date", Query.Direction.DESCENDING).limit(50).get().get().documents

    val newsItems : List<NewsItem> = newsDocuments.map { doc -> doc.toObject() }

    val newsItemList = ArrayList<NewsItem>()

    newsItems.filter { it.important }.take(MAX_NEWS_ITEMS).forEach { newsItemList.add(it) }
    newsItems.filter { !it.important }.take(MAX_NEWS_ITEMS - newsItemList.size).forEach { newsItemList.add(it) }

    val kv = KVar(newsItems)

    val registration = newsCollection.orderBy("date", Query.Direction.DESCENDING).limit(50).addSnapshotListener { value, error ->
        val newNewsItems : List<NewsItem> = value?.documents?.map { doc -> doc.toObject() } ?: emptyList()
        kv.value = newNewsItems
    }

    Runtime.getRuntime().addShutdownHook(Thread {
        registration.remove()
        kv.close(CloseReason("Cleanup"))
    })

    return kv
}

data class NewsItem(val date: Date, val description : String, val important : Boolean) {
    // Required for Firestore toObject
    constructor() : this(Date(), "", false)
}