package org.freenet.website.pages.donate

import kweb.*
import kweb.components.Component
import kweb.state.KVar
import kweb.state.render
import kweb.util.json


fun Component.donatePage() {
    h2().classes("title", "is-small").text("Donate to Freenet")
    p().text(
        """
            Founded in 2001, Freenet is a 501c3 non-profit organization dedicated to the development and 
            propagation of technologies for open and democratic information distribution over the Internet.
            We advocate for unrestricted exchange of intellectual, scientific, literary, social, artistic, 
            creative, human rights, and cultural expressions, free from interference by state, private, 
            or special interests.
        """.trimIndent()
    )

    h2().classes("title", "is-small").text("Donate via PayPal or Credit Card")
    a { a ->
        a["href"] = "https://www.paypal.com/donate?hosted_button_id=EQ9E7DPHB6ETY"
        img { img ->
            img["src"] = "https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif"
            img["alt"] = "Donate with PayPal"
        }
    }

    h2().classes("title", "is-small").text("Donate via Cryptocurrency")
    p().innerHTML(
        """
            Freenet is <b>not</b> a cryptocurrency, but we do accept cryptocurrency donations. For large donations (over $5,000) please contact us before sending. For smaller donations, please use the following wallets:
        """.trimIndent()
    )

    table {
        thead {
            tr {
                th().text("Cryptocurrency")
                th().text("Address")
            }
        }
        tbody {
            tr {
                td().text("Bitcoin")
                td {
                    input { input ->
                        input.setAttributes("readonly" to true.json)
                        input.setAttributes("value" to "3M3fbA7RDYdvYeaoR69cDCtVJqEodo9vth".json)
                        input.setAttributes("onclick" to "this.select();".json)
                    }
                }
            }
            tr {
                td().text("Zcash")
                td {
                    input { input ->
                        input.setAttributes("readonly" to true.json)
                        input.setAttributes("value" to "t1VHw1PHgzvMqEEd31ZBt3Vyy2UrG4J8utB".json)
                        input.setAttributes("onclick" to "this.select();".json)
                    }
                }
            }
            tr {
                td().text("Ethereum")
                td {
                    input { input ->
                        input.setAttributes("readonly" to true.json)
                        input.setAttributes("value" to "0x79158A5Dbd9C0737CB27411817BD2759f5b9a9Ae".json)
                        input.setAttributes("onclick" to "this.select();".json)
                    }
                }
            }

        }
    }
}

private fun Component.renderPage(html: KVar<String?>) {
    render(html) { html ->
        if (html != null) {
            section().classes("section").innerHTML(html)
        }
    }
}