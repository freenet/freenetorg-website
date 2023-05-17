package org.freenet.website.pages.claimId

import kweb.*
import kweb.components.Component
import kweb.util.json

fun Component.claimIdPage() {
    h1().text("Claim your Freenet ID")

    section { section ->
        section.classes("section")

        h1 { h1 ->
            h1.classes("title")
            h1.innerHTML("Step 1")
        }
        h2 { h2 ->
            h2.classes("subtitle")
            h2.innerHTML("Generate master key")
        }
        p().innerHTML(
            """
            This will securely create a P-256 Elliptic Curve keypair in your browser, it will
            not be revealed to our web server. In a later step you'll be able to export it and store 
            it in a safe place.
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

    div().classes("qrcode")
}