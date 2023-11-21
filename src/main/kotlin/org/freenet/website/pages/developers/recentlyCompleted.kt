package org.freenet.website.pages.developers

import kweb.*
import kweb.components.Component
import kweb.state.render
import kweb.util.json
import org.freenet.website.pages.blog.asFriendlyDate
import org.freenet.website.util.Github
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

fun Component.recentlyCompleted() {
    val pullRequests = Github.pullRequests

    h3().classes("title").text("Recently completed")
    if (pullRequests != null) {
        table { el ->
            el.classes("table", "is-hoverable")

            thead {
                tr {
                    th().text("Description")
                    th().text("Merged")
                    th().text("Author")
                }
            }

            tbody {
                for (pr in pullRequests) {
                    tr {

                        td {
                            a { e ->
                                e.setAttributes("style" to "text-decoration: none;".json)
                                e.text(pr.title)
                                e.href = pr.url
                            }
                        }

                        td().text(pr.mergedAt.asFriendlyDate())

                        td {
                            a { e ->
                                e.setAttributes("style" to "text-decoration: none;".json)
                                e.text(pr.author.login)
                                e.href = pr.author.url
                            }
                        }

                    }
                }
            }
        }

    } else {
        h3 { h3 ->
            h3.classes("subtitle")
            h3.text("Loading...")
        }
    }
}