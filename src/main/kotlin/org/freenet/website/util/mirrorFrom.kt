package org.freenet.website.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kweb.state.KVar
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun getUserManualPage(url: String, modify : (Document) -> Unit = {}): KVar<String?> {
    val kv: KVar<String?> = KVar(null)
    val scope = CoroutineScope(Dispatchers.IO)
    scope.launch {
        val document = Jsoup.connect(url).get()
        modify(document)
        cleanUserManualPage(document)
        kv.value = document.select("main").html()
    }

    return kv
}

private fun cleanUserManualPage(doc : Document) {
    updateHeaderText(doc, "h1")
    updateHeaderText(doc, "h2")
    updateHeaderText(doc, "h3")

    // Convert relative URLs to absolute URLs for <img> elements.
    doc.select("img[src]").forEach { img ->
        val absoluteUrl = img.absUrl("src")
        img.attr("src", absoluteUrl)
    }

    // Convert relative URLs to absolute URLs for <a> elements.
    doc.select("a[href]").forEach { anchor ->
        val absoluteUrl = anchor.absUrl("href")
        anchor.attr("href", absoluteUrl)
    }
}

private fun updateHeaderText(document: Document, headingTag: String) {
    val selector = "$headingTag a.header"
    document.select(selector).forEach { header ->
        val headerText = header.text()
        header.parent()?.text(headerText)
    }
}