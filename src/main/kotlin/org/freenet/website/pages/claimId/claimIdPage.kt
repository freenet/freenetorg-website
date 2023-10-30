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
    val unblinded = KVar("") // <--- Not right, the server can never see the unblinded key (ian)
    val finalCertificate = KVar("")


    browser.onMessage { msg ->
        val message = msg!!.jsonObject
        when (message["messageKey"].toString()) {
            "\"publicKey\"" -> {
                blindedHash.value = message["blindedKey"].toString()
                unblinded.value = message["unblindedKey"].toString()
                finalCertificate.value = message["certificate"].toString()

            }
        }
    }

    h1().text("Claim your Freenet ID")

    step1(blindedHash)

    button(fomantic.ui.primary.button).text("Hola").on.click {
        //step2(blindedHash)
        renderCheckout("hola")


        //TODO JS code needs to add is-active to this modal to display it.
        /*div { div ->
            div.classes("modal")
            div { divContent ->
                divContent.classes("modal-content")
                    renderCheckout("Hola")
            }
        }*/

    }

    //step2()

    //step3()

}

private fun Component.step1(blindedHash : KVar<String>) {
    section { section ->
        section.classes("section")

        h1 { h1 ->
            h1.classes("title")
            h1.innerHTML("Step 1")
        }
        h2 { h2 ->
            h2.classes("subtitle")
            h2.innerHTML("Generate your Master Key")
        }
        p().innerHTML(
            """
                First, let's create your unique cryptographic key pair. This stays with you, secure in your 
                browser, and never touches our servers. Click the 'Generate' button when you're ready. 
                We will provide a secure way to store this key pair in a later step.
        """.trimIndent()
        )

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
            span().text("Generate Key")
        }


        //sample line to display blinded hash when received from client
        p().text(blindedHash)
    }
}

private fun Component.step2(blindedHash : KVar<String>) {
    section { section ->
        section.classes("section")

        h1 { h1 ->
            h1.classes("title")
            h1.innerHTML("Step 2")
        }
        h2 { h2 ->
            h2.classes("subtitle")
            h2.innerHTML("Contribute via Stripe")
        }
        p().innerHTML(
            """
                Now that you have your keys, it's time to give your voice some weight in our ecosystem. This is done through a meaningful contribution.
                This donation not only supports our mission, but it also lends credibility to your anonymous identity.
                Your contribution is a signal of your commitment to good behavior on our platform.
                The next step will unlock upon completion of your donation.
        """.trimIndent()
        )

        val stripeButton = button { button ->
            button.classes("button", "is-medium-green", "generate-button")
            span { span ->
                span.classes("icon")
                i().classes("fas", "fa-key")
            }
            span().innerHTML("&nbsp;")
            span().text("Generate Key")
        }
        stripeButton.on.click {
            p().text(signBlindedKey(blindedHash.value))
            /*renderCheckout("Hola")
            p().text(stripeStuff())
            step3()*/
        }
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

private fun Component.step3() {
    section { section ->
        section.classes("section")

        h1 { h1 ->
            h1.classes("title")
            h1.innerHTML("Step 2")
        }
        h2 { h2 ->
            h2.classes("subtitle")
            h2.innerHTML("Certify your Master Key")
        }
        p().innerHTML(
            """
                First, let's create your unique cryptographic key pair. This stays with you, secure in your 
                browser, and never touches our servers. Click the 'Generate' button when you're ready. 
                We will provide a secure way to store this key pair in a later step.
        """.trimIndent()
        )

        button { button ->
            button.classes("button", "is-medium-green", "generate-button")
            button.on.click {
                browser.callJsFunction("beginGeneration();")
            }
            span { span ->
                span.classes("icon")
                i().classes("fas", "fa-key")
            }
            span().innerHTML("&nbsp;")
            span().text("Generate Key")


        }


        browser.onMessage {
            p().text("Hola")
        }


    }
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
