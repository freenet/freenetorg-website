package org.freenet

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.stripe.model.Customer
import com.stripe.model.PaymentIntent
import homePage
import io.ktor.server.plugins.*
import io.ktor.util.date.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
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

//TODO Google Authentication can fail in the first few seconds of a pod existing. Need to add check to make sure this succeeds, and call it again on fail
val firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
    .setProjectId("freenet-353920")
    .setCredentials(GoogleCredentials.getApplicationDefault())
    .build()
val db: Firestore = firestoreOptions.service
val presetDonationValues = KVar(arrayOf("10", "20", "40"))

sealed class InputStatus {
    object None : InputStatus()
    class Available(val minDonationAmount : Long) : InputStatus()
    object NotAvailable : InputStatus()
    object Invalid : InputStatus()
}

enum class EmailStatus {
    Empty, Valid, Invalid
}

fun main() {

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
            route {

                path("/success") {
                    val payIntent = PaymentIntent.retrieve(browser.httpRequestInfo.request.call.parameters["payment_intent"])
                    val customerEmail = saveCustomer(payIntent.charges.data[0].customer)
                    saveStripePaymentDetails(payIntent.toString(), customerEmail, payIntent.metadata["username"]!!, payIntent.amount, payIntent.charges.data[0].id)
                    div(fomantic.ui.container.center.aligned) {
                        val successCopy = "Thank you for confirming the username ${payIntent.metadata["username"]!!}"
                        val successCopy2 = "You will receive payment receipt at $customerEmail"
                        val successCopy3 = "Follow Freenet on Twitter"
                        p().text(successCopy)
                        p().text(successCopy2)
                        p().text(successCopy3)
                    }
                }

                path("/cancel") {
                    h1().text("Payment cancelled")
                }

                path("") {
                    homePage(db)
                }

                path("/names") {

                    val ipAddress = browser.httpRequestInfo.request.call.request.origin.remoteHost
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
                    val usernameInputStatus: KVar<InputStatus> = KVar(InputStatus.None)
                    val emailInputStatus : KVar<EmailStatus> = KVar(EmailStatus.Empty)
                    val minimumDonationAmount : KVar<Long> = KVar(0L)
                    lateinit var username : KVal<String>
                    lateinit var email : KVal<String>
                    var selectedDonationAmount : KVar<String> = KVar("")
                    lateinit var donationInput : InputElement

                    div(fomantic.ui.grid.center.aligned) {
                        form(fomantic.ui.form) {
                            div(fomantic.field) {
                                label().text("Username")
                                div(fomantic.ui.icon.input.huge) {
                                    val usernameInput = input(type = InputType.text, placeholder = "Username", attributes = mapOf("id" to "usernameInput".json))
                                    username = usernameInput.value
                                    username.addListener { _, new ->
                                        GlobalScope.launch {
                                            if (isUsernameValid(new)) {
                                                if (isUsernameAvailable(new)) {
                                                    usernameInputStatus.value = InputStatus.Available(
                                                        minDonationAmount = getMinimumDonationAmount(new)
                                                    )
                                                    minimumDonationAmount.value = getMinimumDonationAmount(new)
                                                    presetDonationValues.value = arrayOf(minimumDonationAmount.value.toString(), "${minimumDonationAmount.value * 2}", "${minimumDonationAmount.value * 3}")
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

                                val usernameInvalidString = "Username invalid. May include numbers, letters, underscores, and hyphens"
                                render(usernameInputStatus) { inputStatus ->
                                    when(inputStatus) {
                                        InputStatus.None -> {}
                                        InputStatus.NotAvailable -> {
                                            p().text("Username Not Available")
                                        }
                                        InputStatus.Invalid -> p().text(usernameInvalidString)
                                        is InputStatus.Available -> {
                                            val usernameAvailableString = "Username Available. Minimum Donation Amount: ${inputStatus.minDonationAmount}"
                                            p().text(usernameAvailableString )
                                        }
                                    }
                                }

                            }
                            div(fomantic.field) {
                                label().text("Email")
                                div(fomantic.ui.icon.input.huge) {
                                    val emailInput = input(type = InputType.email, placeholder = "Email", attributes = mapOf("id" to "emailInput".json))
                                    email = emailInput.value
                                    email.addListener { _, new ->
                                        GlobalScope.launch {
                                            if (new.isEmpty()) {
                                                emailInputStatus.value = EmailStatus.Empty
                                            } else {
                                                if (isValidEmail(new)) {
                                                    emailInputStatus.value = EmailStatus.Valid
                                                } else {
                                                    emailInputStatus.value = EmailStatus.Invalid
                                                }
                                            }
                                        }
                                    }
                                    render(emailInputStatus) {
                                        when(it) {
                                            EmailStatus.Valid -> i().classes("ui green checkmark icon")
                                            EmailStatus.Invalid -> i().classes("ui red x icon")
                                            EmailStatus.Empty ->{}
                                        }
                                    }

                                }
                            }

                            div(fomantic.field) {
                                div(fomantic.ui.grouped.fields) {
                                    label().text("Pay What You Can")
                                    div(fomantic.ui.center.aligned.stackable.grid) {
                                        render(presetDonationValues) {
                                            for (preset in presetDonationValues.value) {
                                                div(fomantic.field.two.wide.column) {
                                                    div(fomantic.ui.radio.checkbox) {
                                                        val radioButton = input().setAttribute("type", "radio")
                                                            .setAttribute("name", "donationPresetRadio")
                                                            .setAttribute("value", preset)
                                                            .setAttribute("tabindex", "0")
                                                        radioButton.on.click {
                                                            selectedDonationAmount.value = preset
                                                        }
                                                        label().text("\$$preset")
                                                    }
                                                }
                                            }
                                        }
                                        div(fomantic.field.six.wide.column.left.aligned) {
                                            div(fomantic.ui.radio.checkbox) {
                                                val customDonationField = input(attributes = mapOf("id" to "donationRadioCustomField".json,
                                                "class" to "ui input".json)).setAttribute("type", "radio")
                                                    .setAttribute("name", "donationPresetRadio")
                                                    .setAttribute("tabindex", "0")
                                                customDonationField.on.click {
                                                    println("selectedDonationAmount on customClick: ${selectedDonationAmount.value}")
                                                    selectedDonationAmount = donationInput.value
                                                    println("selectedDonationAmount on customClick again: ${selectedDonationAmount.value}")
                                                }
                                                label {
                                                    donationInput = input(type= InputType.text, placeholder = "Custom",
                                                        attributes = mapOf("id" to "donationInput".json))
                                                    donationInput.setAttribute("class", "fluid").setAttribute("width", "6em")
                                                    selectedDonationAmount = donationInput.value
                                                    donationInput.on.input {
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            button(fomantic.ui.primary.button).text("Reserve Username").on.click {
                                when(usernameInputStatus.value) {
                                    is InputStatus.Available -> { it
                                        if(emailInputStatus.value == EmailStatus.Valid) {

                                            tempReserveName(username.value, ipAddress, browser.httpRequestInfo.request.headers["Referer"])
                                            val modal = div(fomantic.ui.modal) {
                                                val reservationConfirmationString = "You are reserving the username \"${username.value}\" for \$${selectedDonationAmount.value}.00"
                                                renderCheckout(reservationConfirmationString)
                                            }
                                            browser.callJsFunction("$(\'#\' + {}).modal(\'show\');", modal.id.json)
                                        } else {
                                        }
                                    }
                                    InputStatus.NotAvailable -> {
                                    }
                                    InputStatus.Invalid, InputStatus.None -> {
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

/*
fun isDonationAmountHighEnough(userDonationInput: String, minDonationAmount: Double) : Boolean{
    return userDonationInput.toDouble() >= minDonationAmount
}
*/

fun ElementCreator<*>.renderCheckout(confirmationText : String) {
    browser.callJsFunction("initialize()")
    lateinit var paymentForm : FormElement
    div(fomantic.ui.segment.center.aligned) {
        p().text(confirmationText)
    }
    div(fomantic.ui.grid.center.aligned) {
        paymentForm = form(mapOf("id" to JsonPrimitive("payment-form"))) {
            div(mapOf("id" to JsonPrimitive("payment-element")))
            button(mapOf("id" to JsonPrimitive("submit"))) {
                div(mapOf("id" to JsonPrimitive("spinner"))).classes("spinner hidden")
                span(mapOf("id" to JsonPrimitive("button-text"))).text("Pay Now")
            }
            div(mapOf("id" to JsonPrimitive("payment-message"))).classes("hidden")
        }
    }
    div(fomantic.ui.container.center.aligned) {
        val secureTransactionNoticeString = "Some text explaining that this is a secure transaction through stripe and not to worry about their credit card details"
        p().text(secureTransactionNoticeString)
    }
    div(fomantic.ui.actions) {
        val cancelButton = button(fomantic.ui.button).text("cancel").on.click {
            browser.callJsFunction("$(\'.ui.modal\').modal(\'close\');")
            paymentForm.deleteIfExists()
        }
        cancelButton.classes("ui cancel button")
    }
}

fun getMinimumDonationAmount(username: String) : Long{
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


fun tempReserveName(username: String,  ipAddress: String, referer: String? = null) {
    val docRef = db.collection(usernameTableName).document(username)
    val data = HashMap<String, Any>()

    data["lowercaseUsername"] = username.lowercase()
    data["timeReserved"] = getTimeMillis()
    data["ipAddress"] = ""
    referer?.let {
        data["referer"] = it
    }
    docRef.set(data)
}

fun isUsernameValid(username: String) : Boolean {
    //allows a 2-30 character alphanumeric username that may contain an underscore or period
    val usernameRegex = "^[A-Za-z][A-Za-z0-9_.]{1,29}\$"
    val isValid = username.matches(usernameRegex.toRegex())
    return isValid
}

fun isValidEmail(email : String) : Boolean {
    val emailRegex ="""^([a-zA-Z0-9_\-.]+)@([a-zA-Z0-9 \-.]+)\.([a-zA-Z]{2,5})${'$'}"""
    val isValid = email.matches(emailRegex.toRegex())
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

fun showToast(webBrowser: WebBrowser, message: String) {
    webBrowser.callJsFunction(""" 
        Toastify({
            text: {},
            duration: 5000,
            close: false,
            gravity: "bottom",
            position: "center",
            stopOnFocus: false,
            style: {
                color: "#ff0033",
                background: "white"
            }
        }).showToast();
    """.trimIndent(), message.json)
}