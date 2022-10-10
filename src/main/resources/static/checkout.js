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

///This key can be publicly-accessible and does not need to be hidden.
const stripe = Stripe("pk_test_51KVKDLAIWUhNLMbdcgFrq0f8t5Rm07SEt6ZqXn46fZ7AQbz3mxoMF2cFvW7qRfI3kZN6NyPG95nzN7CrcHBHRWGD00VAifbJEy");

let elements;

checkStatus();


// Fetches a payment intent and captures the client secret
async function initialize() {
    const userInfo = {};
    userInfo["username"] = document.querySelector("#usernameInput").value;
    userInfo["email"] = document.querySelector("#emailInput").value;
    let selectedDonationRadioButton = document.querySelector('input[name="donationPresetRadio"]:checked').id;
    if (selectedDonationRadioButton === "donationRadioCustomField") {
        userInfo["donationAmount"] = document.querySelector("#donationInput").value;
    } else {
        userInfo["donationAmount"] = document.querySelector('input[name="donationPresetRadio"]:checked').value;
    }
    const response = await fetch("/create-payment-intent", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify( userInfo ),
    });
    const { clientSecret } = await response.json();

    const appearance = {
        theme: 'stripe',
    };
    elements = stripe.elements({ appearance, clientSecret });

    const paymentElement = elements.create("payment");
    console.log("Attempting to mount paymentElement")
    paymentElement.mount("#payment-element");
    document
        .querySelector("#submit")
        .addEventListener("click", handleSubmit);
}

async function handleSubmit(e) {
    console.log("Handle Submit")
    e.preventDefault();
    setLoading(true);

    const { error } = await stripe.confirmPayment({
        elements,
        confirmParams: {
            // Make sure to change this to your payment completion page
            return_url: "https://34.74.186.232/success",
        },
    });

    // This point will only be reached if there is an immediate error when
    // confirming the payment. Otherwise, your customer will be redirected to
    // your `return_url`. For some payment methods like iDEAL, your customer will
    // be redirected to an intermediate site first to authorize the payment, then
    // redirected to the `return_url`.
    if (error.type === "card_error" || error.type === "validation_error") {
        showMessage(error.message);
    } else {
        showMessage("An unexpected error occured.");
    }

    setLoading(false);
}

// Fetches the payment intent status after payment submission
async function checkStatus() {
    const clientSecret = new URLSearchParams(window.location.search).get(
        "payment_intent_client_secret"
    );

    if (!clientSecret) {
        return;
    }

    const { paymentIntent } = await stripe.retrievePaymentIntent(clientSecret);

    switch (paymentIntent.status) {
        case "succeeded":
            showMessage("Payment succeeded!");
            break;
        case "processing":
            showMessage("Your payment is processing.");
            break;
        case "requires_payment_method":
            showMessage("Your payment was not successful, please try again.");
            break;
        default:
            showMessage("Something went wrong.");
            break;
    }
}

// ------- UI helpers -------

function showMessage(messageText) {
    const messageContainer = document.querySelector("#payment-message");

    messageContainer.classList.remove("hidden");
    messageContainer.textContent = messageText;

    setTimeout(function () {
        messageContainer.classList.add("hidden");
        messageText.textContent = "";
    }, 4000);
}

// Show a spinner on payment submission
function setLoading(isLoading) {
    if (isLoading) {
        console.log("setLoading")
        // Disable the button and show a spinner
        document.querySelector("#submit").disabled = true;
        document.querySelector("#spinner").classList.remove("hidden");
        document.querySelector("#button-text").classList.add("hidden");
    } else {
        document.querySelector("#submit").disabled = false;
        document.querySelector("#spinner").classList.add("hidden");
        document.querySelector("#button-text").classList.remove("hidden");
    }
}