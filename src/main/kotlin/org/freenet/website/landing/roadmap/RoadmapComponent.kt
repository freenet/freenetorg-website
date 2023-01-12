package org.freenet.website.landing.roadmap

import kweb.*
import kweb.components.Component
import kweb.plugins.fomanticUI.fomantic
import kweb.state.render
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

fun Component.roadmapComponent() {
    render(PivotalTracker.releases) { releases ->
        if (releases != null) {
            h3(fomantic.ui.text).text("Roadmap")

            table { el ->
                el.classes("ui very basic selectable small table")

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
                                    e.text(release.name)
                                    e.href = release.url
                                }
                            }
                            td().text(release.date.format(formatter))

                        }
                    }
                }
            }

            p().innerHTML("""
               Our project on Pivotal Tracker gives a more detailed roadmap. 
               Check it out <a href="https://www.pivotaltracker.com/n/projects/2477110">here</a>.
            """.trimIndent())
        }
    }
}