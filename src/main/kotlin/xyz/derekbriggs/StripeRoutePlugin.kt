package xyz.derekbriggs

import com.stripe.model.checkout.Session
import com.stripe.param.InvoiceItemCreateParams
import com.stripe.param.checkout.SessionCreateParams
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kweb.plugins.KwebPlugin

class StripeRoutePlugin : KwebPlugin() {

    override fun appServerConfigurator(routeHandler: Routing) {
        println("appserver was configurated")
        routeHandler.get("/failing") {
            call.respondText("Failing")
        }
        routeHandler.get("/checkout/") {
            val YOUR_DOMAIN = "http://0.0.0.0:1234"
            val sessionParams : SessionCreateParams =
                SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(YOUR_DOMAIN + "/success")
                    .setCancelUrl(YOUR_DOMAIN + "/cancel")
                    .addLineItem(
                        SessionCreateParams.LineItem.builder()
                            .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                    .setProduct("donation_id")
                                    .setUnitAmount(5000L)
                                    .setCurrency("usd")
                                    .build()
                            )
                            .setQuantity(1L)
                            .build()
                    )
                    .build()
            val session : Session = Session.create(sessionParams)
            call.respondRedirect(session.url)
        }
        /*routeHandler.post("/create-checkout-session") {
            println("routehandler Ran")
            val YOUR_DOMAIN = "http://0.0.0.0:1234"
            val sessionParams : SessionCreateParams =
                SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(YOUR_DOMAIN + "/success")
                    .setCancelUrl(YOUR_DOMAIN + "/cancel")
                    .addLineItem(
                        SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPrice("donation_price_2")
                            .build()
                    )
                    .build()
            val session : Session = Session.create(sessionParams)
            call.respondRedirect(session.url)
            //call.response
        }*/
        super.appServerConfigurator(routeHandler)
    }
}
