// This is your test publishable API key.
const stripe = Stripe("pk_test_51KYyh3HSi8gwBwE3JwrtL8jGGnFpcqZfewOtlQFXHYkItitRf6fNgCAUKPVsHou8tbkGxbAXm03L9ZvVPtv8JOEd00y9t7YCWm");

let elements;

checkStatus();


// Fetches a payment intent and captures the client secret
async function initialize() {
    const userInfo = {};
    userInfo["username"] = document.querySelector("#usernameInput").value;
    userInfo["email"] = document.querySelector("#emailInput").value;
    //userInfo["donationAmount"] = document.querySelector("#donationAmountValue").value;
    let donationSelection = document.getElementById("donationSelection");
    userInfo["donationAmount"] = donationSelection.options[donationSelection.selectedIndex].text;
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
            return_url: "http://localhost:8080/success",
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