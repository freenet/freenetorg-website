package org.freenet.website.util

import io.ktor.server.request.*
import kweb.client.HttpRequestInfo
import org.freenet.website.db.db
import java.util.*

data class Visit(val time : Date, val remoteHostHash : Int, val userAgent : String, val visitUrl : String, val referrer : String) {
    constructor() : this(Date(), 0, "", "", "")
}

fun recordVisit(httpRequestInfo: HttpRequestInfo) {
    if (db != null && httpRequestInfo.userAgent != "GoogleHC/1.0") {
        val visit = Visit(
            time = Date(),
            remoteHostHash = httpRequestInfo.remoteHost.hashCode(),
            userAgent = httpRequestInfo.userAgent ?: "unknown",
            visitUrl = httpRequestInfo.requestedUrl,
            referrer = httpRequestInfo.request.header("Referer") ?: ""
        )
        db.collection("visits").add(visit)
    }
}