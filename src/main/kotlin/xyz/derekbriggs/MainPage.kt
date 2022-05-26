package xyz.derekbriggs

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.FirestoreOptions
import com.stripe.Stripe
import com.stripe.model.Customer
import com.stripe.model.PaymentIntent
import io.ktor.util.date.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kweb.*
import kweb.plugins.fomanticUI.fomantic
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.plugins.staticFiles.ResourceFolder
import kweb.plugins.staticFiles.StaticFilesPlugin
import kweb.state.KVal
import kweb.state.KVar
import kweb.state.render
import kweb.util.json

const val usernameTableName = "reservedUsernames"
const val timeToReserveName = 60 * 1000 * 15//15 minutes

val firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
    .setProjectId("freenet-site")
    .setCredentials(GoogleCredentials.getApplicationDefault())
    .build()
val db = firestoreOptions.service

sealed class InputStatus {
    object None : InputStatus()
    class Available(val minDonationAmount : Int) : InputStatus()
    object NotAvailable : InputStatus()
    object Invalid : InputStatus()
}

enum class EmailStatus {
    Empty, Valid, Invalid
}

fun main() {

    Stripe.apiKey = "sk_test_51KYyh3HSi8gwBwE3syIyEQK1jd1HAJfWftPyWOspGL4cP0xfUz8RWfpHiRUGEjaIoKHBojzNvJQ6E7t3pb6E1l8l0032ZZFgK7"

    Kweb(port = 8080, debug = false, plugins = listOf(fomanticUIPlugin, StripeRoutePlugin(),
        StaticFilesPlugin(ResourceFolder("static"), "/static/stripeCheckout"))) {
        doc.head {
            element("meta").setAttribute("content", "width=device-width, initial-scale=1").setAttribute("name", "viewport")
            element("link").setAttribute("rel", "stylesheet").setAttribute("href", "/static/stripeCheckout/checkout.css")
            element("link").setAttribute("rel", "stylesheet").setAttribute("href", "/static/stripeCheckout/homepage.css")
            element("script").setAttribute("src", "https://js.stripe.com/v3/")
            element("script").setAttribute("src", "/static/stripeCheckout/checkout.js")

        }
        doc.body {
            //These various menus are for supporting mobile devices
            /*div(fomantic.ui.large.top.fixed.menu.transition.hidden) {
                div(fomantic.ui.container) {
                    a(fomantic.item).text("Home")
                    a(fomantic.item).text("About")
                }
            }

            div(fomantic.ui.vertical.inverted.sidebar.menu.left) { //sidebar for mobile screens
                a(fomantic.item).text("Home")
                a(fomantic.item).text("About")
            }
            val menuBar = div(fomantic.ui.inverted.vertical.center.aligned.segment) {
                div(fomantic.ui.container) {
                    div(fomantic.ui.large.secondary.inverted.pointing.menu) {
                        a(fomantic.item).text("Home")
                        a(fomantic.item).text("About")
                    }
                }
            }
            menuBar.setAttribute("id", "menuBar")*/
            route {

                path("/success") {
                    //println(browser.httpRequestInfo.request.call.parameters)
                    val payIntent = PaymentIntent.retrieve(browser.httpRequestInfo.request.call.parameters["payment_intent"])
                    val customerEmail = saveCustomer(payIntent.charges.data[0].customer)
                    saveStripePaymentDetails(payIntent.toString(), customerEmail, payIntent.metadata["username"]!!, payIntent.amount, payIntent.charges.data[0].id)
                    div(fomantic.ui.container.center.aligned) {
                        p().text("Thank you for confirming the username ${payIntent.metadata["username"]!!}")
                        p().text("You will receive payment receipt at $customerEmail")
                        p().text("Follow Freenet on Twitter")
                    }
                }

                path("/cancel") {
                    h1().text("Payment cancelled")
                }

                path("/") {

                    div(fomantic.ui.text.center.aligned.container) {
                        h1(fomantic.ui).setAttribute("style", """font-size: 4em;""").text("Locutus")
                    }

                    div(fomantic.ui.text.container) {
                        val copyString = "The Internet has become increasingly centralized over the past 25 years, such that a handful of companies now effectively control the Internet infrastructure. This poses a threat to freedom of speech and democracy, as the public square is privately owned." +
                                "Locutus is a software platform that makes it easy to create decentralized alternatives to today's centralized tech companies. These decentralized apps will be easy to use, scalable, and secured through cryptography."
                        val copyString2 = "Trust and identity are critical in any decentralized system. By donating to its development, you can reserve your name on Freenet and in future may be granted \"trust tokens\" and other rewards for our early supporters. Names will be fully transferrable, similar to domain names today." +
                                "So please donate now to help us create a more decentralized future!"

                        p().text(copyString).classes("headerText")
                        p().text(copyString2).classes("headerText")
                        br()
                    }
                    lateinit var username : KVal<String>
                    val usernameInputStatus: KVar<InputStatus> = KVar<InputStatus>(InputStatus.None)

                    div(fomantic.ui.grid.center.aligned) {
                        form(fomantic.ui.form) {
                            div(fomantic.field) {
                                label().text("Username")
                                div(fomantic.ui.icon.input.huge) {
                                    val usernameInput = input(type = InputType.text, placeholder = "Username", attributes = mapOf("id" to "usernameInput".json))
                                    username = usernameInput.value
                                    username.addListener { old, new -> println("username $old -> $new") }
                                    username.addListener { _, new ->
                                        GlobalScope.launch {
                                            if (isUsernameValid(new)) {
                                                if (isUsernameAvailable(new)) {
                                                    usernameInputStatus.value = InputStatus.Available(
                                                        minDonationAmount = getMinimumDonationAmount(new)
                                                    )
                                                } else {
                                                    usernameInputStatus.value = InputStatus.NotAvailable
                                                }
                                            } else {
                                                usernameInputStatus.value = InputStatus.Invalid
                                            }
                                        }
                                    }

                                    render(usernameInputStatus) {
                                        when(it) {
                                            InputStatus.None -> {}
                                            is InputStatus.Available -> i().classes("ui green checkmark icon")
                                            InputStatus.NotAvailable, InputStatus.Invalid -> i().classes("ui red x icon")
                                        }
                                    }

                                }

                                render(usernameInputStatus) { inputStatus ->
                                    when(inputStatus) {
                                        InputStatus.None -> {}
                                        is InputStatus.Available -> {
                                            p().text(usernameInputStatus.map { "Username Available. Minimum Donation Amount: ${(it as InputStatus.Available).minDonationAmount}" })
                                            //p().text("Username Available. Minimum Donation Amount: \$${minimumDonationAmount.value}") // Prettify with unicode
                                            // Possibly specify minimum-donation given username length
                                        }
                                        InputStatus.NotAvailable -> {
                                            p().text("Username Not Available") // Prettify with unicode
                                        }
                                        InputStatus.Invalid -> p().text("Username invalid. May include numbers, letters, underscores, and hyphens")
                                    }
                                   /* usernameInput.on(retrieveJs = usernameInput.valueJsExpression).input { event ->
                                        username.value = event.retrieved.jsonPrimitive.content
                                        if (isUsernameValid(username.value)) {
                                            if(isUsernameAvailable(username.value)) {
                                                inputStatus.value = InputStatus.Available
                                            } else {
                                                inputStatus.value = InputStatus.NotAvailable
                                            }
                                        } else {
                                            if (username.value.isEmpty()) {
                                                inputStatus.value = InputStatus.None
                                            }
                                            inputStatus.value = InputStatus.Invalid
                                        }

                                    } */


                                }
                                render(inputStatus) { inputStatus ->
                                    when(inputStatus) {
                                        InputStatus.None -> {}
                                        InputStatus.Available -> {
                                            p().text(minimumDonationAmount.map { "Username Available. Minimum Donation Amount: " })
                                        //p().text("Username Available. Minimum Donation Amount: \$${minimumDonationAmount.value}") // Prettify with unicode
                                            // Possibly specify minimum-donation given username length
                                        }
                                        InputStatus.NotAvailable -> {
                                            p().text("Username Not Available") // Prettify with unicode
                                        }
                                        InputStatus.Invalid -> p().text("Username invalid. May include numbers, letters, underscores, and hyphens")
                                    }
                                }
                            }
                            div(fomantic.field) {
                                label().text("Email")
                                div(fomantic.ui.icon.input.huge) {
                                    val emailInput = input(type = InputType.email, placeholder = "Email", attributes = mapOf("id" to "emailInput".json))
                                    val email = KVar("")
                                    val emailStatus = KVar(EmailStatus.Empty)

                                    emailInput.on(retrieveJs = emailInput.valueJsExpression).input { event ->
                                        email.value = event.retrieved.jsonPrimitive.content
                                        if (email.value.isEmpty()) {
                                            emailStatus.value = EmailStatus.Empty
                                        } else {
                                            if (isValidEmail(email.value)) {
                                                emailStatus.value = EmailStatus.Valid
                                            } else {
                                                emailStatus.value = EmailStatus.Invalid
                                            }
                                        }
                                        render(emailStatus) {
                                            when(it) {
                                                EmailStatus.Valid -> i().classes("ui green checkmark icon")
                                                EmailStatus.Invalid -> i().classes("ui red x icon")
                                                EmailStatus.Empty ->{}
                                            }
                                        }
                                    }
                                }
                            }
                            div(fomantic.field) {
                                label().text("Donation Amount")
                                val selectedDonationAmount : KVar<String?> = KVar(null)

                                div(fomantic.ui.segment) {
                                    div(fomantic.column) {
                                        val donationAmount = KVar("")

                                        val oneButton = button(fomantic.ui.button).text("$10")

                                        oneButton.setAttribute("class", selectedDonationAmount.map {
                                            if (it == "$10") {
                                                "ui active green button"
                                            } else {
                                                "ui green button"
                                            }.json
                                        })

                                        oneButton.on.click {
                                            selectedDonationAmount.value = "10"
                                            donationAmount.value = "10"
                                        }
                                    }
                                    div(fomantic.column) {
                                        val twoButton = button(fomantic.ui.button).text("$20")
                                        twoButton.setAttribute("class", selectedDonationAmount.map {
                                            if (it == "$20") {
                                                "ui active green button"
                                            } else {
                                                "ui green button"
                                            }.json
                                        })
                                        twoButton.on.click {
                                            selectedDonationAmount.value = "20"
                                            donationAmount.value = "20"
                                        }
                                    }
                                    div(fomantic.column) {
                                        val threeButton = button(fomantic.ui.button).text("$40")
                                        threeButton.on.click {
                                            selectedDonationAmount.value = "40"
                                            donationAmount.value = "40"
                                        }
                                        threeButton.setAttribute("class", selectedDonationAmount.map {
                                            if (it == "$40") {
                                                "ui active green button"
                                            } else {
                                                "ui green button"
                                            }.json
                                        })
                                    }
                                }.classes("ui three column center aligned grid")

                                div(fomantic.ui.right.labeled.input) {
                                    val dollarSignLabel = label(fomantic.ui.label.green).text("$")
                                    dollarSignLabel.setAttribute("for", "donationInput")
                                    val donationInput = input(type = InputType.text, placeholder = "10",
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
                            button(fomantic.ui.primary.button).text("Reserve Username").on.click {
                                println(email.value)
                                if (isValidEmail(email.value)) {
                                    tempReserveName(username.value, browser.httpRequestInfo.request.headers["Referer"])
                                    val modal = div(fomantic.ui.modal) {
                                        renderCheckout("You are reserving ${username.value} for $donationAmount.00")
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
        }
    }

}

fun ElementCreator<*>.renderCheckout(confirmationText : String) {
    browser.callJsFunction("initialize()")
    p().text(confirmationText)
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

fun getMinimumDonationAmount(username: String) : Int{
    return when(username.length) {
        2 -> 200
        3 -> 150
        4 -> 100
        5 -> 80
        6 -> 60
        7 -> 45
        else -> 20
    }
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

fun isUsernameValid(username: String) : Boolean {
    val usernameRegex = "^[A-Za-z][A-Za-z0-9_\\.]{1,29}\$"
    val isValid = username.matches(usernameRegex.toRegex())
    println("Username $username is $isValid")
    return isValid
}

fun isValidEmail(email : String) : Boolean {
    val emailRegex ="""^([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5})${'$'}"""
    val isValid = email.matches(emailRegex.toRegex())
    println("email $email is $isValid")
    return isValid
}

fun isUsernameAvailable(username: String) : Boolean {
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