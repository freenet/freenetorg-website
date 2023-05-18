package org.freenet.website.pages.claimId

import kweb.*
import kweb.components.Component

fun Component.claimIdPage() {
    h1().text("Claim your Freenet ID")

    step1()

    step2()

}

private fun Component.step1() {
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
                browser.callJsFunction("beginGeneration();")
            }
            span { span ->
                span.classes("icon")
                i().classes("fas", "fa-key")
            }
            span().innerHTML("&nbsp;")
            span().text("Generate Key")
        }
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
    }
}
