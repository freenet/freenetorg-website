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

    companion object CreatePayment {
        @SerializedName("items")
        var items: Array<Any> = emptyArray()
    }

    internal class CreatePaymentResponse(private val clientSecret: String)

    override fun appServerConfigurator(routeHandler: Routing) {
        routeHandler.post("/create-payment-intent") {
            Stripe.apiKey = "sk_test_51KYyh3HSi8gwBwE3syIyEQK1jd1HAJfWftPyWOspGL4cP0xfUz8RWfpHiRUGEjaIoKHBojzNvJQ6E7t3pb6E1l8l0032ZZFgK7"
            val customerParams = CustomerCreateParams.builder().setEmail(UserInfo.emailAddress).build()
            val customer = Customer.create(customerParams)
            val gson = Gson()
            //val requestBody = call.receiveText()
            //val postBody : CreatePayment = gson.fromJson(requestBody, CreatePayment::class.java)
            val params : PaymentIntentCreateParams = PaymentIntentCreateParams.builder()
                .setAmount(UserInfo.donationAmount)
                .setCustomer(customer.id)
                .setCurrency("USD")
                .putMetadata("username", UserInfo.usernameToReserve)
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
