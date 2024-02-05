package org.freenet.website.pages.claimId

import com.stripe.model.PaymentIntent
import io.mola.galimatias.URL
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kweb.*
import kweb.components.Component
import kweb.plugins.fomanticUI.fomantic
import kweb.state.KVar
import kweb.state.render
import kweb.util.pathQueryFragment

enum class TierLevel(val tierName: String, val contributionAmount: Int) {
    BRONZE("Bronze", 10),
    SILVER("Silver", 20),
    Gold("Gold", 50)
}

fun Component.claimIdPage() {

    val blindedHash = KVar("")
    val finalCertificate = KVar("")

    browser.onMessage { msg ->
        val message = msg!!.jsonObject
        when (message["messageKey"].toString()) {
            "\"publicKey\"" -> {//TODO this string is probably being parsed twice adding an extra set of quote marks. Probably in the onMessage code somewhere in Kweb.
                blindedHash.value = message["blindedKey"].toString()
                finalCertificate.value = message["certificate"].toString()

            }
        }
    }

    val stripeData = browser.gurl.value.query()?.let {
        val params = it.split("&")
        val map = params.associate {
            val (key, value) = it.split("=")
            key to value
        }
        map

    }

    if (stripeData != null) {//this branch means there were extra query parameters meaning we are actually on a succesful payment page
        //TODO this branch should be split into two, one for success, and one for stripe failure.
        val paymentIntent = PaymentIntent.retrieve(stripeData["payment_intent"])
        //Stripes paymentIntent class should allow storing and loading any necessary data about our user.
        div(mapOf("id" to JsonPrimitive("qrCodeBox")))
        browser.callJsFunction("createQRCode();")
        //TODO this is possibly the best place to call the client side function blindUserKey(), and do the server side sign
        //todo pull the necessary parameters from the paymentIntent, or wherever else, and supply them to createQRCode().
    } else {//this branch means we are loading the initial claim id page.
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

        val displayCheckout = kvar(false)
        var tierLevel = TierLevel.BRONZE

        div() {
            enumValues<TierLevel>().forEach { currentTier ->
                p() {
                    button { button ->
                        button.classes("button", "is-medium-green", "generate-button")
                        button.on.click {
                            tierLevel = currentTier
                            displayCheckout.value = true
                        }
                        span { span ->
                            span.classes("icon")
                            i().classes("fas", "fa-key")
                        }
                        span().innerHTML("&nbsp;")
                        span().text("${currentTier.tierName}: $${currentTier.contributionAmount}")
                    }
                }.classes("control")
            }

        }.classes("field is-grouped")

        render(displayCheckout) { displayCheckout ->
            if (displayCheckout) {
                renderCheckout("You have started the donation process",
                    tierLevel.tierName, tierLevel.contributionAmount)
            }
        }
    }

}

//the blinded key from the client
fun signBlindedKey(clientMessage: String) : String {
    //val rsaSigner = RSASigner()
    //RSASigner.FreenetKey.initialize()
    //return rsaSigner.RSASign(clientMessage)
    return ""
}

fun ElementCreator<*>.renderCheckout(confirmationText : String, donationTier: String, donationAmount: Int) {
    browser.callJsFunction("initialize({},{})", JsonPrimitive(donationTier), JsonPrimitive(donationAmount))
    lateinit var paymentForm : FormElement
    div() {
        div() {
            paymentForm = form(mapOf("id" to JsonPrimitive("payment-form"))) {
                div(mapOf("id" to JsonPrimitive("payment-element")))
                button(mapOf("id" to JsonPrimitive("submit"))).text("Pay Now")
            }
            div(mapOf("id" to JsonPrimitive("payment-message"))) {

            }.classes("hidden")
        }.classes("modal-content")
    }.classes("modal is-active")

}
