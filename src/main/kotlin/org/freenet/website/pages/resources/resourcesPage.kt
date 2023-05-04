package org.freenet.website.pages.resources

import kweb.components.Component
import kweb.section

/**
 * This tab could provide additional resources for visitors, such as video
 * introductions, presentations, research papers, and other media related
 * to Freenet. It could also include links to external resources, such as
 * the YouTube video of Ian's talk and the original Freenet Classic
 * website.
 */
fun Component.resourcesPage() {
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