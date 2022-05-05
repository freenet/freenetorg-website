package xyz.derekbriggs

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.FirestoreOptions
import com.stripe.Stripe
import com.stripe.model.Customer
import com.stripe.model.CustomerCollection
import com.stripe.model.PaymentIntent
import io.ktor.util.date.*
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kweb.*
import kweb.plugins.fomanticUI.fomantic
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.plugins.staticFiles.ResourceFolder
import kweb.plugins.staticFiles.StaticFilesPlugin
import kweb.state.KVar
import kweb.state.render
import kweb.util.json

val firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
    .setProjectId("freenet-site")
    .setCredentials(GoogleCredentials.getApplicationDefault())
    .build()
val db = firestoreOptions.service

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
                    //println(browser.httpRequestInfo.request.call.parameters)
                    val payIntent = PaymentIntent.retrieve(browser.httpRequestInfo.request.call.parameters["payment_intent"])
                    val customerEmail = saveCustomer(payIntent.charges.data[0].customer)
                    println(payIntent)
                    saveStripePaymentDetails(customerEmail, payIntent.metadata["username"]!!, payIntent.amount, payIntent.charges.data[0].id)
                    h1().text("Payment successful")
                }

                path("/cancel") {
                    h1().text("Payment cancelled")
                }

                path("/") {
                    browser.httpRequestInfo.request.headers["Referer"]?.let {
                        //UserInfo.referer = it
                    }

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

                            val username = KVar("")
                            val email = KVar("")
                            val donationAmount = KVar("")
                            val inputStatus = KVar(InputStatus.None)
                            div(fomantic.ui.container) {
                                div(fomantic.ui.input) {
                                    val usernameInput = input(type = InputType.text, placeholder = "Username", attributes = mapOf("id" to "usernameInput".json))
                                    usernameInput.on(retrieveJs = usernameInput.valueJsExpression).input { event ->
                                        username.value = event.retrieved.jsonPrimitive.content
                                    }
                                }
                                render(inputStatus) { inputStatus ->
                                    when(inputStatus) {
                                        InputStatus.None -> {}
                                        InputStatus.Available -> {
                                            p().text("Available") // Prettify with unicode
                                            // Possibly specify minimum-donation given username length
                                        }
                                        InputStatus.NotAvailable -> {
                                            p().text("Not Available") // Prettify with unicode
                                        }
                                    }
                                }
                                br()
                                div(fomantic.ui.input) {
                                    val emailInput = input(type = InputType.email, placeholder = "Email", attributes = mapOf("id" to "emailInput".json))
                                    emailInput.on(retrieveJs = emailInput.valueJsExpression).input { event ->
                                        email.value = event.retrieved.jsonPrimitive.content
                                    }
                                }
                                br()
                                div(fomantic.ui.input) {
                                    val donationInput = input(type = InputType.text, placeholder = "5.00", attributes = mapOf("id" to "donationInput".json))
                                    donationInput.on(retrieveJs = donationInput.valueJsExpression).input { event ->
                                        donationAmount.value = event.retrieved.jsonPrimitive.content
                                    }
                                }
                                br()
                                button(fomantic.ui.button).text("Reserve").on.click {
                                    renderCheckout()
                                    /*if (isValidEmail(email.value)) {

                                        url.value = "/checkout"
                                    } else {
                                        //TODO Alert user they have typed an invalid email address
                                    }*/
                                }
                            }
                        }.setAttribute("style", """min-height: 700px;""")
                    }
                }
                path("/checkout") {
                    println(browser.httpRequestInfo.request.call.request)
                    //h1().text("Hello ${browser.httpRequestInfo.request.call.request}")
                    //h1().text("Hello ${UserInfo.emailAddress}, you are reserving ${UserInfo.usernameToReserve} for ${UserInfo.donationAmount}")
                    renderCheckout()
                }
            }
        }.classes("pushable")
    }

}

fun isValidEmail(email : String) : Boolean {
    return true
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

enum class InputStatus {
    None, Available, NotAvailable
}

fun tempReserveName(username: String) {
    val docRef = db.collection("reservedUsernames").document(username)
    val data = HashMap<String, Any>()

    data["timeReserved"] = getTimeMillis()
    docRef.set(data)
}

fun isNameAvailable(username: String) : Boolean {
    val docRef = db.collection("reservedUsernames").document(username)
    val future = docRef.get()

    val document = future.get()
    if (document.exists()) {
        //TODO: add if statement to check if existing document was created more than X minutes ago, and has a non-null transactionID
        return false
    }
    return true
}


//Called to finalize reserving a username, via a Stripe payment
fun saveStripePaymentDetails(email: String, username: String, donationAmount: Long, transactionId: String) {
    val docRef = db.collection("reservedUsernames").document(username)
    val data = HashMap<String, Any>()

    data["email"] = email
    //data["ipAddress"] =
    /*UserInfo.referer?.let {
        data["referer"] = it
    }*/
    data["donationAmount"] = donationAmount
    data["Stripe_transaction_id"] = transactionId
    data["paymentMethod"] = "Stripe"

    val result = docRef.set(data)
}

fun saveCustomer(stripeCustomerId: String) : String{
    val customer = Customer.retrieve(stripeCustomerId)
    val docRef = db.collection("Customers").document(customer.email)
    val data = HashMap<String, Any>()

    data["stripeCustomerId"] = stripeCustomerId
    docRef.set(data)
    return customer.email
}