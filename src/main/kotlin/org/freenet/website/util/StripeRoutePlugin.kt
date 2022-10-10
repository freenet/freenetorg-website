/*
 *         Freenet.org - web application
 *         Copyright (C) 2022  Freenet Project Inc
 *
 *         This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as
 *         published by the Free Software Foundation, either version 3 of the
 *         License, or (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package org.freenet.website.util

import com.google.gson.Gson
import com.stripe.Stripe
import com.stripe.model.Customer
import com.stripe.model.PaymentIntent
import com.stripe.param.CustomerCreateParams
import com.stripe.param.PaymentIntentCreateParams
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
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
            //TODO this key should probably get set somewhere else.
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
