package org.freenet.website.landing.news

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import kweb.state.ObservableList
import org.freenet.website.util.getObservableCollection
import java.util.*

fun retrieveNews(db: Firestore): ObservableList<NewsItem> {
    return db.collection("news-items")
        .orderBy("date", Query.Direction.DESCENDING)
        .limit(50)
        .getObservableCollection()

}

data class NewsItem(val date: Date, val description : String, val important : Boolean) {
    // Required for Firestore toObject
    @Suppress("unused")
    constructor() : this(Date(), "", false)
}

