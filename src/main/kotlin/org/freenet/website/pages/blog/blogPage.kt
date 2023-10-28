package org.freenet.website.pages.blog

import kweb.*
import kweb.components.Component
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

fun Component.blogPage(number: Int? = null) {
    section { section ->
        section.classes("section")

        if (number == null) {
            h1().classes("title").text("Freenet Blog")

            div { div ->
                div.classes("container")
                val discussions = GitHubDiscussions.discussions
                if (discussions == null) {
                    h3 { it.text("Loading, please refresh page.") }
                } else {
                    discussions.discussions.forEach { discussion ->
                        a(href = "/blog/${discussion.number}/${formatForUrl(discussion.title)}.html") {
                            it["style"] = "color: #000000;"
                            div {
                                it.classes("box")
                                div {
                                    it.classes("title", "is-4")
                                    it.text(discussion.title)
                                }
                                div {
                                    it.classes("subtitle", "is-6")
                                    it.text(discussion.createdAt.asFriendlyDate())
                                }
                            }
                        }
                    }
                }
            }
        } else {
            val discussion = GitHubDiscussions.discussions?.discussionsByNumber?.get(number)
            if (discussion == null) {
                h3 { it.text("Discussion $number not found") }
            } else {
                div { div ->
                    div.classes("container")
                    div {
                        it.classes("title", "is-3")
                        it.text(discussion.title)
                    }
                    div {
                        it.classes("subtitle", "is-6")
                        it.text(discussion.createdAt.asFriendlyDate())
                    }
                    div {
                        it.classes("content")
                        it.innerHTML(discussion.bodyHTML)
                    }
                }
            }
        }

    }
}

private fun formatForUrl(title: String): String {
    return title.lowercase(Locale.getDefault())
        .replace(Regex("\\s"), "-")
        .replace(Regex("[^a-z0-9-]"), "")
}


private fun getOrdinal(n: Int): String {
    val suffixes = arrayOf("th", "st", "nd", "rd")
    return when (n % 100) {
        11, 12, 13 -> n.toString() + "th"
        else -> n.toString() + suffixes[n % 10.coerceIn(1..3)]
    }
}

fun Instant.asFriendlyDate(): String {
    val localDate = this.atZone(ZoneId.systemDefault()).toLocalDate()
    val day = getOrdinal(localDate.dayOfMonth)
    val month = localDate.month.getDisplayName(TextStyle.FULL, Locale.US)
    val year = localDate.year
    return "$day $month, $year"
}