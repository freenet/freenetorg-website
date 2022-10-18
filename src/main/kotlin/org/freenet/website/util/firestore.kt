package org.freenet.website.util

import com.google.cloud.firestore.DocumentChange
import com.google.cloud.firestore.Query
import com.google.cloud.firestore.QueryDocumentSnapshot
import kweb.state.ObservableList
import java.util.ArrayList

inline fun <reified T> QueryDocumentSnapshot.toObject(): T = this.toObject(T::class.java)

inline fun <reified T : Any> Query.getObservableCollection() : ObservableList<T> {
    val obsList = ObservableList<T>(ArrayList())

    val registration = addSnapshotListener { value, error ->
        require(error == null) { "Error retrieving documents: $error" }
        requireNotNull(value)
        for (change in value.documentChanges) {
            when (change.type) {
                DocumentChange.Type.ADDED -> {
                    obsList.add(change.newIndex, change.document.toObject())
                }
                DocumentChange.Type.MODIFIED ->  {
                    obsList[change.newIndex] = change.document.toObject()
                }
                DocumentChange.Type.REMOVED -> {
                    obsList.removeAt(change.oldIndex)
                }
            }
        }
    }

    obsList.addCloseListener { registration.remove() }

    return obsList
}