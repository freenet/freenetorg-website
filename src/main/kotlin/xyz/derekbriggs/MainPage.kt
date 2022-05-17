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
import java.util.regex.Pattern

const val usernameTableName = "reservedUsernames"
const val timeToReserveName = 60 * 1000 * 15//15 minutes

val firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
    .setProjectId("freenet-site")
    .setCredentials(GoogleCredentials.getApplicationDefault())
    .build()
val db = firestoreOptions.service

const val copyString = "The Internet has become increasingly centralized over the past 25 years, such that a handful of companies now effectively control the Internet infrastructure. This poses a threat to freedom of speech and democracy, as the public square is privately owned.\n" +
        "\n\n" +
        "Locutus is a software platform that makes it easy to create decentralized alternatives to today's centralized tech companies. These decentralized apps will be easy to use, scalable, and secured through cryptography. \n" +
        "\n" +
        "Trust and identity are critical in any decentralized system. By donating to its development, you can reserve your name on Freenet and in future may be granted \"trust tokens\" and other rewards for our early supporters. Names will be fully transferrable, similar to domain names today. \n" +
        "\n" +
        "So please donate now to help us create a more decentralized future!"

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
                    saveStripePaymentDetails(payIntent.toString(), customerEmail, payIntent.metadata["username"]!!, payIntent.amount, payIntent.charges.data[0].id)
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
                                p().text(copyString)
                            }
                        }

                        val username = KVar("")
                        val email = KVar("")
                        val donationAmount = KVar("")
                        val inputStatus = KVar(InputStatus.None)
                        div(fomantic.ui.grid.center.aligned) {
                            form(fomantic.ui.form) {
                                div(fomantic.field) {
                                    label().text("Username")
                                    div(fomantic.ui.icon.input.huge) {
                                        val usernameInput = input(type = InputType.text, placeholder = "Username", attributes = mapOf("id" to "usernameInput".json))
                                        usernameInput.on(retrieveJs = usernameInput.valueJsExpression).input { event ->
                                            username.value = event.retrieved.jsonPrimitive.content
                                            if(isNameAvailable(username.value)) {
                                                inputStatus.value = InputStatus.Available
                                            } else {
                                                inputStatus.value = InputStatus.NotAvailable
                                            }
                                        }
                                        render(inputStatus) {
                                            when(it) {
                                                InputStatus.None -> {
                                                    i(fomantic.search.circular.ui.icon)
                                                }
                                                InputStatus.Available -> {
                                                    i().classes("ui checkmark icon")
                                                }
                                                InputStatus.NotAvailable -> {
                                                    i().classes("ui cat icon")
                                                }
                                            }
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
                                }
                                div(fomantic.field) {
                                    label().text("Email")
                                    div(fomantic.ui.input.huge) {
                                        val emailInput = input(type = InputType.email, placeholder = "Email", attributes = mapOf("id" to "emailInput".json))
                                        emailInput.on(retrieveJs = emailInput.valueJsExpression).input { event ->
                                            email.value = event.retrieved.jsonPrimitive.content
                                        }
                                    }
                                }
                                div(fomantic.field) {
                                    label().text("Donation Amount")
                                    val selectedDonationAmount : KVar<String?> = KVar(null)
                                    div(fomantic.ui.buttons.three) {
                                        val oneButton = button(fomantic.ui.button).text("5")

                                        oneButton.setAttribute("class", selectedDonationAmount.map {
                                            if (it == "5") {
                                                "ui active button"
                                            } else {
                                                "ui button"
                                            }.json
                                        })

                                        oneButton.on.click {
                                            selectedDonationAmount.value = "5"
                                            donationAmount.value = "5"
                                        }

                                        val twoButton = button(fomantic.ui.button).text("10")
                                        twoButton.setAttribute("class", selectedDonationAmount.map {
                                            if (it == "10") {
                                                "ui active button"
                                            } else {
                                                "ui button"
                                            }.json
                                        })
                                        twoButton.on.click {
                                            selectedDonationAmount.value = "10"
                                            donationAmount.value = "10"
                                        }

                                        val threeButton = button(fomantic.ui.button).text("20")
                                        threeButton.on.click {
                                            selectedDonationAmount.value = "20"
                                            donationAmount.value = "20"
                                        }
                                        threeButton.setAttribute("class", selectedDonationAmount.map {
                                            if (it == "20") {
                                                "ui active button"
                                            } else {
                                                "ui button"
                                            }.json
                                        })
                                    }
                                    div(fomantic.ui.right.labeled.input) {
                                        val dollarSignLabel = label(fomantic.ui.label).text("$")
                                        dollarSignLabel.setAttribute("for", "donationInput")
                                        val donationInput = input(type = InputType.text, placeholder = "5.00",
                                            attributes = mapOf("id" to "donationInput".json))
                                        div(fomantic.ui.basic.label).text(".00")
                                        donationInput.setValue(donationAmount)
                                        donationInput.on.click {
                                            selectedDonationAmount.value = null
                                        }
                                        donationInput.on(retrieveJs = donationInput.valueJsExpression).input { event ->
                                            donationAmount.value = event.retrieved.jsonPrimitive.content
                                        }
                                    }
                                }
                                button(fomantic.ui.button).text("Reserve").on.click {
                                    println(email.value)
                                    if (isValidEmail(email.value)) {
                                        tempReserveName(username.value, browser.httpRequestInfo.request.headers["Referer"])
                                        val modal = div(fomantic.ui.modal) {
                                            renderCheckout()
                                        }
                                        browser.callJsFunction("$(\'#\' + {}).modal(\'show\');", modal.id.json)
                                        println("Showing modal: ${modal.id}")
                                    } else {
                                        p().text("Invalid Email")
                                    }
                                }
                            }
                        }

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
    val emailRegex = "^(.+)@(\\S+) \$."
    //val emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@" +
                 //    "[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})\$"
    //return (Pattern.compile(emailRegex)
        //.matcher(email).matches())
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
    browser.callJsFunction("initialize()")
    val paymentForm = form(mapOf("id" to JsonPrimitive("payment-form"))) {
        div(mapOf("id" to JsonPrimitive("payment-element")))
        button(mapOf("id" to JsonPrimitive("submit"))) {
            div(mapOf("id" to JsonPrimitive("spinner"))).classes("spinner hidden")
            span(mapOf("id" to JsonPrimitive("button-text"))).text("Pay Now")
        }
        div(mapOf("id" to JsonPrimitive("payment-message"))).classes("hidden")
    }
    div(fomantic.ui.actions) {
        val cancelButton = button(fomantic.ui.button).text("cancel").on.click {
            browser.callJsFunction("$(\'.ui.modal\').modal(\'close\');")
            paymentForm.deleteIfExists()
        }
        cancelButton.classes("ui cancel button")
    }
}

fun ElementCreator<*>.renderForm() {

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

fun tempReserveName(username: String, referer: String?) {
    val docRef = db.collection(usernameTableName).document(username)
    val data = HashMap<String, Any>()

    data["lowercaseUsername"] = username.lowercase()
    data["timeReserved"] = getTimeMillis()
    referer?.let {
        data["referer"] = it
    }
    docRef.set(data)
}

fun isNameAvailable(username: String) : Boolean {
    val usernameCollection = db.collection(usernameTableName)
    val query = usernameCollection.whereEqualTo("lowercaseUsername", username.lowercase())
    val querySnapshot = query.get()


    for (document in querySnapshot.get().documents) {
        //if the username in this document was reserved more than 'timeToReserveName' ago, and has a null transactionId, it is available
        println(document.toString())
        return ((document.get("Stripe_transaction_id") == null) && getTimeMillis() - document.get("timeReserved") as Long > timeToReserveName)

    }

    //the usernameToReserve does not exist in the database, and is available
    return true
}


//Called to finalize reserving a username, via a Stripe payment
fun saveStripePaymentDetails(stripePayIntentJson: String, email: String, username: String, donationAmount: Long, transactionId: String) {
    val docRef = db.collection(usernameTableName).document(username)
    val data = HashMap<String, Any>()

    data["stripePayIntent"] = stripePayIntentJson
    data["email"] = email
    //data["ipAddress"] =
    /*UserInfo.referer?.let {
        data["referer"] = it
    }*/
    data["donationAmount"] = donationAmount
    data["Stripe_transaction_id"] = transactionId
    data["paymentMethod"] = "Stripe"

    val result = docRef.update(data)
}

fun saveCustomer(stripeCustomerId: String) : String{
    val customer = Customer.retrieve(stripeCustomerId)
    val docRef = db.collection("Customers").document(customer.email)
    val data = HashMap<String, Any>()

    data["stripeCustomerId"] = stripeCustomerId
    docRef.set(data)
    return customer.email
}