package org.freenet.website.util

import kweb.a
import kweb.components.Component
import kweb.i
import kweb.section
import kweb.span

fun Component.iconButton(html : String, href : String, icon : Array<String>, buttonClasses : Array<String> = arrayOf("button")) {
    a { a ->
        a.classes(*buttonClasses)
        a.href = href

        span { span ->
            span.classes("icon")

            i { i ->
                i.classes(*icon)
            }
        }

        span { span ->
            span.innerHTML(html)
        }

    }
}
/*
fun Component.workInProgress() {
    section { section ->
        section.classes("section")
        section.innerHTML("""
                <div class="container">
                  <div class="notification is-warning">
                    <p class="title is-4">Under Construction</p>
                    <p class="subtitle is-6">We're working hard to improve our website. Please check back soon!</p>
                  </div>
                </div>
        """.trimIndent())
    }
}*/