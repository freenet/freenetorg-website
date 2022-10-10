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