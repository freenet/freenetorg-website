let rsaPublicKey = "-----BEGIN RSA PUBLIC KEY-----"
    + "MEgCQQCo9+BpMRYQ/dL3DS2CyJxRF+j6ctbT3/Qp84+KeFhnii7NT7fELilKUSnx"
    + "S30WAvQCCo2yU1orfgqr41mM70MBAgMBAAE="
    + "-----END RSA PUBLIC KEY-----"

// Called when user presses "Generate Key" button
async function beginGeneration() {
    let kp = generateUserECCKeyPair();
    let kpBase64 = publicKeyToBase64(kp.userECPublicKey);
    console.log("User EC Public Key: " + kpBase64);
    let qrcode = new QRCode(document.getElementsByClassName("qrcode")[0], {
        text: kpBase64,
    });
    await generateAndStoreKeys(kp);
}

function generateUserKey() {
    let kp = generateUserECCKeyPair();
    let kpBase64 = publicKeyToBase64(kp.userECPublicKey);
    localStorage.setItem("userECPublicKey", kp.userECPublicKey);
    localStorage.setItem("userECPrivateKey", kp.userECPrivateKey);
    let message = {
        "messageKey": "publicKey",
        "data": kpBase64
    }
    sendMessage(message)
}




async function generateAndStoreKeys(kp) {
    let publicKeyHash = hashPublicKey(kp.userECPublicKey);
    // Assume freenetRSAPublicKey is available
    let {blindedPublicKeyHash, blindingFactor} = blind(publicKeyHash, freenetRSAPublicKey);
    let donationSuccessful = await makeDonation(blindedPublicKeyHash);
    if (donationSuccessful) {
        // Assume we receive blindedSignature from the server after donation
        let unblindedSignature = unblind(blindedSignature, blindingFactor, freenetRSAPublicKey);
        // Assume freenetRSACertPEM is available
        let userECPublicKeyCert = createUserECPublicKeyCertificate(unblindedSignature, freenetRSACertPEM);
        // Store the keys and certificate in localStorage
        localStorage.setItem("userECPublicKey", kp.userECPublicKey);
        localStorage.setItem("userECPrivateKey", kp.userECPrivateKey);
        localStorage.setItem("userECPublicKeyCert", userECPublicKeyCert);
        //potentially clear local storage on completed donation
    }
}

// Stub function to represent the donation process.
// You need to implement this function with your actual donation process.
async function makeDonation(blindedPublicKeyHash) {
    // Send the blindedPublicKeyHash to the server, make a donation and get the blindedSignature
    // If the donation is successful, return true. Otherwise, return false.
    // For now, we're just simulating a delay and returning true.
    await new Promise(resolve => setTimeout(resolve, 2000));
    return true;
}

function generateUserECCKeyPair() {
    const curveName = "c256";
    let keys = new sjcl.ecc.ecdsa.generateKeys(sjcl.ecc.curves[curveName], 4);

    let pub = keys.pub.get();
    let sec = sjcl.codec.hex.fromBits(keys.sec.get());

    return {userECPublicKey: pub, userECPrivateKey: sec};
}

function hashPublicKey(userECPublicKey) {
    let hasher = new sjcl.misc.hmac(userECPublicKey, sjcl.hash.sha256);
    return hasher.digest();
}

function blind(publicKeyHash, freenetRSAPublicKey) {
    let blindingFactor = sjcl.bn.random(freenetRSAPublicKey, 10);
    let blindedPublicKeyHash = publicKeyHash.mulmod(blindingFactor, freenetRSAPublicKey);

    return {blindedPublicKeyHash: blindedPublicKeyHash, blindingFactor};
}

function unblind(blindedSignature, blindingFactor, freenetRSAPublicKey) {
    return blindedSignature
        .mulmod(
            blindingFactor.inverseMod(freenetRSAPublicKey),
            freenetRSAPublicKey
        );
}

function createUserECPublicKeyCertificate(unblindedSignature, freenetRSACertPEM) {
    let userECPublicKeyCert = "-----BEGIN CERTIFICATE-----\n";
    userECPublicKeyCert += "Unblinded Signature: " + unblindedSignature + "\n";
    userECPublicKeyCert += "Freenet RSA Certificate: " + freenetRSACertPEM + "\n";
    userECPublicKeyCert += "-----END CERTIFICATE-----\n";
    return userECPublicKeyCert;
}

function publicKeyToBase64(userECPublicKey) {
    try {
        // Convert the arrays to binary
        let xBuffer = new ArrayBuffer(userECPublicKey.x.length * 4);
        let xView = new DataView(xBuffer);
        userECPublicKey.x.forEach((value, index) => xView.setInt32(index * 4, value));

        let yBuffer = new ArrayBuffer(userECPublicKey.y.length * 4);
        let yView = new DataView(yBuffer);
        userECPublicKey.y.forEach((value, index) => yView.setInt32(index * 4, value));

        // Combine the two binary arrays
        let combined = new Uint8Array(xBuffer.byteLength + yBuffer.byteLength);
        combined.set(new Uint8Array(xBuffer), 0);
        combined.set(new Uint8Array(yBuffer), xBuffer.byteLength);

        // Convert to base64
        let publicKeyBase64 = btoa(String.fromCharCode.apply(null, combined));

        return publicKeyBase64;
    } catch (error) {
        console.error('Error encoding public key to base64:', error);
        return null;
    }
}

function base64ToPublicKey(publicKeyBase64) {
    try {
        // Convert from base64 to binary
        let combined = new Uint8Array([...atob(publicKeyBase64)].map(c => c.charCodeAt(0)));

        // Check that the combined length is a multiple of 8 bytes (2 * 4 bytes)
        if (combined.length % 8 !== 0) {
            throw new Error('Invalid base64 public key');
        }

        // Split the combined binary data back into two arrays
        let arrayLength = combined.length / 2;
        let xBinary = combined.slice(0, arrayLength);
        let yBinary = combined.slice(arrayLength);

        // Convert the binary arrays to integer arrays
        let x = [];
        let y = [];
        let xView = new DataView(xBinary.buffer);
        let yView = new DataView(yBinary.buffer);

        for (let i = 0; i < arrayLength / 4; i++) {
            x.push(xView.getInt32(i * 4));
            y.push(yView.getInt32(i * 4));
        }

        return { x: x, y: y };
    } catch (error) {
        console.error('Error decoding base64 to public key:', error);
        return null;
    }
}


/*
let userECKeyPair = generateUserECCKeyPair();
let userECPublicKeyHash = hashPublicKey(userECKeyPair.userECPublicKey);

// Assuming freenetRSAPublicKey is available and you have the blindedSignature
let blindedPublicKeyHashData = blind(userECPublicKeyHash, freenetRSAPublicKey);
let unblindedSignature = unblind(blindedSignature, blindedPublicKeyHashData.blindingFactor, freenetRSAPublicKey);

// Assuming you have the freenetRSACertPEM
let userECPublicKeyCert = createUserECPublicKeyCertificate(unblindedSignature, freenetRSACertPEM);

// Now you can store the certificate
localStorage.setItem("userECPublicKeyCert", userECPublicKeyCert);
 */
window.beginGeneration = beginGeneration;