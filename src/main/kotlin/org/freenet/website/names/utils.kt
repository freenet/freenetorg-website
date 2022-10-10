package org.freenet.website.names

import com.stripe.model.Customer
import io.ktor.util.date.*
import kweb.WebBrowser
import kweb.util.json
import org.freenet.website.db.db
import org.freenet.website.timeToReserveName
import org.freenet.website.usernameTableName

fun getMinimumDonationAmount(username: String) : Long{
    return when(username.length) {
        2 -> 200
        3 -> 150
        4 -> 100
        5 -> 80
        6 -> 60
        7 -> 45
        else -> 20
    }
}

fun tempReserveName(username: String,  ipAddress: String, referer: String? = null) {
    if (db == null)
        error("No database available because site is running in offline mode for development")

    val docRef = db.collection(usernameTableName).document(username)
    val data = HashMap<String, Any>()

    data["lowercaseUsername"] = username.lowercase()
    data["timeReserved"] = getTimeMillis()
    data["ipAddress"] = ""
    referer?.let {
        data["referer"] = it
    }
    docRef.set(data)
}

fun isUsernameValid(username: String) : Boolean {
    //allows a 2-30 character alphanumeric username that may contain an underscore or period
    val usernameRegex = "^[A-Za-z][A-Za-z0-9_.]{1,29}\$"
    val isValid = username.matches(usernameRegex.toRegex())
    return isValid
}

fun isValidEmail(email : String) : Boolean {
    val emailRegex ="""^([a-zA-Z0-9_\-.]+)@([a-zA-Z0-9 \-.]+)\.([a-zA-Z]{2,5})${'$'}"""
    val isValid = email.matches(emailRegex.toRegex())
    return isValid
}

fun isUsernameAvailable(username: String) : Boolean {
    if (db == null) {
        error("No database available because site is running in offline mode for development")
    }
    val usernameCollection = db.collection(usernameTableName)
    val query = usernameCollection.whereEqualTo("lowercaseUsername", username.lowercase())
    val querySnapshot = query.get()


    for (document in querySnapshot.get().documents) {
        //if the username in this document was reserved more than 'timeToReserveName' ago, and has a null transactionId, it is available
        println(document.toString())
        return ((document.get("Stripe_transaction_id") == null) && getTimeMillis() - document.get("timeReserved") as Long > timeToReserveName)

    }

    //the usernameToReserve does not exist in the database, and is available
    return true
}


//Called to finalize reserving a username, via a Stripe payment
fun saveStripePaymentDetails(stripePayIntentJson: String, email: String, username: String, donationAmount: Long, transactionId: String) {
    if (db == null) {
        error("No database available because site is running in offline mode for development")
    }

    val docRef = db.collection(usernameTableName).document(username)
    val data = HashMap<String, Any>()

    data["stripePayIntent"] = stripePayIntentJson
    data["email"] = email
    data["donationAmount"] = donationAmount
    data["Stripe_transaction_id"] = transactionId
    data["paymentMethod"] = "Stripe"

    val result = docRef.update(data)
}

fun saveCustomer(stripeCustomerId: String) : String{
    if (db == null) {
        error("No database available because site is running in offline mode for development")
    }

    val customer = Customer.retrieve(stripeCustomerId)
    val docRef = db.collection("Customers").document(customer.email)
    val data = HashMap<String, Any>()

    data["stripeCustomerId"] = stripeCustomerId
    docRef.set(data)
    return customer.email
}

fun showToast(webBrowser: WebBrowser, message: String) {
    webBrowser.callJsFunction(""" 
        Toastify({
            text: {},
            duration: 5000,
            close: false,
            gravity: "bottom",
            position: "center",
            stopOnFocus: false,
            style: {
                color: "#ff0033",
                background: "white"
            }
        }).showToast();
    """.trimIndent(), message.json)
}