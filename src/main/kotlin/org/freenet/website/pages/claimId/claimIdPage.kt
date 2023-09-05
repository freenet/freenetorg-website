package org.freenet.website.pages.claimId

import kotlinx.serialization.json.jsonObject
import kweb.*
import kweb.components.Component
import kweb.state.KVar

fun Component.claimIdPage() {


    val blindedHash = KVar("")
    val unblinded = KVar("")


    browser.onMessage { msg ->
        val message = msg!!.jsonObject
        when (message["messageKey"].toString()) {
            "\"publicKey\"" -> {
                blindedHash.value = message["blindedKey"].toString()
                unblinded.value = message["unblindedKey"].toString()

            }
        }
    }

    h1().text("Claim your Freenet ID")

    step1(blindedHash, unblinded)

    step2()

    //step3()

}

private fun Component.step1(blindedHash : KVar<String>, unblinded: KVar<String>) {
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


        p().text(blindedHash)
        p().text(unblinded)
    }
}

private fun Component.step2() {
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
            p().text(stripeStuff())
            step3()
        }
    }
}

private fun stripeStuff() : String {
    return "Payment completed"
}

fun signBlindedKey() : String {
    return "signedKey"
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
