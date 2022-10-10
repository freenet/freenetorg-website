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

package org.freenet.website.util

import io.ktor.server.request.*
import kweb.client.HttpRequestInfo
import org.freenet.website.db.db
import java.util.*

data class Visit(val time : Date, val remoteHostHash : Int, val userAgent : String, val visitUrl : String, val referrer : String) {
    constructor() : this(Date(), 0, "", "", "")
}

fun recordVisit(httpRequestInfo: HttpRequestInfo) {
    if (db != null && httpRequestInfo.requestedUrl.contains("freenet.org") && httpRequestInfo.userAgent?.contains("Google") != true) {
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