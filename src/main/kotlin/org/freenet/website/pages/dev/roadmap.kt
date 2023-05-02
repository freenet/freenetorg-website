package org.freenet.website.pages.dev

import kweb.*
import kweb.components.Component
import kweb.state.render
import kweb.util.json
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

fun Component.roadmap() {
    render(PivotalTracker.releases) { releases ->
        h3().classes("title").text("Roadmap")
        if (releases != null) {
            p().innerHTML(
                """
               Projected milestones completion dates, these are estimates only and will change in realtime. 
               See the <a href="https://www.pivotaltracker.com/n/projects/2477110">project</a> in Pivotal
               Tracker for details.
            """.trimIndent()
            )
            table { el ->
                el.classes("table", "is-hoverable")

                thead {
                    tr {
                        th().text("Milestone")
                        th().text("Projected")
                    }
                }

                tbody {
                    for (release in releases) {
                        tr {
                            td {
                                a { e ->
                                    e.setAttributes("style" to "text-decoration: none;".json)
                                    e.text(release.name)
                                    e.href = release.url
                                }
                            }
                            td().text(release.date.format(formatter))

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
}