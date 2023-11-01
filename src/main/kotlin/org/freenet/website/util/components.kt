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