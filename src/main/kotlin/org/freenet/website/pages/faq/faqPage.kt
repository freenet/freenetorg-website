package org.freenet.website.pages.faq

import kweb.*
import kweb.components.Component
import kweb.state.render
import org.jsoup.Jsoup
import retrieveMDPage

fun Component.faqPage() {
    val faqHtml =
        retrieveMDPage("https://raw.githubusercontent.com/wiki/freenet/freenet-core/Frequently-Asked-Questions.md")
    val jsoupDoc = Jsoup.parse(faqHtml)
    var faqNumber = 0
    val headings = ArrayList<String>()
    jsoupDoc.select("h1").forEach { h1 ->
        h1.id("faq-$faqNumber")
        headings += h1.html()
        faqNumber++
    }
    h1().text("Frequently Asked Questions")
    ul {
        for ((ix, heading) in headings.withIndex()) {
            li {
                a { a ->
                    a["href"] = "#faq-$ix"

                    a.innerHTML(heading)
                }
            }
        }
    }
    div().innerHTML(jsoupDoc.body().html())
}