package xyz.derekbriggs

import com.stripe.Stripe
import kweb.*
import kweb.plugins.fomanticUI.fomantic
import kweb.plugins.fomanticUI.fomanticUIPlugin

fun main() {

    Stripe.apiKey = "sk_test_51KYyh3HSi8gwBwE3syIyEQK1jd1HAJfWftPyWOspGL4cP0xfUz8RWfpHiRUGEjaIoKHBojzNvJQ6E7t3pb6E1l8l0032ZZFgK7"

    Kweb(port = 8080, debug = false, plugins = listOf(fomanticUIPlugin, StripeRoutePlugin())) {
        doc.body {
            route {

                path("/success") {
                    h1().text("Payment successful")
                }

                path("/cancel") {
                    h1().text("Payment cancelled")
                }

                path("/") {

                    titleBar()

                    div(fomantic.pusher) {
                        div(fomantic.ui.inverted.vertical.masthead.center.aligned.segment) {
                            div(fomantic.ui.container) {
                                div(fomantic.ui.large.secondary.inverted.pointing.menu) {
                                    a(fomantic.item).text("Home")
                                    a(fomantic.item).text("About")
                                }
                            }
                            div(fomantic.ui.text.container) {
                                h1(fomantic.ui.inverted.header).setAttribute("style", """margin-top: 3em; font-size: 4em;""").text("Locutus")
                                h2().text("Do cool things")
                            }

                            div(fomantic.ui.container) {
                                div(fomantic.ui.input) {
                                    val input = input(type = InputType.text, placeholder = "Username").setAttribute("id", "usernameInput")
                                }
                                br()
                                div(fomantic.ui.input) {
                                    val priceInput = input(type = InputType.text, placeholder = "5.00")
                                }
                                br()
                                a() {
                                    button(fomantic.ui.button).text("Reserve")
                                }.setAttribute("href", "/checkout/")
                            }
                        }.setAttribute("style", """min-height: 700px;""")
                    }
                }
            }
        }.classes("pushable")
    }
}

fun ElementCreator<*>.titleBar() {
    div(fomantic.ui.large.top.fixed.menu.transition.hidden) {
        div(fomantic.ui.container) {
            a(fomantic.item).text("Home")
            a(fomantic.item).text("About")
        }
    }

    div(fomantic.ui.vertical.inverted.sidebar.menu.left) { //sidebar for mobile screens
        a(fomantic.item).text("Home")
        a(fomantic.item).text("About")
    }
}