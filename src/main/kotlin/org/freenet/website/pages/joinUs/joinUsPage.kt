package org.freenet.website.pages.joinUs

import kweb.*
import kweb.components.Component
import org.freenet.website.util.workInProgress

/**
 * This tab could focus on community engagement and provide information
 * on how individuals and organizations can get involved with Freenet.
 * It could include links to the Freenet User Manual, the Matrix chat,
 * and the Freenet Classic community. It could also provide
 * information on how to donate to Freenet and how grant organizations
 * can support the project.
 */
fun Component.joinUsPage() {
    donationCryptoWallets()

    workInProgress()
}

private fun Component.donationCryptoWallets() {
    h3().text("Support Our Work")

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