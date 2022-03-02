import kweb.*
import kweb.plugins.fomanticUI.fomantic
import kweb.plugins.fomanticUI.fomanticUIPlugin

fun main() {
    Kweb(port = 1234, plugins = listOf(fomanticUIPlugin)) {
        doc.body() {
            div(fomantic.ui.large.top.fixed.menu.transition.hidden) {
                div(fomantic.ui.container) {
                    a(fomantic.item).text("Home")
                    a(fomantic.item).text("About")
                }
            }

            div(fomantic.ui.vertical.inverted.sidebar.menu.left) { //sidebar for mobile screens
                a(fomantic.item).text("Home")
                a(fomantic.item).text("About")
            }

            div(fomantic.pusher) {
                div(fomantic.ui.inverted.vertical.masthead.center.aligned.segment) {
                    div(fomantic.ui.container) {
                        div(fomantic.ui.large.secondary.inverted.pointing.menu) {
                            a(fomantic.item).text("Home")
                            a(fomantic.item).text("About")
                        }
                    }
                    div(fomantic.ui.text.container) {
                        h1(fomantic.ui.inverted.header).setAttribute("style", """margin-top: 3em; font-size: 4em;""").text("Locutus")
                        h2().text("Do cool things")
                    }

                    div(fomantic.ui.container) {
                        div(fomantic.ui.input) {
                            val input = input(type = InputType.text, placeholder = "Username")
                        }
                        br()
                        button(fomantic.ui.button).text("Reserve").apply {

                        }
                    }
                }.setAttribute("style", """min-height: 700px;""")
            }
        }.classes("pushable")
    }
}