package org.freenet.website.pages

import kweb.*
import kweb.components.Component
import org.freenet.website.util.iconButton
import java.util.*

/**
 * The main landing page, which provides an overview of Freenet, its mission,
 * and its features.
 */
fun Component.homePage() {

    section { section ->
        section.classes("section")
        h2().classes("subtitle", "is-normal").text("Declare your digital independence")
        intro()
        p()
        learnMoreLinks()
    }

    section { section ->
        section.classes("section")
        latestNews()
    }
}

private fun Component.learnMoreLinks() {
    div { div ->
        div.classes("buttons")
        // Video Introduction
        iconButton(
            html = "Watch Ian's Talk",
            href = "https://youtu.be/yBtyNIqZios?si=vnFje0OQFYkni7NZ",
            icon = arrayOf("fas", "fa-video"),
            buttonClasses = arrayOf("button", "is-medium-blue")
        )
        // User Manual
        iconButton(
            html = "Read User Manual",
            href = "https://docs.freenet.org/",
            icon = arrayOf("fas", "fa-book"),
            buttonClasses = arrayOf("button", "is-medium-teal")
        )
        // Community Chat
        iconButton(
            html = "Chat on Matrix",
            href = "https://matrix.to/#/#freenet-locutus:matrix.org",
            icon = arrayOf("fas", "fa-comments"),
            buttonClasses = arrayOf("button", "is-medium-purple")
        )
        // GitHub Repository
        iconButton(
            html = "Visit GitHub",
            href = "https://github.com/freenet/freenet-core",
            icon = arrayOf("fab", "fa-github"),
            buttonClasses = arrayOf("button", "is-medium-orange")
        )
    }
}

private fun Component.socialLinks() {
    h3().text("Connect With Us")

    div { div ->
        div.classes("buttons")
        iconButton("Twitter", "https://twitter.com/freenetorg", arrayOf("fab", "fa-twitter"), buttonClasses = arrayOf("button", "is-medium-blue"))
        iconButton("Matrix", "https://matrix.to/#/#freenet-locutus:matrix.org", arrayOf("fas", "fa-comment"), buttonClasses = arrayOf("button", "is-medium-purple"))
        iconButton("Reddit", "https://www.reddit.com/r/freenet/", arrayOf("fab", "fa-reddit"), buttonClasses = arrayOf("button", "is-medium-orange"))
    }
}

private fun Component.donationCryptoWallets() {
    h3().text("Support Our Work")

    h4().text("Donate via PayPal or Credit Card")

    div().classes("content").innerHTML("""
<form action="https://www.paypal.com/donate" method="post" target="_top">
<input type="hidden" name="hosted_button_id" value="EQ9E7DPHB6ETY" />
<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif" border="0" name="submit" title="PayPal - The safer, easier way to pay online!" alt="Donate with PayPal button" />
<img alt="" border="0" src="https://www.paypal.com/en_US/i/scr/pixel.gif" width="1" height="1" />
</form>
    """.trimIndent())

    h4().text("Donate via Cryptocurrency")

    p().innerHTML("Freenet is <b>not</b> a cryptocurrency, but we do accept cryptocurrency donations." +
            " For donations over $5,000 please contact us before sending. For smaller donations, " +
            "please use the following wallets:")

    val donationWallets = listOf(
        Pair("Bitcoin", "3M3fbA7RDYdvYeaoR69cDCtVJqEodo9vth"),
        Pair("Zcash", "t1VHw1PHgzvMqEEd31ZBt3Vyy2UrG4J8utB"),
        Pair("Ethereum", "0x79158A5Dbd9C0737CB27411817BD2759f5b9a9Ae"),
    )

    table { table ->
        table.classes("table", "is-hoverable")
        tbody {
            for (wallet in donationWallets) {
                tr {
                    td().text(wallet.first)
                    td { td ->
                        td.classes("has-text-centered")
                        div { div ->
                            div.classes("control", "has-icons-right")
                            input(type = InputType.text)
                                .classes("input", "is-fullwidth")
                                .set("readonly", "true")
                                .set("value", wallet.second)
                                .set("onclick", "this.select()")
                            span { span ->
                                span.classes("icon", "is-normal", "is-right")
                                i().classes("fas", "fa-copy")
                            }
                        }
                    }
                }
            }
        }
    }
}