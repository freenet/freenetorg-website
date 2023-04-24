package org.freenet.website.landing.blog

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import kweb.state.ObservableList
import org.freenet.website.util.getFirst
import org.freenet.website.util.getObservableCollection
import java.util.*

fun recentBlogs(db: Firestore): ObservableList<BlogEntry> {
    return db.collection("blog-items")
        .orderBy("date", Query.Direction.DESCENDING)
        .limit(50)
        .getObservableCollection()

}

fun getBlogEntry(db: Firestore, url: String): BlogEntry {
    return db.collection("blog-entries")
        .whereEqualTo("url", url)
        .getFirst()

}

data class BlogEntry(val date: Date, val title : String, val url : String, val body : String) {
    // Required for Firestore toObject
    @Suppress("unused")
    constructor() : this(Date(), "", "