package org.freenet.website.util

import io.ktor.server.request.*
import kweb.client.HttpRequestInfo
import org.freenet.website.db.db
import java.util.*

data class Visit(
    val time: Date,
    val remoteHostHash: Int,
    val userAgent: String,
    val visitUrl: String,
    val referrer: String
) {
    // Needed for Firestore toObject
    @Suppress("unused")
    constructor() : this(Date(), 0, "", "", "")
}

fun recordVisit(httpRequestInfo: HttpRequestInfo) {
    if (db != null && httpRequestInfo.requestedUrl.contains("freenet.org") && httpRequestInfo.userAgent?.contains("Google") != true) {
        val visit = Visit(
            time = Date(),
            // Hash for user privacy
            remoteHostHash = httpRequestInfo.remoteHost.hashCode(),
            userAgent = httpRequestInfo.userAgent ?: "unknown",
            visitUrl = httpRequestInfo.requestedUrl,
            referrer = httpRequestInfo.request.headers["Referer"] ?: ""
        )
        db.collection("visits").add(visit)
    }
}