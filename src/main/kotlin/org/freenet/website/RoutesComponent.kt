package org.freenet.website

import kweb.components.Component
import kweb.route
import kweb.state.ObservableList
import org.freenet.website.landing.landingPageComponent
import org.freenet.website.landing.news.NewsItem

fun Component.routesComponent(
    latestNewsItems: ObservableList<NewsItem>,
) {
    route {
        path("") {
            landingPageComponent(latestNewsItems)
        }

        path("blog") {
            blogComponent()
        }
    }

}