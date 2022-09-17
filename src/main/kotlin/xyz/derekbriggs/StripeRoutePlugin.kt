package xyz.derekbriggs

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.stripe.Stripe
import com.stripe.model.Customer
import com.stripe.model.PaymentIntent
import com.stripe.param.CustomerCreateParams
import com.stripe.param.PaymentIntentCreateParams
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.*
import kweb.plugins.KwebPlugin

class StripeRoutePlugin : KwebPlugin() {

     data class UserInfo (
         val username : String,
         val email : String,
         val donationAmount : Long,
     )

    internal class CreatePaymentResponse(private val clientSecret: String)

    override fun appServerConfigurator(routeHandler: Routing) {
        routeHandler.post("/create-payment-intent") {
            Stripe.apiKey = System.getenv("STRIPE_SECRET_KEY")
            val gson = Gson()
            val requestBody = call.receiveText()
            val postBody : UserInfo = gson.fromJson(requestBody, UserInfo::class.java)
            println(postBody)
            val customerParams = CustomerCreateParams.builder().setEmail(postBody.email).build()
            val customer = Customer.create(customerParams)
            val params : PaymentIntentCreateParams = PaymentIntentCreateParams.builder()
                .setAmount(postBody.donationAmount * 100)
                .setCustomer(customer.id)
                .setReceiptEmail(customer.email)
                .setDescription("Reservation for ${postBody.username}")
                .setCurrency("USD")
                .putMetadata("username", postBody.username)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods
                        .builder()
                        .setEnabled(true)
                        .build()
                ).build()

            val paymentIntent = PaymentIntent.create(params)

            val paymentResponse = CreatePaymentResponse(paymentIntent.clientSecret)
            call.respond(gson.toJson(paymentResponse))
        }
        super.appServerConfigurator(routeHandler)
    }
}
