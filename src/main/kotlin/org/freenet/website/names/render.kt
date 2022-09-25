package org.freenet.website.names

import com.stripe.model.PaymentIntent
import io.ktor.server.plugins.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kweb.*
import kweb.plugins.fomanticUI.fomantic
import kweb.state.KVal
import kweb.state.KVar
import kweb.state.render
import kweb.util.json
import org.freenet.website.*

fun ElementCreator<*>.renderNamesLanding() {
    val ipAddress = browser.httpRequestInfo.request.call.request.origin.remoteHost
    div(fomantic.ui.text.center.aligned.container) {
        h1(fomantic.ui).setAttribute("style", """font-size: 4em;""").text("Locutus")
    }

    div(fomantic.ui.text.container) {

        p().text(
            """The Internet has become increasingly centralized over the past 25 years, such that a handful of 
                |companies now effectively control the Internet infrastructure. This poses a threat to freedom of 
                |speech and democracy, as the public square is privately owned. Locutus is a software platform that 
                |makes it easy to create decentralized alternatives to today's centralized tech companies. These 
                |decentralized apps will be easy to use, scalable, and secured through cryptography.""".trimMargin()
        ).classes("headerText")
        p().text(
            """Trust and identity are critical in any decentralized system. By donating to its development, 
                |you can reserve your name on Freenet and in future may be granted "trust tokens" and other 
                |rewards for our early supporters. Names will be fully transferrable, similar to domain names 
                |today. So please donate now to help us create a more decentralized future!""".trimMargin()
        ).classes("headerText")
        br()
    }
    val usernameInputStatus: KVar<InputStatus> = KVar(InputStatus.None)
    val emailInputStatus: KVar<EmailStatus> = KVar(EmailStatus.Empty)
    val minimumDonationAmount: KVar<Long> = KVar(0L)
    lateinit var username: KVal<String>
    lateinit var email: KVal<String>
    var selectedDonationAmount: KVar<String> = KVar("")
    lateinit var donationInput: InputElement

    div(fomantic.ui.grid.center.aligned) {
        form(fomantic.ui.form) {
            div(fomantic.field) {
                label().text("Username")
                div(fomantic.ui.icon.input.huge) {
                    val usernameInput = input(
                        type = InputType.text,
                        placeholder = "Username",
                        attributes = mapOf("id" to "usernameInput".json)
                    )
                    username = usernameInput.value
                    username.addListener { _, new ->
                        GlobalScope.launch {
                            if (isUsernameValid(new)) {
                                if (isUsernameAvailable(new)) {
                                    usernameInputStatus.value = InputStatus.Available(
                                        minDonationAmount = getMinimumDonationAmount(new)
                                    )
                                    minimumDonationAmount.value = getMinimumDonationAmount(new)
                                    presetDonationValues.value = arrayOf(
                                        minimumDonationAmount.value.toString(),
                                        "${minimumDonationAmount.value * 2}",
                                        "${minimumDonationAmount.value * 3}"
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
                        when (it) {
                            InputStatus.None -> {}
                            is InputStatus.Available -> i().classes("ui green checkmark icon")
                            InputStatus.NotAvailable, InputStatus.Invalid -> i().classes("ui red x icon")
                        }
                    }

                }

                val usernameInvalidString = "Username invalid. May include numbers, letters, underscores, and hyphens"
                render(usernameInputStatus) { inputStatus ->
                    when (inputStatus) {
                        InputStatus.None -> {}
                        InputStatus.NotAvailable -> {
                            p().text("Username Not Available")
                        }

                        InputStatus.Invalid -> p().text(usernameInvalidString)
                        is InputStatus.Available -> {
                            val usernameAvailableString =
                                "Username Available. Minimum Donation Amount: ${inputStatus.minDonationAmount}"
                            p().text(usernameAvailableString)
                        }
                    }
                }

            }
            div(fomantic.field) {
                label().text("Email")
                div(fomantic.ui.icon.input.huge) {
                    val emailInput = input(
                        type = InputType.email,
                        placeholder = "Email",
                        attributes = mapOf("id" to "emailInput".json)
                    )
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
                        when (it) {
                            EmailStatus.Valid -> i().classes("ui green checkmark icon")
                            EmailStatus.Invalid -> i().classes("ui red x icon")
                            EmailStatus.Empty -> {}
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
                                val customDonationField = input(
                                    attributes = mapOf(
                                        "id" to "donationRadioCustomField".json,
                                        "class" to "ui input".json
                                    )
                                ).setAttribute("type", "radio")
                                    .setAttribute("name", "donationPresetRadio")
                                    .setAttribute("tabindex", "0")
                                customDonationField.on.click {
                                    println("selectedDonationAmount on customClick: ${selectedDonationAmount.value}")
                                    selectedDonationAmount = donationInput.value
                                    println("selectedDonationAmount on customClick again: ${selectedDonationAmount.value}")
                                }
                                label {
                                    donationInput = input(
                                        type = InputType.text, placeholder = "Custom",
                                        attributes = mapOf("id" to "donationInput".json)
                                    )
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
                when (usernameInputStatus.value) {
                    is InputStatus.Available -> {
                        it
                        if (emailInputStatus.value == EmailStatus.Valid) {

                            tempReserveName(
                                username.value,
                                ipAddress,
                                browser.httpRequestInfo.request.headers["Referer"]
                            )
                            val modal = div(fomantic.ui.modal) {
                                val reservationConfirmationString =
                                    "You are reserving the username \"${username.value}\" for \$${selectedDonationAmount.value}.00"
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


fun ElementCreator<*>.renderSuccess() {
    val payIntent = PaymentIntent.retrieve(browser.httpRequestInfo.request.call.parameters["payment_intent"])
    val customerEmail = saveCustomer(payIntent.charges.data[0].customer)
    saveStripePaymentDetails(
        payIntent.toString(),
        customerEmail,
        payIntent.metadata["username"]!!,
        payIntent.amount,
        payIntent.charges.data[0].id
    )
    div(fomantic.ui.container.center.aligned) {
        p().text("Thank you for confirming the username ${payIntent.metadata["username"]!!}")
        p().text("You will receive payment receipt at $customerEmail")
        p().text("Follow Freenet on Twitter")
    }
}

fun ElementCreator<*>.renderCancel() {
    h1().text("Payment cancelled")
}
