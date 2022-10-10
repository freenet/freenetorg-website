/*
 *         Freenet.org - web application
 *         Copyright (C) 2022  Freenet Project Inc
 *
 *         This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as
 *         published by the Free Software Foundation, either version 3 of the
 *         License, or (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package org.freenet.website.landing

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Query
import kweb.ElementCreator
import kweb.state.CloseReason
import kweb.state.KVal
import kweb.state.KVar
import org.freenet.website.util.toObject
import java.util.*

fun retrieveNews(db: Firestore): KVal<List<NewsItem>> {
    val newsCollection = db.collection("news-items")
    val kv : KVar<List<NewsItem>> = KVar(emptyList())

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