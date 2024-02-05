// Called when user presses "Generate Key" button
function generateUserKey() {//this should be renamed something like beginGeneration. called in claimIdPage
    let kp = generateUserECCKeyPair(); //todo find out what bit length Forge uses for ECC keypairs
    //let kpBase64 = publicKeyToBase64(kp.userECPublicKey);
    localStorage.setItem("userECPublicKey", kp.userECPublicKey);
    localStorage.setItem("userECPrivateKey", kp.userECPrivateKey);

    //TODO, update potential contributor that their machine has generated an ECC keypair. Display the keypair?
}

//TODO, consider user ECC key generation with Forge instead of SJCL. Forge supports the blind signature album, while SJCL does not.
function generateUserECCKeyPair() {
    const curveName = "c256";
    let keys = new sjcl.ecc.ecdsa.generateKeys(sjcl.ecc.curves[curveName], 4);

    let pub = keys.pub.get();
    let sec = sjcl.codec.hex.fromBits(keys.sec.get());

    return {userECPublicKey: pub, userECPrivateKey: sec};
}

//TODO should be called in claimIdPage, probably when stripe payment is initiated, or on the payment success page
function blindUserKey(freenetKey) {
    let hashedKey = hashPublicKey(kp.userECPublicKey);
    let blindedKey = blind(hashedKey, freenetKey);
    let message = {
        "messageKey": "publicKey",
        "blindedKey": blindedKey,
    };
    sendMessage(message);
}

function hashPublicKey(userECPublicKey) {
    // Convert the public key to bytes (if it's not already)
    let bytes;
    if (typeof userECPublicKey === 'string') {
        bytes = forge.util.encodeUtf8(userECPublicKey);
    } else {
        bytes = userECPublicKey; // Assuming it's already bytes
    }

    // Compute the SHA-256 hash
    const md = forge.md.sha256.create();
    md.update(bytes);
    const hash = md.digest();

    // Convert the hash to a BigInteger
    const hashBigInt = new forge.jsbn.BigInteger(hash.toHex(), 16);

    return hashBigInt;
}

function blind(publicKeyHash, freenetKey) {//publicKeyHash is the hashed form of the user's public key generated on the client side.
    //TODO this function should load the public key from the bronze/silver/gold freenet keypair, instead of generating one.
    //This is important to being able to sign/verify the signature.
    const keypair = forge.pki.rsa.generateKeyPair({bits: 2048, e: 0x10001});
    const modulus = keypair.publicKey.n;
    const exponent = keypair.publicKey.e;
    localStorage.setItem("blindModulus", modulus);


    // Generate a random blinding factor
    const blindingFactor = forge.random.getBytesSync(24); // Generate random bytes
    localStorage.setItem("blindingFactor", blindingFactor);
    let blindingFactorBigInt = new forge.jsbn.BigInteger(forge.util.bytesToHex(blindingFactor), 16);

    // Compute reModN = blindingFactor^exponent mod modulus
    let reModN = blindingFactorBigInt.modPow(exponent, modulus);

    // Compute blindedHash = publicKeyHash * reModN mod modulus
    let blindedHash = publicKeyHash.multiply(reModN).mod(modulus);

    return blindedHash.toString(16); // Return as hexadecimal string
}


function unblind(blindedValue) {
    const modulusString = localStorage.getItem("blindModulus");
    const modulus = new forge.jsbn.BigInteger(modulusString, 16);
    const blindingFactor = localStorage.getItem("blindingFactor");
    // Convert inputs to forge BigIntegers
    blindedValue = new forge.jsbn.BigInteger(blindedValue, 16);
    let blindingFactorBigInt = new forge.jsbn.BigInteger(forge.util.bytesToHex(blindingFactor), 16);


    // Compute the modular inverse of the blinding factor
    const blindingFactorInv = blindingFactorBigInt.modInverse(modulus);

    // Compute the unblinded value
    const unblindedValue = blindedValue.multiply(blindingFactorInv).mod(modulus);

    return unblindedValue.toString(16); // Return as hexadecimal string
}

//called by claimIdPage after stripe payment is successful
function createQRCode() {
    //TODO userKeyJson should be a JSON formatted string containing the user's contribution level, keypair, and the certificate
    //that shows the user's key was signed by an authentic freenet key.
    var userKeyJson = ""
    var qrCode = new QRCode(document.getElementById("qrCodeBox"), userKeyJson);
}


//TODO
//The code below this point is not used. I left it here because I may use some of it.

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
// Assuming you have the freenetRSACertPEM
let userECPublicKeyCert = createUserECPublicKeyCertificate(unblindedSignature, freenetRSACertPEM);

// Now you can store the certificate
localStorage.setItem("userECPublicKeyCert", userECPublicKeyCert);
 */
window.beginGeneration = beginGeneration;