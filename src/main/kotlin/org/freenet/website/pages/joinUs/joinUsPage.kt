package org.freenet.website.pages.joinUs

import kweb.components.Component
import kweb.h1
import kweb.p
import kweb.section

/**
 * This tab could focus on community engagement and provide information
 * on how individuals and organizations can get involved with Freenet.
 * It could include links to the Freenet User Manual, the Matrix chat,
 * and the Freenet Classic community. It could also provide
 * information on how to donate to Freenet and how grant organizations
 * can support the project.
 */
fun Component.joinUsPage() {
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
}