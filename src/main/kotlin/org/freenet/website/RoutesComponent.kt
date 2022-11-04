package org.freenet.website

import kweb.ElementCreator
import kweb.route
import kweb.state.Component
import kweb.state.ObservableList
import kweb.state.render
import org.freenet.website.landing.LandingPageComponent
import org.freenet.website.landing.news.NewsItem

class RoutesComponent(private val latestNewsItems : ObservableList<NewsItem>) : Component {
    override fun render(elementCreator: ElementCreator<*>) {
        with(elementCreator) {
            route {
                path("") {
                    render(LandingPageComponent(latestNewsItems))
                }
            }

        }
    }
}