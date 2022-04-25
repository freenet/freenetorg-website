package xyz.derekbriggs

import com.stripe.Stripe
import kotlinx.serialization.json.JsonPrimitive
import kweb.*
import kweb.plugins.fomanticUI.fomantic
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.plugins.staticFiles.ResourceFolder
import kweb.plugins.staticFiles.StaticFilesPlugin
import kweb.state.render

fun main() {

    Stripe.apiKey = "sk_test_51KYyh3HSi8gwBwE3syIyEQK1jd1HAJfWftPyWOspGL4cP0xfUz8RWfpHiRUGEjaIoKHBojzNvJQ6E7t3pb6E1l8l0032ZZFgK7"

    Kweb(port = 8080, debug = false, plugins = listOf(fomanticUIPlugin, StripeRoutePlugin(),
        StaticFilesPlugin(ResourceFolder("static"), "/static/stripeCheckout"))) {
        doc.head {
            element("meta").setAttribute("content", "width=device-width, initial-scale=1").setAttribute("name", "viewport")
            element("link").setAttribute("rel", "stylesheet").setAttribute("href", "/static/stripeCheckout/checkout.css")
            element("script").setAttribute("src", "https://js.stripe.com/v3/")

        }
        doc.body {
            route {

                path("/success") {
                    println(browser.httpRequestInfo.request.call.parameters)
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
                                    val usernameInput = input(type = InputType.text, placeholder = "Username").setAttribute("id", "usernameInput")
                                }
                                br()
                                div(fomantic.ui.input) {
                                    val emailInput = input(type = InputType.email, placeholder = "Email").setAttribute("id", "emailInput")
                                }
                                br()
                                div(fomantic.ui.input) {
                                    val donationInput = input(type = InputType.text, placeholder = "5.00").setAttribute("id", "donationInput")
                                }
                                br()
                                button(fomantic.ui.button).text("Reserve").on.click {
                                    url.value = "/checkout/frank"
                                    //disableElement("usernameInput")
                                    //renderCheckout()
                                }
                            }


                        }.setAttribute("style", """min-height: 700px;""")
                    }
                }
                path("/checkout/{email}") { params ->
                    val email = params.getValue("email").value
                    h1().text("Hello, $email")
                    renderCheckout()
                }
            }
        }.classes("pushable")
    }
}

fun ElementCreator<*>.disableElement(elementId : String) {
    val disableElementJs = """
        let elementId = {};
        document.querySelector("#" + elementId).disabled = true;
    """.trimIndent()
    browser.callJsFunction(disableElementJs, JsonPrimitive(elementId))
}
fun ElementCreator<*>.renderCheckout() {
    form(mapOf("id" to JsonPrimitive("payment-form"))) {
        div(mapOf("id" to JsonPrimitive("payment-element")))
        button(mapOf("id" to JsonPrimitive("submit"))) {
            div(mapOf("id" to JsonPrimitive("spinner"))).classes("spinner hidden")
            span(mapOf("id" to JsonPrimitive("button-text"))).text("Pay Now")
            element("script").setAttribute("src", "/static/stripeCheckout/checkout.js")
        }
        div(mapOf("id" to JsonPrimitive("payment-message"))).classes("hidden")
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
