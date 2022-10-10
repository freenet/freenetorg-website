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
import kweb.state.CloseReason
import kweb.state.KVal
import kweb.state.KVar
import org.freenet.website.util.toObject
import org.kohsuke.github.GHIssueState
import org.kohsuke.github.GitHub
import java.util.*

private val github = GitHub.connect()

private val kwebRepo = github.getRepository("kwebio/kweb-core")

fun retrieveIssues(): KVal<List<NewsItem>> {

    kwebRepo.getIssues(GHIssueState.OPEN).toList().map { issue ->

        if (issue.labels.con)
    }
}
