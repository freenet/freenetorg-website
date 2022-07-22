package xyz.derekbriggs

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.stripe.Stripe
import com.stripe.model.Customer
import com.stripe.model.PaymentIntent
import com.stripe.param.CustomerCreateParams
import com.stripe.param.PaymentIntentCreateParams
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
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
            Stripe.apiKey = "sk_test_51KYyh3HSi8gwBwE3syIyEQK1jd1HAJfWftPyWOspGL4cP0xfUz8RWfpHiRUGEjaIoKHBojzNvJQ6E7t3pb6E1l8l0032ZZFgK7"
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
