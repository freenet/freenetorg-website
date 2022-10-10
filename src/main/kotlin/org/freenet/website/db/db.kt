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

package org.freenet.website.db

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import org.freenet.website.isLocalTestingMode

val db: Firestore? = run {
    if (isLocalTestingMode) {
        null
    } else {
        val firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
            .setProjectId(System.getenv("GOOGLE_CLOUD_PROJECT_NAME"))
            .setCredentials(GoogleCredentials.getApplicationDefault())
            .build()
        firestoreOptions.service
    }

}