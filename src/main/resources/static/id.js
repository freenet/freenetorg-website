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
    console.log("stored stuff");
    //let modulus = generateModulus(4096);
    let hashedKey = hashPublicKey(kp.userECPublicKey);
    console.log("hashedKey done");
    console.log("blind generate start");
    let blindedKey = blind(hashedKey);
    //let unblindedKey = unblind(blindedKey)
    console.log("blinding done");
    let message = {
        "messageKey": "publicKey",
        "data": hashedKey,
        "blindedKey": blindedKey,
        "unblindedKey": unblindedKey
        //"blindedKey": blindedKey.blindedPublicKeyHash
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
    console.log("begin hashing");
    const md = forge.md.sha256.create();
    md.update(bytes);
    const hash = md.digest();
    console.log("hash done");

    // Convert the hash to a BigInteger
    const hashBigInt = new forge.jsbn.BigInteger(hash.toHex(), 16);
    console.log("hashBigInt done");

    return hashBigInt;
}

function blind(publicKeyHash) {
    console.log("blind start");
    const keypair = forge.pki.rsa.generateKeyPair({bits: 2048, e: 0x10001});
    console.log("blind keypair made");
    const modulus = keypair.publicKey.n;
    const exponent = keypair.publicKey.e;
    console.log("mod and exp done");

    // Convert publicKeyHash to forge BigInteger
    //let publicKeyHashBn = forge.jsbn.BigInteger(publicKeyHash);
    console.log("bigIntConversion")

    // Generate a random blinding factor
    const blindingFactor = forge.random.getBytesSync(24); // Generate random bytes
    console.log("blindingFactor generated")
    let blindingFactorBigInt = new forge.jsbn.BigInteger(forge.util.bytesToHex(blindingFactor), 16);
    console.log("blinding factor made");

    // Compute reModN = blindingFactor^exponent mod modulus
    let reModN = blindingFactorBigInt.modPow(exponent, modulus);
    console.log("reModN done");

    // Compute blindedHash = publicKeyHash * reModN mod modulus
    let blindedHash = publicKeyHash.multiply(reModN).mod(modulus);
    console.log("blind mulmod done");

    return blindedHash.toString(16); // Return as hexadecimal string
}

const forge = require('node-forge');

function unblind(blindedValue, blindingFactor, modulus) {
    // Convert inputs to forge BigIntegers
    blindedValue = new forge.jsbn.BigInteger(blindedValue, 16);
    blindingFactor = new forge.jsbn.BigInteger(blindingFactor, 16);

    // Compute the modular inverse of the blinding factor
    const blindingFactorInv = blindingFactor.modInverse(modulus);

    // Compute the unblinded value
    const unblindedValue = blindedValue.multiply(blindingFactorInv).mod(modulus);

    return unblindedValue.toString(16); // Return as hexadecimal string
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

/*function hashPublicKey(userECPublicKey) {
    let hasher = new sjcl.misc.hmac(userECPublicKey, sjcl.hash.sha256);
    return hasher.digest();
}*/



/*function hashPublicKey(userECPublicKey) {
    // Convert the public key to a bitArray (if it's not already)
    let bitArray;
    if (typeof userECPublicKey === 'string') {
        bitArray = sjcl.codec.utf8String.toBits(userECPublicKey);
    } else {
        bitArray = userECPublicKey;  // Assuming it's already a bitArray
    }

    // Compute the SHA-256 hash
    console.log("begin hashing")
    let hashBitArray = sjcl.hash.sha256.hash(bitArray);
    console.log("hashBitArray done")

    let hashBn = sjcl.bn.fromBits(hashBitArray)
    console.log("hashBN done")

    return hashBn;
}*/


// function blind(publicKeyHash, freenetRSAPublicKey) {
//     let blindingFactor = sjcl.bn.random(freenetRSAPublicKey, 10);
//     let blindedPublicKeyHash = publicKeyHash.mulmod(blindingFactor, freenetRSAPublicKey);
//
//     return {blindedPublicKeyHash: blindedPublicKeyHash, blindingFactor};
// }

/*function unblind(blindedSignature, blindingFactor, freenetRSAPublicKey) {
    return blindedSignature
        .mulmod(
            blindingFactor.inverseMod(freenetRSAPublicKey),
            freenetRSAPublicKey
        );
}*/

/*function blind(publicKeyHash) {
    console.log("blind function start");
    //const keypair = forge.pki.rsa.generateKeyPair({bits: 2048, e: 0x10001});
    console.log("blind function end");
    return "hola";
}*/

/*function blind(publicKeyHash) {
    console.log("blind start");
    const keypair = forge.pki.rsa.generateKeyPair({bits: 2048, e: 0x10001});
    console.log("blind keypair made")
    const modulus = keypair.publicKey.n;
    const exponent = keypair.publicKey.e;

    let blindingFactor = sjcl.bn.random(modulus);
    console.log("blinding factor made")

    let reModN = blindingFactor.powermod(exponent, modulus);
    console.log("reModN done")

    let blindedHash = publicKeyHash.mulmod(reModN, modulus)
    console.log("blind mulmod done")

    return blindedHash;
}*/

/*function blind(publicKeyHash, modulus) {
    if (typeof sjcl === 'undefined' || !sjcl.bn) {
        throw new Error('sjcl library or its bn module is not available');
    }

    // Ensure that publicKeyHash is an instance of sjcl.bn
    if (!(publicKeyHash instanceof sjcl.bn)) {
        throw new Error('publicKeyHash is not an instance of sjcl.bn');
    }

    // Generate a random blinding factor. Ensure it's less than the modulus
    let blindingFactor = new sjcl.bn.random(modulus);

    // Blind the publicKeyHash using modular multiplication
    let blindedPublicKeyHash = publicKeyHash.mulmod(blindingFactor, modulus);

    return {
        blindedPublicKeyHash: blindedPublicKeyHash,
        blindingFactor: blindingFactor
    };
}*/

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