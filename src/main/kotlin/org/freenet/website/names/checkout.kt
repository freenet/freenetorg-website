package org.freenet.website.names

import kotlinx.serialization.json.JsonPrimitive
import kweb.*
import kweb.plugins.fomanticUI.fomantic

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