import kweb.*

fun main() {
    Kweb(port = 1234) {
        doc.body {
            h1().text("Hola Mundo!!")
        }
    }
}