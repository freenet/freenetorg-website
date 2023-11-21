package org.freenet.website.pages.claimId

import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kweb.*
import kweb.components.Component
import kweb.plugins.fomanticUI.fomantic
import kweb.state.KVar
import kweb.state.render

fun Component.claimIdPage() {


    val blindedHash = KVar("")
    val finalCertificate = KVar("")


    browser.onMessage { msg ->
        val message = msg!!.jsonObject
        when (message["messageKey"].toString()) {
            "\"publicKey\"" -> {
                blindedHash.value = message["blindedKey"].toString()
                finalCertificate.value = message["certificate"].toString()

            }
        }
    }

    h1().text("Claim your Freenet ID")

    section { section ->
        section.classes("section")

        h1 { h1 ->
            h1.classes("title")
            h1.innerHTML("Claim your Freenet ID")
        }
        h2 { h2 ->
            h2.classes("subtitle")
            h2.innerHTML("Generate your Master Key")
        }
        p().innerHTML(
            """
                Secure a Freenet Patron Certificate to support Freenet's mission of enabling free and open communication on the internet. This certificate reflects your early support and belief in our network's potential. In the future, it could also serve as a hallmark of trust and authenticity within the Freenet community, similar to a proof of membership.
        """.trimIndent()
        )
        p().innerHTML(
            """
                The process is straightforward and designed with your anonymity in mind. When you click the button below, a unique digital key pair will be generated in your browser. Following a blind signature protocol, this key pair will be securely signed by Freenet in such a way that our servers cannot tie the payment transaction to your public/private keypair, ensuring your anonymity. After a simple payment to help sustain Freenet, you'll receive your Freenet Patron Certificate, which you can download or copy directly.
            """.trimIndent()
        )

        p().text(blindedHash)

        button { button ->
            button.classes("button", "is-medium-green", "generate-button")
            button.on.click {
                browser.callJsFunction("generateUserKey();")
            }
            span { span ->
                span.classes("icon")
                i().classes("fas", "fa-key")
            }
            span().innerHTML("&nbsp;")
            span().text("Create Certificate")
        }
    }

    //step1(blindedHash)

    button { button ->
        button.classes("button", "is-medium-green", "generate-button")
        button.on.click {
            //TODO renderCheckout form and display it when necessary using a Kvar
            button.creator!!.parentCreator!!.renderCheckout("You have started the donation process")
            //renderCheckout("You have started the donation process")
            //TODO JS code needs to add is-active to this modal to display it.
            /*div { div ->
                div.classes("modal")
                div { divContent ->
                    divContent.classes("modal-content")
                        renderCheckout("Hola")
                }
            }*/
        }
        span { span ->
            span.classes("icon")
            i().classes("fas", "fa-key")
        }
        span().innerHTML("&nbsp;")
        span().text("Pay via Stripe")
    }

}

private fun stripeStuff() : String {
    //TODO implement Stripe checkout form
    return "Payment completed"
}

//the blinded key from the client
fun signBlindedKey(clientMessage: String) : String {
    val rsaSigner = RSASigner()
    RSASigner.FreenetKey.initialize()
    return rsaSigner.RSASign(clientMessage)
}

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
