package org.freenet.website.pages.faq

import kweb.*
import kweb.components.Component
import kweb.state.render
import org.freenet.website.util.retrievePage
import org.jsoup.Jsoup

// TODO: Move code below to shared KVar
// TODO:
private val faqHtml = retrievePage("https://raw.githubusercontent.com/wiki/freenet/locutus/Frequently-Asked-Questions.md")

fun Component.faqPage() {
    render(faqHtml) { faqHtml ->
        if (faqHtml == null) {
            div().text("Loading FAQ...")
        } else {
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
    }
}
