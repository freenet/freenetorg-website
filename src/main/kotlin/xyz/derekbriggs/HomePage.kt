import kweb.*
import kweb.plugins.fomanticUI.fomantic

fun ElementCreator<*>.homePage() {
    div(fomantic.ui.text.center.aligned.container) {
        div(fomantic.ui.text.left.aligned.container) {
            div() {
                h1(fomantic.ui.text).text("Freenet - Staging")
                h2(fomantic.ui.text).text("Declare your digital independence")
            }

            br()

            h2(fomantic.ui.text).text("Freenet")
            p(fomantic.ui.text).text("23 years ago we created the original Freenet, the first distributed, decentralized peer-to-peer network. It pioneered " +
                    "technologies like cryptographic contracts and small-world networks, and is still under active development.\n" +
                    "\n")
            p(fomantic.ui.text).text("Today we’re building Locutus, which will make it easy for developers to create and deploy decentralized alternatives to today’s centralized tech companies. " +
                    "These decentralized apps will be easy to use, scalable, and secured through cryptography.")

            h2(fomantic.ui.text).text("Locutus News")
            div(fomantic.ui.bulleted.list) {
                div(fomantic.ui.item).text("5th September, 2022: Ian gave an interview (YouTube) to Louis Rossmann about Locutus")
                div(fomantic.ui.item).text("16th July, 2022: Ian gave a talk (YouTube) on Decentralized Reputation and Trust in Locutus")
                div(fomantic.ui.item).text("7th July, 2022: Ian gave an introductory talk (Youtube, Vimeo) on Locutus")
                div(fomantic.ui.item).text("6th July, 2022: Locutus makes the front page of the r/cryptocurrency subreddit")
                div(fomantic.ui.item).text("18th April, 2022: Locutus makes the front page of the r/programming subreddit")
                div(fomantic.ui.item).text("10th April, 2022: Locutus makes the front page of Hacker News")
                div(fomantic.ui.item).text("27th March, 2022: Locutus makes the front page of the r/privacy subreddit")
            }

            h2(fomantic.ui.text).text("Software")
            div(fomantic.ui.bulleted.list) {
                div(fomantic.ui.item).text("Fred - The original Freenet software, first released in March 2000 and continuously developed since")
                div(fomantic.ui.item).text("Locutus - A decentralized application layer for the Internet, first release expected September 2022")
            }

            h2(fomantic.ui.text).text("Community")
            div(fomantic.ui.bulleted.list) {
                div(fomantic.ui.item).text("r/freenet on Reddit")
                div(fomantic.ui.item) {
                    p().text("Locutus")
                    div(fomantic.ui.bulleted.list) {
                        div(fomantic.ui.item).text("Follow @freenetorg on Twitter for updates")
                        div(fomantic.ui.item).text("Chat with us on Matrix")
                    }
                }
                div(fomantic.ui.item) {
                    p().text("Fred")
                    div(fomantic.ui.bulleted.list) {
                        div(fomantic.ui.item).text("Follow @freenetproject")
                        div(fomantic.ui.item).text("IRC: #freenet on libera")
                    }
                }
            }

            h2(fomantic.ui.text).text("Support Us")
            div(fomantic.ui.bulleted.list) {
                div(fomantic.ui.item).text("Bitcoin: 3M3fbA7RDYdvYeaoR69cDCtVJqEodo9vth")
                div(fomantic.ui.item).text("Zcash: t1VHw1PHgzvMqEEd31ZBt3Vyy2UrG4J8utB")
                div(fomantic.ui.item).text("Ethereum: 0x79158A5Dbd9C0737CB27411817BD2759f5b9a9Ae")
                div(fomantic.ui.item).text("Paypal & others: link")
            }

        }




    }.setAttribute("background-color", "e8e8e8")
}