package org.freenet.website.util

import org.freenet.website.pages.claimId.TierLevel
import java.security.*


fun main() {
    val eccKeyPair = generateECCKeyPair()

    enumValues<TierLevel>().forEach { currentTier ->
        //TODO write this signed rsaKeypair to json bronze, silver, gold.
        val rsaKeyPair = generateRSAKeyPair()

        val signature = signWithECCPrivateKey(rsaKeyPair.public, eccKeyPair.private)

        val isSignatureValid = verifySignature(rsaKeyPair.public, eccKeyPair.public, signature)
    }




}

fun generateECCKeyPair() : KeyPair {
    val keyPairGenerator = KeyPairGenerator.getInstance("EC")
    //keypair can be customized using ECGenParameterSpec
    return keyPairGenerator.generateKeyPair()
}

fun generateRSAKeyPair() : KeyPair {
    val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
    return keyPairGenerator.generateKeyPair()
}


fun signWithECCPrivateKey(dataToSign: PublicKey, privateKey: PrivateKey) : ByteArray {
    val signature = Signature.getInstance("SHA256withECDSA");
    signature.initSign(privateKey)
    signature.update(dataToSign.encoded)
    return signature.sign()
}

fun verifySignature(originalData: PublicKey, publicKey: PublicKey, signature: ByteArray) : Boolean {
    val verifier = Signature.getInstance("SHA256withECDSA")
    verifier.initVerify(publicKey)
    verifier.update(originalData.encoded)
    return verifier.verify(signature)
}