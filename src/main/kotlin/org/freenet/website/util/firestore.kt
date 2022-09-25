package org.freenet.website.util

import com.google.cloud.firestore.QueryDocumentSnapshot

inline fun <reified T> QueryDocumentSnapshot.toObject(): T = this.toObject(T::class.java)