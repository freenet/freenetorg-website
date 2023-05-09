package org.freenet.website.pages.joinUs

import kweb.components.Component
import org.bouncycastle.crypto.engines.RSAEngine
import org.bouncycastle.crypto.params.RSAKeyParameters
import java.math.BigInteger
import java.util.*

fun Component.idComponent() {
    
}

fun main() {
    // Load private key (replace with actual private key values)
    val modulus = BigInteger("...") // N
    val privateExponent = BigInteger("...") // d
    val privateKey = RSAKeyParameters(true, modulus, privateExponent)

    // Receive blinded message from JavaScript (base64 encoded)
    val blindedMessageBase64 = readLine() ?: ""
    val blindedMessageBytes = Base64.getDecoder().decode(blindedMessageBase64)

    // Sign the blinded message
    val engine = RSAEngine()
    engine.init(true, privateKey)
    val signatureOnConcealedMessage = engine.processBlock(blindedMessageBytes, 0, blindedMessageBytes.size)

    // Send the signature back to JavaScript (base64 encoded)
    println(Base64.getEncoder().encodeToString(signatureOnConcealedMessage))
}